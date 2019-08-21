// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback

import android.Manifest
import android.Manifest.permission.MEDIA_CONTENT_CONTROL
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.XmlResourceParser
import android.os.Process
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.XmlRes
import androidx.media.MediaBrowserServiceCompat
import com.jadebyte.jadeplayer.main.common.data.Constants
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import java.io.IOException
import java.security.MessageDigest


/**
 * Created by Wilberforce on 2019-08-19 at 13:40.
 *
 * Validates that calling package is authorized to browse a [MediaBrowserServiceCompat].
 *
 * The list of allowed signing certificates and their corresponding package names is defined in
 * res/allowed_media_browser_callers.xml
 */
class PackageValidator(context: Context, @XmlRes xmlResId: Int) {

    private val context: Context = context.applicationContext
    private val packageManager: PackageManager = this.context.packageManager

    private val certificateWhitelist: Map<String, KnownCallerInfo> =
        buildCertificateWhiteList(context.resources.getXml(xmlResId))

    private val platformSignature: String = getSystemSignature()
    private val callerChecked = mutableMapOf<String, Pair<Int, Boolean>>()


    /**
     * Checks whether the caller attempting to connect to a [MediaBrowserServiceCompat] is known.
     * See [PlaybackService.onGetRoot] for where this is utilized.
     *
     * @param callingPackage The package name of the caller.
     * @param callingUid The user id of the caller.
     * @return `true` if the caller is known, `false` otherwise.
     */
    fun isKnownCaller(callingPackage: String, callingUid: Int): Boolean {
        // If the caller has already been checked, return the previous result here.
        val (checkedUid, checkResult) = callerChecked[callingPackage] ?: Pair(0, false)
        if (checkedUid == callingUid) {
            return checkResult
        }


        /**
         * Because some of these checks can be slow, we save the result in [callerChecked] after this code is run.
         *
         * In particular, there's little reason to compute the calling package's certificate sigature (SHA-256) each
         * call.
         *
         * This is safe to do as we know the UID matches the package's UID (from the check above), and app's UIDs are
         * set at install time. Additionally, a package name + UID is guaranteed to be constant until a reboot.
         * (After a reboot, then a previously assigned UID could be reassigned.)
         */
        /**
         * Because some of these checks can be slow, we save the result in [callerChecked] after this code is run.
         *
         * In particular, there's little reason to compute the calling package's certificate sigature (SHA-256) each
         * call.
         *
         * This is safe to do as we know the UID matches the package's UID (from the check above), and app's UIDs are
         * set at install time. Additionally, a package name + UID is guaranteed to be constant until a reboot.
         * (After a reboot, then a previously assigned UID could be reassigned.)
         */


        // Build the caller info for the rest of the checks here
        val callerPackageInfo = buildCallerInfo(callingPackage) ?: return false

        // Verify that things aren't broken. (Thus test should always pass)
        if (callerPackageInfo.uid != callingUid) return false

        val callerSignature = callerPackageInfo.signature
        val isPackageInWhitelist =
            certificateWhitelist[callingPackage]?.signatures?.first { it.signature == callerSignature } != null

        val isCallerKnown = when {
            // If it's our own app making the call, allow it.
            callingUid == Process.myUid() -> true
            // If it's one of the apps on the whitelist, allow it.
            isPackageInWhitelist -> true
            // If the system is making the call, allow it.
            callingUid == Process.SYSTEM_UID -> true
            // If the app was signed by the same certificate as the platform itself, also allow it.
            callerSignature == platformSignature -> true
            /**
             * [MEDIA_CONTENT_CONTROL] permission is only available to system applications, and
             * while it isn't required to allow these apps to connect to a
             * [MediaBrowserServiceCompat], allowing this ensures optimal compatability with apps
             * such as Android TV and the Google Assistant.
             */
            callerPackageInfo.permissions.contains(MEDIA_CONTENT_CONTROL) -> true
            /**
             * This last permission can be specifically granted to apps, and, in addition to
             * allowing them to retrieve notifications, it also allows them to connect to an
             * active [MediaSessionCompat].
             * As with the above, it's not required to allow apps holding this permission to
             * connect to your [MediaBrowserServiceCompat], but it does allow easy comparability
             * with apps such as Wear OS.
             */
            callerPackageInfo.permissions.contains(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE) -> true
            // If none of the pervious checks succeeded, then the caller is unrecognized.
            else -> false
        }

        if (!isCallerKnown) logUnknownCaller(callerPackageInfo)

        // Save our work for next time
        callerChecked[callingPackage] = Pair(callingUid, isCallerKnown)
        return isCallerKnown
    }

    // Logs an info level message with details of how to add a caller to the allowed callers list when app is debuggable
    private fun logUnknownCaller(callerPackageInfo: CallerPackageInfo) {
        if (callerPackageInfo.signature != null) {
            Timber.i("Package ${callerPackageInfo.name} with package name ${callerPackageInfo.packageName} and signature ${callerPackageInfo.signature} is unknown")
        }
    }

    /**
     * Builds a [CallerPackageInfo] for a given package that can be used for all the various checks that are performed
     * before allowing an app to connect ti a [MediaBrowserServiceCompat]
     */
    private fun buildCallerInfo(callingPackage: String): CallerPackageInfo? {
        val packageInfo = getPackageInfo(callingPackage) ?: return null
        val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
        val uid = packageInfo.applicationInfo.uid
        val signature = getSignature(packageInfo)

        val requestedPermissions = packageInfo.requestedPermissions
        val permissionFlags = packageInfo.requestedPermissionsFlags
        val activePermissions = mutableListOf<String>()
        requestedPermissions?.forEachIndexed { i, s ->
            if (permissionFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED != 0) {
                activePermissions += s
            }
        }
        return CallerPackageInfo(appName, callingPackage, uid, signature, activePermissions.toSet())
    }

    // Finds the Android platform signing key signature. This key is never null.
    private fun getSystemSignature(): String {
        return getPackageInfo(ANDROID_PLATFORM)?.let {
            getSignature(it)
        } ?: throw IllegalStateException("Platform signature not found")
    }

    @Suppress("DEPRECATION")
    private fun getSignature(packageInfo: PackageInfo): String? {
        return if (packageInfo.signatures == null || packageInfo.signatures.size != 1) {
            // Security best practices dictate that an app should be signed with exactly one (1)
            // signature. Because of this, if there are multiple signatures, reject it.
            null
        } else {
            val certificate = packageInfo.signatures[0].toByteArray()
            getSignatureSha256(certificate)
        }
    }

    // Creates a SHA-256 signature from the given byte array
    private fun getSignatureSha256(certificate: ByteArray): String {
        val md: MessageDigest = MessageDigest.getInstance("SHA256")
        md.update(certificate)

        // This code takes the byte array generated by `md.digest()` and joins each of the bytes
        // to a string, applying the string format `%02x` on each digit before it's appended, with
        // a colon (':') between each of the items.
        // For example: input=[0,2,4,6,8,10,12], output="00:02:04:06:08:0a:0c"
        return md.digest().joinToString(":") { String.format("%02x", it) }
    }

    // Looks up the [PackageInfo] for a package name.
    // This requests both the signatures (for checking if an app is on the whitelist) and
    // the app's permissions, which allow for more flexibility in the whitelist.
    @Suppress("DEPRECATION")
    @SuppressLint("PackageManagerGetSignatures")
    private fun getPackageInfo(callingPackage: String): PackageInfo? {
        return packageManager.getPackageInfo(
            callingPackage,
            PackageManager.GET_SIGNATURES or PackageManager.GET_PERMISSIONS
        )
    }


    private fun buildCertificateWhiteList(parser: XmlResourceParser): Map<String, KnownCallerInfo> {
        val certificates = LinkedHashMap<String, KnownCallerInfo>()
        try {
            var eventType = parser.next()
            while (eventType != XmlResourceParser.END_DOCUMENT) {

                if (eventType == XmlResourceParser.START_TAG) {

                    parseTag(parser)?.let {

                        val packageName = it.packageName
                        val existingCallerInfo = certificates[packageName]
                        if (existingCallerInfo != null) {
                            existingCallerInfo.signatures += it.signatures
                        } else {
                            certificates[packageName] = it
                        }
                    }

                }
                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            Timber.e(e, "buildCertificateWhiteList: Could not read allowed callers from XML")
        } catch (e: IOException) {
            Timber.e(e, "buildCertificateWhiteList: Could not read allowed callers from XML")
        }
        return certificates
    }

    private fun parseTag(parser: XmlResourceParser): KnownCallerInfo? {
        if (parser.name != "signature") return null
        val name = parser.getAttributeValue(null, "name")
        val packageName = parser.getAttributeValue(null, "package")

        val callerSignatures = mutableSetOf<KnownSignature>()
        var eventType = parser.next()
        while (eventType != XmlResourceParser.END_TAG) {
            val isRelease = parser.getAttributeBooleanValue(null, "release", false)
            val signature = parser.nextText().replace(Constants.WHITESPACE_REGEX, "").toLowerCase()
            callerSignatures += KnownSignature(signature, isRelease)
            eventType = parser.next()
        }
        return KnownCallerInfo(name, packageName, callerSignatures)
    }
}


private data class KnownCallerInfo(
    internal val name: String,
    internal val packageName: String,
    internal val signatures: MutableSet<KnownSignature>
)

private data class KnownSignature(
    internal val signature: String,
    internal val release: Boolean
)

private data class CallerPackageInfo(
    internal val name: String,
    internal val packageName: String,
    internal val uid: Int,
    internal val signature: String?,
    internal val permissions: Set<String>
)

private const val ANDROID_PLATFORM = "android"
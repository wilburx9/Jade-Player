// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.getStarted

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.BaseActivity
import com.jadebyte.jadeplayer.main.MainActivity
import kotlinx.android.synthetic.main.activity_get_started.*


class GetStartedActivity : BaseActivity() {

    private val permissionRequestExternalStorage = 0
    private val storagePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)
        getStarted.setOnClickListener { handleGetStartedClick() }
    }


    private fun handleGetStartedClick() {
        if (isPermissionGranted(storagePermission)) {
            startMainActivity()
        } else
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    storagePermission
                )
            ) {
                // The user has denied the permission and selected the "Don't ask again"
                // option in the permission request dialog
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(storagePermission),
                    permissionRequestExternalStorage
                )
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == permissionRequestExternalStorage) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission request was granted
                startMainActivity()
            } else {
                // Permission request was denied.
                infoText.visibility = View.VISIBLE
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    public override fun onRestart() {
        if (isPermissionGranted(storagePermission)) {
            startMainActivity()
        }
        super.onRestart()
    }


}

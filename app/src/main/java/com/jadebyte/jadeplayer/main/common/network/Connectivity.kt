/*
 * Copyright (c) 2017 Emil Davtyan
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

@file:Suppress("DEPRECATION")

package com.jadebyte.jadeplayer.main.common.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager

/**
 * Created by Wilberforce on 19/04/2019 at 20:57.
 * Check device's network connectivity and speed
 * Original @author emil http://stackoverflow.com/users/220710/emil
 * Original source: https://gist.github.com/emil2k/5130324
 */
object Connectivity {

    /**
     * Get the network info
     * @param context Must be an application context
     * @return [ConnectivityManager.getActiveNetworkInfo]
     */
    private fun getNetworkInfo(context: Context): NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    /**
     * Check if there is any connectivity
     * @param context Must be an application context
     * @return true if connected to an internet connnection. False otherwise
     */
    fun isConnected(context: Context): Boolean {
        val info = Connectivity.getNetworkInfo(context)
        return info != null && info.isConnected
    }

    /**
     * Check if there is any connectivity to a Wifi network
     * @param context Must be an application context
     * @returnn true if connected to WIFI. False otherwise
     */
    fun isConnectedWifi(context: Context): Boolean {
        val info = Connectivity.getNetworkInfo(context)
        return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_WIFI
    }

    /**
     * Check if there is any connectivity to a mobile network
     * @param context Must be an application context
     * @return true if connected to an internet connection. False otherwise
     */
    fun isConnectedMobile(context: Context): Boolean {
        val info = Connectivity.getNetworkInfo(context)
        return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_MOBILE
    }

    /**
     * Check if there is fast connectivity
     * @param context Must be an application context
     * @return true if the connection is fast. False otherwise
     */
    fun isConnectedFast(context: Context): Boolean {
        val info = Connectivity.getNetworkInfo(context)
        return info != null && info.isConnected && Connectivity.isConnectionFast(info.type, info.subtype)
    }

    /**
     * Check if the connection is fast
     * @param type the type of connection. Either WIFI or mobile
     * @param subType the type of mobile network
     * @return true of the connection is fast. WIFI is always fast
     */
    private fun isConnectionFast(type: Int, subType: Int): Boolean {
        return when (type) {
            ConnectivityManager.TYPE_WIFI -> true
            ConnectivityManager.TYPE_MOBILE -> when (subType) {
                TelephonyManager.NETWORK_TYPE_1xRTT, // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_CDMA, // ~ 14-64 kbps
                TelephonyManager.NETWORK_TYPE_EDGE, // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_GPRS -> false // ~ 100 kbps

                TelephonyManager.NETWORK_TYPE_EVDO_0, // ~ 400-1000 kbps
                TelephonyManager.NETWORK_TYPE_EVDO_A, // ~ 600-1400 kbps
                TelephonyManager.NETWORK_TYPE_HSDPA, // ~ 2-14 Mbps
                TelephonyManager.NETWORK_TYPE_HSPA, // ~ 700-1700 kbps
                TelephonyManager.NETWORK_TYPE_HSUPA, // ~ 1-23 Mbps
                TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps

                // Above API level 7, make sure to set android:targetSdkVersion
                // to appropriate level to use these
                TelephonyManager.NETWORK_TYPE_EHRPD, // API level 11  ~ 1-2 Mbps
                TelephonyManager.NETWORK_TYPE_EVDO_B, // API level 9  ~ 5 Mbps
                TelephonyManager.NETWORK_TYPE_HSPAP, // API level 13  ~ 10-20 Mbps
                TelephonyManager.NETWORK_TYPE_LTE -> true // API level 11  ~ 10+ Mbps
                TelephonyManager.NETWORK_TYPE_IDEN, // API level 8 ~25 kbps
                TelephonyManager.NETWORK_TYPE_UNKNOWN -> false
                else -> false
            }
            else -> false
        }
    }

}

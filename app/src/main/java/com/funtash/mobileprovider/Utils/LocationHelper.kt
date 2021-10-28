package com.funtash.mobileprovider.Utils

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings

class LocationHelper {
    companion object {
        fun checkLocation(mContext: Context) {
            val locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                LocationDisableAlert(mContext)
            }
        }
        private fun LocationDisableAlert(mContext: Context) {
            val alertDialogBuilder = android.app.AlertDialog.Builder(mContext)
            alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton(
                    "Enable GPS"
                ) { dialog, id ->
                    val callGPSSettingIntent = Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    )
                    try {
                        mContext.startActivity(callGPSSettingIntent)
                    } catch (e: Exception) {
                    }
                }

            val alert = alertDialogBuilder.create()
            try {
                alert.show()
            } catch (e: Exception) {
            }
        }
    }
}
package com.goazzi.skycore.misc

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.Objects

object Util {

    fun isGpsEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var isGpsEnabled = false
        try {
            isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: java.lang.Exception) {
            Log.d(TAG, "isGpsEnabled: $ex")
        }

        return isGpsEnabled
    }

    fun hasLocationPermission(context: Context): Boolean {
        val fineLoc: Boolean = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLoc: Boolean = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLoc && coarseLoc
    }

    fun getStatus(isClosed: Boolean): String {
        return if (isClosed) {
            "Currently CLOSED"
        } else {
            "Currently OPEN"
        }
    }

    fun getStringFromDouble(value: Double): String {
        return value.toString()
    }

    private const val TAG = "Util"
}
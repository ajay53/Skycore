package com.goazzi.skycore.misc

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Insets
import android.location.LocationManager
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowInsets
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.goazzi.skycore.R
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

    @BindingAdapter("imageAdapter")
    @JvmStatic
    fun setImageResource(view: AppCompatImageView, imageUrl: String) {
        val context: Context = view.context

        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.ic_restaurant)
            .error(R.drawable.ic_restaurant)

        Glide.with(context).setDefaultRequestOptions(options).load(imageUrl).into(view)
    }

    fun getScreenWidth(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    private const val TAG = "Util"
}
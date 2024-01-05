package com.goazzi.skycore.misc

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Insets
import android.location.LocationManager
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowInsets
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.goazzi.skycore.R

object Util {

    //checking for GPS
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

    //checking for locatin permission
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

    //setting and styling status of restaurant
    @JvmStatic
    @BindingAdapter("statusAdapter")
    fun setStatus(view: AppCompatTextView, isClosed: Boolean) {
        val status = if (isClosed) {
            "Currently CLOSED"
        } else {
            "Currently OPEN"
        }

        val spannable = SpannableString(status)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(view.context, R.color.black)),
            0, 9,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (isClosed) {
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(view.context, R.color.holo_red_dark)),
                9, status.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(view.context, R.color.holo_green_dark)),
                9, status.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        view.text = spannable
    }

    @JvmStatic
    @BindingAdapter("imageAdapter")
    fun setImageResource(view: AppCompatImageView, imageUrl: String) {
        val context: Context = view.context

        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.ic_restaurant)
            .error(R.drawable.ic_restaurant)

        Glide.with(context).setDefaultRequestOptions(options).load(imageUrl).into(view)
    }

    //setting and styling rating of restaurant
    @JvmStatic
    @BindingAdapter("ratingAdapter")
    fun setRating(view: AppCompatTextView, rating: Double) {

        val colorId: Int = when {
            rating > 4.5 -> R.color.holo_green_dark
            rating > 4.0 -> R.color.yellow
            rating > 3.5 -> R.color.holo_orange_dark
            else -> R.color.holo_red_dark
        }

        view.text = rating.toString()
        view.setBackgroundResource(colorId)
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

    fun getSelectedLocation(isSwitchEnabled: Boolean): Enum.Location {
        return if (isSwitchEnabled) Enum.Location.USA else Enum.Location.CURRENT
    }

    private const val TAG = "Util"
}
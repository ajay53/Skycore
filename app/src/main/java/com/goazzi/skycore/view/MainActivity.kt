package com.goazzi.skycore.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.HandlerThread
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goazzi.skycore.R
import com.goazzi.skycore.adapter.RestaurantRecyclerAdapter
import com.goazzi.skycore.databinding.ActivityMainBinding
import com.goazzi.skycore.databinding.DialogLocationRequestBinding
import com.goazzi.skycore.misc.*
import com.goazzi.skycore.misc.Enum
import com.goazzi.skycore.model.Business
import com.goazzi.skycore.model.BusinessesServiceClass
import com.goazzi.skycore.model.SearchBusiness
import com.goazzi.skycore.viewmodel.MainViewModel
import com.goazzi.skycore.viewmodel.MainViewModelFactory
import com.google.android.gms.location.*
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), RestaurantRecyclerAdapter.OnRestaurantClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var businesses: MutableList<Business>
    private lateinit var recyclerAdapter: RestaurantRecyclerAdapter
    private var isLoading: Boolean = false
    private var radius: Int = 100
    private var resetData: Boolean = false
    private val locHandler = HandlerThread("location")
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModelFactory()
        )[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //to hide action/title bar
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.businessServiceClass.observe(this) { business ->
            isLoading = false
            updateUI(business)
//            Log.d(TAG, "onCreate: business: $business")
        }

        initViews()
        askPermissions()
        createLocationRequest()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun initViews() {
        binding.sbRadiusSelector.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                //return if permission is not granted
                if (!arePermissionsEnabled()) {
                    binding.sbRadiusSelector.progress = 0
                    return
                }

                radius = when (p1) {
                    0 -> {
                        100
                    }
                    else -> {
                        250 * p1
                    }
                }

                //using formatter to remove zero after decimal point
                val decimalFormat = DecimalFormat("0.##")
                binding.tvRadiusVal.text =
                    if (radius >= 1000) {
                        "${decimalFormat.format(radius / 1000f)} KM"
                    } else {
                        "$radius M"
                    }

                //call api
                resetData = true
                isLoading = true
                val searchBusiness =
                    SearchBusiness(
                        40.730610,
                        -73.935242,
                        radius,
                        Enum.SortByEnum.BEST_MATCH.type,
                        Constants.PAGE_LIMIT,
                        0
                    )
                /*val searchBusiness =
                    SearchBusiness(
                        lat, lon, 1000, SortByEnum.BEST_MATCH.type, Constants.PAGE_LIMIT,
                        0
                    )*/
                viewModel.setSearchBusiness(searchBusiness)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                if (!arePermissionsEnabled()) {
                    askPermissions()
                    binding.sbRadiusSelector.progress = 0
                }
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        binding.rvRestaurants.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @Override
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager: LinearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager
                /*
                val visibleItemCount: Int = layoutManager.childCount
                val totalItemCount: Int = layoutManager.itemCount
                val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()*/
                val lastVisibleItemPosition: Int = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && lastVisibleItemPosition == businesses.size - 1) {

                    //return if permission is not granted
                    if (!arePermissionsEnabled()) {
                        askPermissions()
                        return
                    }

                    //call api
                    val searchBusiness =
                        SearchBusiness(
                            40.730610,
                            -73.935242,
                            radius,
                            Enum.SortByEnum.BEST_MATCH.type,
                            Constants.PAGE_LIMIT,
                            businesses.size
                        )
                    /*val searchBusiness =
                        SearchBusiness(
                            lat, lon, radius, SortByEnum.BEST_MATCH.type, Constants.PAGE_LIMIT,
                            businesses.size
                        )*/

                    resetData = false
                    isLoading = true
                    viewModel.setSearchBusiness(searchBusiness)
                }
            }
        })
    }

    private fun askPermissions() {
        if (!Util.isGpsEnabled(applicationContext)) {
            showLocationPermissionDialog(Enum.PermissionEnum.GPS)
//            gpsReqLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//            return false
        }

        if (!Util.hasLocationPermission(applicationContext)) {
            showLocationPermissionDialog(Enum.PermissionEnum.LOCATION)
//            permReqLauncher.launch(
//                arrayOf(
//                    //                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                )
//            )
//            return false
        }
//        return true
    }

    private fun arePermissionsEnabled(): Boolean {
        return (Util.isGpsEnabled(applicationContext) && Util.hasLocationPermission(
            applicationContext
        ))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateUI(businessServiceClass: BusinessesServiceClass) {
        /*Toast.makeText(
            applicationContext,
            "count: ${businessServiceClass.businesses.count()}",
            Toast.LENGTH_SHORT
        ).show()*/
        if (!this::recyclerAdapter.isInitialized) {
            businesses = mutableListOf<Business>().apply { addAll(businessServiceClass.businesses) }
            recyclerAdapter =
                RestaurantRecyclerAdapter(applicationContext, businesses, this)
            binding.rvRestaurants.adapter = recyclerAdapter
            binding.rvRestaurants.layoutManager = LinearLayoutManager(applicationContext)
            binding.rvRestaurants.setHasFixedSize(true)

            /*if (businesses.isEmpty()) {
                binding.ivNoRestaurant.visibility = View.VISIBLE
                binding.tvNoRestaurant.visibility = View.VISIBLE
                binding.rvRestaurants.visibility = View.GONE
            } else {
                binding.ivNoRestaurant.visibility = View.GONE
                binding.tvNoRestaurant.visibility = View.GONE
                binding.rvRestaurants.visibility = View.VISIBLE
            }*/
        } else {
            //reset list in case of radius change, else append list(pagination)
            if (resetData) {
                businesses.clear()
            }
            businesses.addAll(businessServiceClass.businesses)
            recyclerAdapter.notifyDataSetChanged()

            /*if (businesses.isEmpty()) {
                binding.ivNoRestaurant.visibility = View.VISIBLE
                binding.tvNoRestaurant.visibility = View.VISIBLE
                binding.rvRestaurants.visibility = View.GONE
            } else {
                binding.ivNoRestaurant.visibility = View.GONE
                binding.tvNoRestaurant.visibility = View.GONE
                binding.rvRestaurants.visibility = View.VISIBLE
            }*/
        }
        if (businesses.isEmpty()) {
            binding.ivNoRestaurant.visibility = View.VISIBLE
            binding.tvNoRestaurant.visibility = View.VISIBLE
            binding.rvRestaurants.visibility = View.GONE
        } else {
            binding.ivNoRestaurant.visibility = View.GONE
            binding.tvNoRestaurant.visibility = View.GONE
            binding.rvRestaurants.visibility = View.VISIBLE
        }
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { entry ->
                /*if (entry.key == Manifest.permission.ACCESS_COARSE_LOCATION) {
                    if (entry.value) {
                        Toast.makeText(applicationContext, "coarse Loc granted", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(applicationContext, "coarse Loc not", Toast.LENGTH_SHORT)
                            .show()
                    }
                }*/
                if (entry.key == Manifest.permission.ACCESS_FINE_LOCATION) {
                    if (entry.value) {
                        startLocationUpdates()
//                        Toast.makeText(applicationContext, "fine Loc granted", Toast.LENGTH_SHORT)
//                            .show()
                    } else {
                        binding.sbRadiusSelector.progress = 0
                        showLocationPermissionDialog(Enum.PermissionEnum.LOCATION)
//                        Toast.makeText(applicationContext, "fine Loc not", Toast.LENGTH_SHORT)
//                            .show()
                    }
                }
            }
        }

    private val gpsReqLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            //check for gps access
            if (arePermissionsEnabled()) {
                startLocationUpdates()
            }
        }

    private fun showLocationPermissionDialog(permissionEnum: Enum.PermissionEnum) {
        val alertBinding: DialogLocationRequestBinding =
            DialogLocationRequestBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .create()
        builder.setView(alertBinding.root)

//        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        when (permissionEnum) {
            Enum.PermissionEnum.GPS -> {
                alertBinding.tvTitle.text = getString(R.string.gps_permission_title)
                alertBinding.tvDesc.text = getString(R.string.gps_permission_desc)
            }
            Enum.PermissionEnum.LOCATION -> {
                alertBinding.tvTitle.text = getString(R.string.location_permission_title)
                alertBinding.tvDesc.text = getString(R.string.location_permission_desc)
            }
        }

        alertBinding.tvConfirm.setOnClickListener {
            builder.dismiss()
//            Toast.makeText(applicationContext, "confirm", Toast.LENGTH_SHORT).show()
            when (permissionEnum) {
                Enum.PermissionEnum.GPS -> {
                    gpsReqLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                Enum.PermissionEnum.LOCATION -> {
                    permReqLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            }
        }
        alertBinding.tvCancel.setOnClickListener {
            builder.dismiss()
//            Toast.makeText(applicationContext, "cancel", Toast.LENGTH_SHORT).show()
        }
        alertBinding.ivCross.setOnClickListener {
            builder.dismiss()
//            Toast.makeText(applicationContext, "cross", Toast.LENGTH_SHORT).show()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(builder.window?.attributes)
        val dialogWidth: Int = (Util.getScreenWidth(this) * 0.8f).toInt()
        layoutParams.width = dialogWidth
        builder.window?.attributes = layoutParams
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            Constants.UPDATE_INTERVAL_IN_MILLISECONDS
        ).build()

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        locationCallback = object : LocationCallback() {
            //NOT EXECUTING WHEN APP IS IN BACKGROUND
            //added foregaround service for this
            override fun onLocationResult(locationResult: LocationResult) {

                val currLocation: Location? = locationResult.lastLocation
                currLocation?.let {
                    lat = currLocation.latitude
                    lon = currLocation.longitude
                    /*Log.d(
                        TAG,
                        "onLocationResult: lat: ${currLocation.latitude} || Lon: ${currLocation.longitude}"
                    )*/
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (!locHandler.isAlive) {
            locHandler.start()
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            locHandler.looper
        )
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onRestaurantClick(pos: Int) {
        Toast.makeText(applicationContext, "Restaurant Clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJobs()
    }

    companion object {
        private const val TAG = "MainActivity"
    }


}
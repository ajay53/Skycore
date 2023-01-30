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
import com.goazzi.skycore.misc.Constants
import com.goazzi.skycore.misc.Enum
import com.goazzi.skycore.misc.Util
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
    private var isLastPage: Boolean = false
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
            binding.progressBar.hide()
            //handling response code
            if (business == null || business.code != 200) {
                if (business != null && business.message.isNotBlank()) {
                    Toast.makeText(applicationContext, business.message, Toast.LENGTH_SHORT)
                        .show()
                }
                binding.ivNoRestaurant.visibility = View.VISIBLE
                binding.tvNoRestaurant.visibility = View.VISIBLE
            } else {
                updateUI(business)
            }
        }

        initViews()
        askPermissions()
        createLocationRequest()
    }

    override fun onResume() {
        super.onResume()
        if (arePermissionsEnabled()) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initViews() {
        binding.sbRadiusSelector.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                //return if permission is not granted
                if (!arePermissionsEnabled()) {
                    binding.sbRadiusSelector.progress = 0
                    return
                }

                //keeping the min radius as 100
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
                binding.progressBar.show()
                binding.ivNoRestaurant.visibility = View.GONE
                binding.tvNoRestaurant.visibility = View.GONE
                resetData = true
                isLoading = true
                isLastPage = false
                //using location as per switch
                val searchBusiness = if (binding.swLocation.isChecked) {
                    SearchBusiness(
                        40.730610,
                        -73.935242,
                        radius,
                        Enum.SortBy.RATING.type,
                        Constants.PAGE_LIMIT,
                        0
                    )
                } else {
                    SearchBusiness(
                        lat, lon, radius, Enum.SortBy.RATING.type, Constants.PAGE_LIMIT,
                        0
                    )
                }

                viewModel.setSearchBusiness(searchBusiness)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //ask for permissions if not granted
                if (!arePermissionsEnabled()) {
                    askPermissions()
                    binding.sbRadiusSelector.progress = 0
                }
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        binding.swLocation.setOnCheckedChangeListener { _, isChecked ->
            //resetting views on switch change
            binding.tvLocationSwitch.text = if (isChecked) {
                "Location: NYC"
            } else {
                "Location: Current"
            }
            if (this::businesses.isInitialized) {
                businesses.clear()
            }
            if (this::recyclerAdapter.isInitialized) {
                recyclerAdapter.notifyDataSetChanged()
            }
            resetData = true
            isLoading = false
            isLastPage = false
            binding.sbRadiusSelector.progress = 0
            binding.ivNoRestaurant.visibility = View.GONE
            binding.tvNoRestaurant.visibility = View.GONE
            viewModel.cancelJobs()
            binding.progressBar.hide()
        }

        binding.rvRestaurants.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @Override
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager: LinearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager

                val visibleItemCount: Int = layoutManager.childCount
                val totalItemCount: Int = layoutManager.itemCount
                val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition: Int = layoutManager.findLastVisibleItemPosition()

                //checking for last element and calling api
//                if (!isLoading && lastVisibleItemPosition == businesses.size - 1) {
                if (!isLoading && !isLastPage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {

                    //return if permission is not granted
                    if (!arePermissionsEnabled()) {
                        askPermissions()
                        return
                    }

                    //call api
                    binding.progressBar.show()
                    binding.ivNoRestaurant.visibility = View.GONE
                    binding.tvNoRestaurant.visibility = View.GONE
                    resetData = false
                    isLoading = true

                    //using location as per switch
                    val searchBusiness = if (binding.swLocation.isChecked) {
                        SearchBusiness(
                            40.730610,
                            -73.935242,
                            radius,
                            Enum.SortBy.RATING.type,
                            Constants.PAGE_LIMIT,
                            businesses.size
                        )
                    } else {
                        SearchBusiness(
                            lat, lon, radius, Enum.SortBy.RATING.type, Constants.PAGE_LIMIT,
                            businesses.size
                        )
                    }

                    viewModel.setSearchBusiness(searchBusiness)
                }
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            //call api
            binding.progressBar.show()
            binding.ivNoRestaurant.visibility = View.GONE
            binding.tvNoRestaurant.visibility = View.GONE
            resetData = true
            isLoading = true
            isLastPage = false

            //using location as per switch
            val searchBusiness = if (binding.swLocation.isChecked) {
                SearchBusiness(
                    40.730610,
                    -73.935242,
                    radius,
                    Enum.SortBy.RATING.type,
                    Constants.PAGE_LIMIT,
                    0
                )
            } else {
                SearchBusiness(
                    lat, lon, radius, Enum.SortBy.RATING.type, Constants.PAGE_LIMIT,
                    0
                )
            }

            viewModel.setSearchBusiness(searchBusiness)
        }
    }

    private fun askPermissions() {
        if (!Util.isGpsEnabled(applicationContext)) {
            showLocationPermissionDialog(Enum.Permission.GPS)
        }
        if (!Util.hasLocationPermission(applicationContext)) {
            showLocationPermissionDialog(Enum.Permission.LOCATION)
        }
    }

    private fun arePermissionsEnabled(): Boolean {
        return (Util.isGpsEnabled(applicationContext) && Util.hasLocationPermission(
            applicationContext
        ))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateUI(businessServiceClass: BusinessesServiceClass) {

        binding.swipeRefreshLayout.isRefreshing = false
        if (businessServiceClass.businesses.size < Constants.PAGE_LIMIT) {
            isLastPage = true
        }

        if (!this::recyclerAdapter.isInitialized) {
            businesses = mutableListOf<Business>().apply { addAll(businessServiceClass.businesses) }
            recyclerAdapter =
                RestaurantRecyclerAdapter(applicationContext, businesses, this)
            binding.rvRestaurants.adapter = recyclerAdapter
            binding.rvRestaurants.layoutManager = LinearLayoutManager(applicationContext)
            binding.rvRestaurants.setHasFixedSize(true)
        } else {
            //reset list in case of radius change, else append list - (pagination)
            if (resetData) {
                recyclerAdapter.refreshList(businessServiceClass.businesses)

                //scroll to 1st item,
                binding.rvRestaurants.layoutManager?.scrollToPosition(0)
            } else {
                businesses.addAll(businessServiceClass.businesses)
                recyclerAdapter.notifyDataSetChanged()
            }


            /*if (resetData) {
                businesses.clear()
                businesses.addAll(businessServiceClass.businesses)
                recyclerAdapter.notifyDataSetChanged()
            } else {
                recyclerAdapter.refreshList(businessServiceClass.businesses)
            }*/


            //OG Code
            //reset list in case of radius change, else append list - (pagination)
            /*if (resetData) {
                businesses.clear()
            }
            businesses.addAll(businessServiceClass.businesses)
            recyclerAdapter.notifyDataSetChanged()*/
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
                if (entry.key == Manifest.permission.ACCESS_FINE_LOCATION) {
                    if (entry.value) {
                        if (arePermissionsEnabled()) {
                            startLocationUpdates()
                        }
                    } else {
                        binding.sbRadiusSelector.progress = 0
                        showLocationPermissionDialog(Enum.Permission.LOCATION)
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

    private fun showLocationPermissionDialog(permission: Enum.Permission) {
        val alertBinding: DialogLocationRequestBinding =
            DialogLocationRequestBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .create()
        builder.setView(alertBinding.root)

        when (permission) {
            Enum.Permission.GPS -> {
                alertBinding.tvTitle.text = getString(R.string.gps_permission_title)
                alertBinding.tvDesc.text = getString(R.string.gps_permission_desc)
            }
            Enum.Permission.LOCATION -> {
                alertBinding.tvTitle.text = getString(R.string.location_permission_title)
                alertBinding.tvDesc.text = getString(R.string.location_permission_desc)
            }
        }

        alertBinding.tvConfirm.setOnClickListener {
            builder.dismiss()
//            Toast.makeText(applicationContext, "confirm", Toast.LENGTH_SHORT).show()
            when (permission) {
                Enum.Permission.GPS -> {
                    gpsReqLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                Enum.Permission.LOCATION -> {
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
        }
        alertBinding.ivCross.setOnClickListener {
            builder.dismiss()
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
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            locHandler.looper
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onRestaurantClick(pos: Int) {
        Toast.makeText(applicationContext, businesses[pos].name, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJobs()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
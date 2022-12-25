package com.goazzi.skycore.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goazzi.skycore.R
import com.goazzi.skycore.adapter.RestaurantRecyclerAdapter
import com.goazzi.skycore.databinding.ActivityMainBinding
import com.goazzi.skycore.databinding.DialogLocationRequestBinding
import com.goazzi.skycore.misc.Constants
import com.goazzi.skycore.misc.PermissionType
import com.goazzi.skycore.misc.Util
import com.goazzi.skycore.model.Business
import com.goazzi.skycore.model.BusinessesServiceClass
import com.goazzi.skycore.model.SearchBusiness
import com.goazzi.skycore.viewmodel.MainViewModel
import com.goazzi.skycore.viewmodel.MainViewModelFactory
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), RestaurantRecyclerAdapter.OnRestaurantClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var businesses: MutableList<Business>
    private lateinit var recyclerAdapter: RestaurantRecyclerAdapter
    private var isLoading: Boolean = false
    private var radius: Int = 100
    private var resetData: Boolean = false

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
        /*requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )*/
    }

    private fun askPermissions() {
        if (!Util.isGpsEnabled(applicationContext)) {
            showLocationPermissionDialog(PermissionType.GPS)
//            gpsReqLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//            return false
        }

        if (!Util.hasLocationPermission(applicationContext)) {
            showLocationPermissionDialog(PermissionType.LOCATION)
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

    private fun initViews() {
        binding.sbRadiusSelector.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

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

                /*if (!arePermissionsEnabled()) {
                    return
                }*/

                //call api
                resetData = true
                isLoading = true
                val searchBusiness =
                    SearchBusiness(
                        40.730610,
                        -73.935242,
                        radius,
                        "best_match",
                        Constants.PAGE_LIMIT,
                        0
                    )
                //                SearchBusiness(18.61014312183422, 73.78609507664633, 1000, "best_match", 15)
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
                            "best_match",
                            Constants.PAGE_LIMIT,
                            businesses.size
                        )
                    //                SearchBusiness(18.61014312183422, 73.78609507664633, 1000, "best_match", 15)

                    resetData = false
                    isLoading = true
                    viewModel.setSearchBusiness(searchBusiness)
                }
            }
        })
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
        } else {
            if (resetData) {
                businesses.clear()
            }
            businesses.addAll(businessServiceClass.businesses)
            recyclerAdapter.notifyDataSetChanged()
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
                        Toast.makeText(applicationContext, "fine Loc granted", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        binding.sbRadiusSelector.progress = 0
                        showLocationPermissionDialog(PermissionType.LOCATION)
//                        Toast.makeText(applicationContext, "fine Loc not", Toast.LENGTH_SHORT)
//                            .show()
                    }
                }
                /*if (!entry.value) {
                    when (entry.key) {
                        Manifest.permission.ACCESS_COARSE_LOCATION -> {
                            Toast.makeText(applicationContext, "coarse Loc", Toast.LENGTH_SHORT)
                                .show()
                        }
                        Manifest.permission.ACCESS_FINE_LOCATION -> {

                        }
                    }
                }*/
            }
            /*val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
//            val menuView = navView[0] as BottomNavigationMenuView
//            menuView.children.elementAt(0).performClick()
//            Util.setClickedMenuItemId(Util.BottomNavigationMenuItemIds.EXPLORE_NAV_MENU)
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.data = Uri.parse("package:" + applicationContext.packageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent)
            }*/
        }

    private val gpsReqLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            //check for gps access
            /*if(!Util.isGpsEnabled(applicationContext)){

            }*/
        }

    private fun showLocationPermissionDialog(permissionType: PermissionType) {
        val alertBinding: DialogLocationRequestBinding =
            DialogLocationRequestBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .create()
        builder.setView(alertBinding.root)

//        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        when (permissionType) {
            PermissionType.GPS -> {
                alertBinding.tvTitle.text = getString(R.string.gps_permission_title)
                alertBinding.tvDesc.text = getString(R.string.gps_permission_desc)
            }
            PermissionType.LOCATION -> {
                alertBinding.tvTitle.text = getString(R.string.location_permission_title)
                alertBinding.tvDesc.text = getString(R.string.location_permission_desc)
            }
        }

        alertBinding.tvConfirm.setOnClickListener {
            builder.dismiss()
//            Toast.makeText(applicationContext, "confirm", Toast.LENGTH_SHORT).show()
            when (permissionType) {
                PermissionType.GPS -> {
                    gpsReqLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                PermissionType.LOCATION -> {
                    permReqLauncher.launch(
                        arrayOf(
                            //                    Manifest.permission.ACCESS_COARSE_LOCATION,
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
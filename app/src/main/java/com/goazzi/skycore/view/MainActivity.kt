package com.goazzi.skycore.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import com.goazzi.skycore.databinding.ActivityMainBinding
import com.goazzi.skycore.misc.Util
import com.goazzi.skycore.model.Business
import com.goazzi.skycore.model.SearchBusiness
import com.goazzi.skycore.viewmodel.MainViewModel
import com.goazzi.skycore.viewmodel.MainViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationMenuView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var businesses: Business

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

        binding.btnDum.setOnClickListener {
            val searchBusiness =
                SearchBusiness(40.730610, -73.935242, 1000, "best_match", 15)
//                SearchBusiness(18.61014312183422, 73.78609507664633, 1000, "best_match", 15)
            viewModel.setSearchBusiness(searchBusiness)
        }

        viewModel.business.observe(this) { business ->
            Log.d(TAG, "onCreate: business: $business")
        }

        binding.sbRadiusSelector.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                /*when (p1) {
                    1 -> {
                        val radius:Int = 250
                    }
                    else {
                        val radius:Int = 250 * p1
                    }
                }*/
                val radius: Int = 250 * p1
                Log.d(TAG, "onProgressChanged: p1: $p1 || radius: $radius")

                //call api

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        if (!Util.hasLocationPermission(applicationContext)) {
            permReqLauncher.launch(
                arrayOf(
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
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
                        Toast.makeText(applicationContext, "fine Loc not", Toast.LENGTH_SHORT)
                            .show()
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

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Do something if some permissions granted or denied
            permissions.entries.forEach {
                // Do checking here
                if (!it.value) {
                    if (it.key == Manifest.permission.ACCESS_COARSE_LOCATION) {
                        Toast.makeText(this, "Location permission not granted.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    if (it.key == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "Storage permission not granted.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    if (it.key == Manifest.permission.CAMERA) {
                        Toast.makeText(this, "Camera permission not granted.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    if (it.key == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "Storage permission not granted.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    private val gpsReqLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//        attemptSocketConnection()
        }

    companion object {
        private const val TAG = "MainActivity"
    }
}
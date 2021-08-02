package com.example.beemon

import android.Manifest
import android.app.Activity
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.beemon.databinding.FragmentFirstBinding
import com.punchthrough.blestarterappandroid.ble.ConnectionManager
import kotlinx.android.synthetic.main.fragment_first.*
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule
import kotlin.random.Random






private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)


        // define layout manager for the Recycler view
        binding.scanResultsRecyclerView.layoutManager = LinearLayoutManager(activity)
        // attach adapter to the recycler view
        binding.scanResultsRecyclerView.adapter = scanResultAdapter

        // create new objects
        val item1= ScanItem("myBeeHive1","1X:XX:XX:XX", "-101dBm")
        val item2= ScanItem("myBeeHive2","2X:XX:XX:XX", "-102dBm")
        val item3= ScanItem("myBeeHive3","3X:XX:XX:XX", "-103dBm")

        // call set data method of adapter class and the list data
        scanResultAdapter.setData(listOf(item1, item2, item3))
//        binding.btnNewLanguage.setOnClickListener {
//            // on click of button add one more item to the list
//            val language4= Language(4,"CPP" , "Exp : 5 years")
//            rvAdapter.setData(listOf(language1,language2,language3,language4))
//
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         binding.buttonNext.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        setupRecyclerView()
        scan_button.setOnClickListener {
            startBleScan()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        scan_results_recycler_view.apply {
            adapter = scanResultAdapter
            layoutManager = LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }

        val animator = scan_results_recycler_view.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }


    }

    private val bleScanner by lazy {
        (activity as MainActivity).bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()



    private var isScanning = false
        set(value) {
            field = value
            val newText = if (value) "Stop Scan" else "Start Scan"
            activity?.runOnUiThread(Runnable { scan_button.setText(newText) })
        }

    private val scanResults = mutableListOf<ScanResult>()


    private val scanResultAdapter by lazy {
        ScanResultAdapter(scanResults) { result ->
            if (isScanning) {
                stopBleScan()
            }
//
//            Timber.i("connecting")
//            with(result.device) {
//                Timber.i("Connecting to $address")
//                ConnectionManager.connect(this, binding.root.context)
//            }
        }
    }


    private val isLocationPermissionGranted
        get() = context?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun stopBleScan() {
        bleScanner.stopScan(scanCallback)
        isScanning = false
    }

    private fun requestLocationPermission() {
        this.activity?.requestPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun startBleScan() {
        if (!isLocationPermissionGranted!!) {
            requestLocationPermission()
        }
        Timber.i("Starting bleScanner")
        scanResults.clear()
        scanResultAdapter.notifyDataSetChanged()
        bleScanner.startScan(null, scanSettings, scanCallback)
        isScanning = true
        Timer().schedule(10000) {
            stopBleScan()
        }
    }

    /*******************************************
     * Callback bodies
     *******************************************/

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                with(result.device) {
                    Timber.i("device already in the list! Name: ${name ?: "Unnamed"}, address: $address")
                }
            } else {
                with(result.device) {
                    Timber.i("Found BLE new device! Name: ${name ?: "Unnamed"}, address: $address")
                }
                scanResults.add(result)
                val scanItems = mutableListOf<ScanItem>()
                for (scanresult in scanResults) {
                    scanItems.add(ScanItem(
                        scanresult.device.name,
                        scanresult.device.address,
                        "${scanresult.rssi} dBm")
                    )
                }
                scanResultAdapter.setData(scanItems)
            }
        }
        override fun onScanFailed(errorCode: Int) {
            Timber.e("onScanFailed: code $errorCode")
        }
    }
    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }
    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }
//    private fun onListItemClick(position: Int) {
//        Toast.makeText(this.activity?.applicationContext, scanResults[position], Toast.LENGTH_SHORT).show()
//    }

}

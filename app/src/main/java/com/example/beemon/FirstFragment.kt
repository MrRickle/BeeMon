package com.example.beemon

import android.Manifest
import android.app.Activity
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beemon.databinding.FragmentFirstBinding
import kotlinx.android.synthetic.main.fragment_first.*
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule


private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private var scanItems = mutableListOf<ScanItem>()

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
        // create new objects
        // val item1= ScanItem("No Device","XX:XX:XX:XX", "-00dBm")
        scanItems = mutableListOf()
        // attach adapter to the recycler view
        binding.scanResultsRecyclerView.adapter = ScanResultAdapter(scanItems) {
                scanItem: ScanItem -> scanItemClicked(scanItem)
        }
        return binding.root
    }

    private fun scanItemClicked(scanItem: ScanItem) {
        Toast.makeText(this.activity?.applicationContext,
            scanItem.itemName, Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         binding.buttonNext.setOnClickListener {
             if (isScanning)
                 stopBleScan()
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
//        setupRecyclerView()
        scan_button.setOnClickListener {
            startStopBleScan()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            activity?.runOnUiThread { scan_button.text = newText }
        }

    private val isLocationPermissionGranted
        get() = context?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun stopBleScan() {
        if (isScanning) {
            bleScanner.stopScan(scanCallback)
            isScanning = false
        }
    }

    private fun requestLocationPermission() {
        this.activity?.requestPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun startStopBleScan(){
        if (isScanning){
            Timber.i("Stopping bleScanner")
            stopBleScan()
        }else {
            if (!isLocationPermissionGranted!!) {
                requestLocationPermission()
            }
            Timber.i("Starting bleScanner")
            bleScanner.startScan(null, scanSettings, scanCallback)
            isScanning = true
            Timer().schedule(10000) {
                stopBleScan()
            }
        }
    }

    /*******************************************
     * Callback bodies
     *******************************************/

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanItems.indexOfFirst { it.itemMacAddress == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                with(result.device) {
                    Timber.i("device already in the list! Name: ${name ?: "Unnamed"}, address: $address")
                    val newScanItem =  getScanItemFromScanResult(result)
                    if (newScanItem != scanItems[indexQuery]) {
                        Timber.i("Updating scanItem[$indexQuery]")
                        scanItems[indexQuery] = newScanItem
                        binding.scanResultsRecyclerView.adapter?.notifyItemChanged(indexQuery)
                    }
                    else {
                        Timber.i(" ${result.device.name} did not change")
                    }
                }
            } else {
                with(result.device) {
                    Timber.i("Found BLE new device! Name: ${name ?: "Unnamed"}, address: $address")
                }
                scanItems.add(getScanItemFromScanResult(result))
                binding.scanResultsRecyclerView.adapter?.notifyItemInserted(scanItems.size)
            }
        }
        fun getScanItemFromScanResult (result: ScanResult ):ScanItem{
            return ScanItem(
                result.device.name,
                result.device.address,
                "${result.rssi} dBm"
            )
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


}

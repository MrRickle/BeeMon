package com.example.beemon
import android.bluetooth.le.ScanResult
import android.util.Log
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beemon.R
import kotlinx.android.synthetic.main.row_scan_result.view.device_name
import kotlinx.android.synthetic.main.row_scan_result.view.mac_address
import kotlinx.android.synthetic.main.row_scan_result.view.signal_strength

class ScanResultAdapter(
    private val items: List<ScanResult>,
    private val funk: ((device: ScanResult) -> Unit)
) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
           val view = LayoutInflater.from(parent.context).inflate(
               R.layout.content_main
           ,
            parent,
            false
        )

            return ViewHolder(view, funk)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    class ViewHolder(
        private val view: View,
        private val funk: ((device: ScanResult) -> Unit)) : RecyclerView.ViewHolder(view)
    {

        fun bind(result: ScanResult) {
            view.device_name.text = result.device.name ?: "Unnamed"
            view.mac_address.text = result.device.address
            view.signal_strength.text = result.rssi.toString() // result.rssi.toString() + " dB"
            if (result.device.toString() == "2B:0E:27:A5:7F:DB"){
                funk.invoke(result)
            }
        }
    }
}

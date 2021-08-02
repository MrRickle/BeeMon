/*
 * Copyright 2019 Punch Through Design LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.beemon

import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.beemon.databinding.RowScanResultBinding

class ScanResultAdapter(
    private val items: List<ScanResult>,
    private val onClickListener: ((device: ScanResult) -> Unit)
) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {
    private var oldScanItemsList=emptyList<com.example.beemon.ScanItem>()
    // create an inner class with name ViewHolder
    //It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: RowScanResultBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowScanResultBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }
    // bind the items with each item of the list languageList which than will be
    // shown in recycler view
    // to keep it simple we are not setting any image data to view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(oldScanItemsList[position]){
                binding.deviceName.text = this.itemName
                binding.macAddress.text = this.itemMacAddress
                binding.signalStrength.text = this.itemSignalStrength
            }
        }
    }

    override fun getItemCount() = oldScanItemsList.size

    //
    fun setData(newScanItemList : List<ScanItem>){
        val diffUtil = MyDiffUtil(oldScanItemsList , newScanItemList)
        // it calculates the different items of the oldLanguageList and newLanguageList
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        // assign oldLanguageList to newLanguageList
        oldScanItemsList = newScanItemList
        diffResult.dispatchUpdatesTo(this)
    }


//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = items[position]
//        holder.bind(item)
//    }
//
//    class ViewHolder(
//        private val view: View,
//        private val onClickListener: ((device: ScanResult) -> Unit)
//    ) : RecyclerView.ViewHolder(view) {
//
//        fun bind(result: ScanResult) {
//            view.device_name.text = result.device.name ?: "Unnamed"
//            view.mac_address.text = result.device.address
//            view.signal_strength.text = "${result.rssi} dBm"
//            view.setOnClickListener { onClickListener.invoke(result) }
//        }
//    }
}

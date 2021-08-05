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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beemon.databinding.RowScanResultBinding

class ScanResultAdapter(
    private var scanItemList: MutableList<ScanItem>,
    private val clickListener: (ScanItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            // LayoutInflater: takes ID from layout defined in XML.
            // Instantiates the layout XML into corresponding View objects.
            // Use context from main app -> also supplies theme layout values!
            val inflater = LayoutInflater.from(parent.context)
            // Inflate XML. Last parameter: don't immediately attach new view to the parent view group
            val binding = RowScanResultBinding.inflate(inflater, parent, false)
            return ScanItemViewHolder(binding)
        }

    // bind the items with each item of the list scanItemList which than will be
    // shown in recycler view
    // to keep it simple we are not setting any image data to view
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Populate ViewHolder with data that corresponds to the position in the list
        // which we are told to load
        (holder as ScanItemViewHolder).bind(scanItemList[position], clickListener)
    }

    override fun getItemCount() = scanItemList.size

    // create an inner class with name ScanItemViewHolder
    //It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ScanItemViewHolder(private val binding: RowScanResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ScanItem, clickListener: (ScanItem) -> Unit) {
            binding.deviceName.text = item.itemName
            binding.macAddress.text = item.itemMacAddress
            binding.signalStrength.text = item.itemSignalStrength
            binding.root.setOnClickListener { clickListener(item) }
        }

    }
}

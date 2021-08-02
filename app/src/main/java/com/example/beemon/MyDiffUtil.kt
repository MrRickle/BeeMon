package com.example.beemon
import androidx.recyclerview.widget.DiffUtil

// pass two list one oldList and second newList
class MyDiffUtil(
    private val oldList : List<ScanItem>,
    private val newList : List<ScanItem>
) :DiffUtil.Callback() {
    // implement methods
    override fun getOldListSize(): Int {
        // return oldList size
        return oldList.size
    }

    override fun getNewListSize(): Int {
        // return newList size
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // compare items based on their unique id
        return oldList[oldItemPosition].itemMacAddress == newList[newItemPosition].itemMacAddress
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // in here compare each item if they are same or different
        // return false if any data is same else return true
        return when {
            oldList[oldItemPosition].itemMacAddress != newList[newItemPosition].itemMacAddress -> false
            oldList[oldItemPosition].itemName != newList[newItemPosition].itemName -> false
            oldList[oldItemPosition].itemSignalStrength != newList[newItemPosition].itemSignalStrength -> false
            else -> true
        }
    }
}
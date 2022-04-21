package com.example.alpha.deliveryman

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alpha.R
import com.example.alpha.util.toBitmap

class PickUpItemAdapter(
    val fn: (ViewHolder, PickUpItem) -> Unit = { _, _ -> }
) : ListAdapter<PickUpItem, PickUpItemAdapter.ViewHolder>(DiffCallback) {



    companion object DiffCallback : DiffUtil.ItemCallback<PickUpItem>() {
        override fun areItemsTheSame(a: PickUpItem, b: PickUpItem)    = a.id == b.id
        override fun areContentsTheSame(a: PickUpItem, b: PickUpItem) = a == b
    }

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val root = view
        val itemImage: ImageView = view.findViewById(R.id.itemImage)
        val name: TextView = view.findViewById(R.id.itemNameTxt)
        val quantity: TextView = view.findViewById(R.id.itemQuantityTxt)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.pickup_detail_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val pickup = getItem(position)
        holder.name.text = pickup.name
        holder.quantity.text = pickup.quantity.toString()
        if(pickup.image.toString() != "Blob { bytes= }"){
            holder.itemImage.setImageBitmap(pickup.image.toBitmap())
        }
        fn(holder, pickup)
    }

}
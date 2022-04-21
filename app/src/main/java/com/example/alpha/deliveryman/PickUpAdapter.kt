package com.example.alpha.deliveryman

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.textservice.TextInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alpha.R
import com.example.alpha.user.UserAdapter
import com.example.alpha.util.toBitmap

class PickUpAdapter(
    val fn: (ViewHolder, PickUp) -> Unit = { _, _ -> }
) : ListAdapter<PickUp, PickUpAdapter.ViewHolder>(DiffCallback) {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PickUp>() {
        override fun areItemsTheSame(a: PickUp, b: PickUp)    = a.orderId == b.orderId
        override fun areContentsTheSame(a: PickUp, b: PickUp) = a == b
    }

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
            }

        val root = view
        val orderID: TextView = view.findViewById(R.id.orderID)
        val sellerAddr: TextView = view.findViewById(R.id.sellerAddrText)
        val userAddr: TextView = view.findViewById(R.id.userAddrText)
        val quantity: TextView = view.findViewById(R.id.quantityTxt)
        val detailBtn: Button = view.findViewById(R.id.detailBtn)

        fun bind(pickup: PickUp, isActivated: Boolean = false) {
            root.isActivated = isActivated
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.pickup_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val pickup = getItem(position)
        holder.orderID.text = pickup.orderId
        holder.userAddr.text = pickup.userAddress
        holder.sellerAddr.text = pickup.sellerAddress
        holder.quantity.text = "(" + pickup.orderQuantity.toString() + ") items"
        fn(holder, pickup)
    }

}
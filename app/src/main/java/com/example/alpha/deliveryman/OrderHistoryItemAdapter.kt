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

class OrderHistoryItemAdapter(
    val fn: (ViewHolder, DeliveryHistory) -> Unit = { _, _ -> }
) : ListAdapter<DeliveryHistory, OrderHistoryItemAdapter.ViewHolder>(DiffCallback) {



    companion object DiffCallback : DiffUtil.ItemCallback<DeliveryHistory>() {
        override fun areItemsTheSame(a: DeliveryHistory, b: DeliveryHistory)    = a.id == b.id
        override fun areContentsTheSame(a: DeliveryHistory, b: DeliveryHistory) = a == b
    }

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val root = view
        val id: TextView = view.findViewById(R.id.orderID)
        val status: TextView = view.findViewById(R.id.status)
        val sa: TextView = view.findViewById(R.id.sellerAddrText)
        val ca: TextView = view.findViewById(R.id.userAddrText)
        val quantity: TextView = view.findViewById(R.id.quantityTxt)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.order_history_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val od = getItem(position)
        holder.id.text = ("#Order " + od.id + "")
        holder.sa.text = od.sellerAddress
        holder.ca.text = od.customerAddress
        var status = when{
            (od.progress == 1 && od.status == 1) -> {
                holder.status.text = "On Way to Shop"
            }
            (od.progress == 2 && od.status == 1) -> {
                holder.status.text = "Sending to Customer"
            }
            (od.progress == 3 && od.status == 1) -> {
                holder.status.text = "Completed"
            }
            else -> {

            }
        }
        holder.quantity.text = od.quantity.toString()

        fn(holder, od)
    }

}
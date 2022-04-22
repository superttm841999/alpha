package com.example.alpha.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alpha.R
import com.example.alpha.data.Seller

class ShopOrderAdapter (
    val fn: (ViewHolder, Seller) -> Unit = { _, _ -> }
) : ListAdapter<Seller, ShopOrderAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Seller>() {
        override fun areItemsTheSame(a: Seller, b: Seller) = a.docId == b.docId
        override fun areContentsTheSame(a: Seller, b: Seller) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val imgLogo: ImageView = view.findViewById(R.id.imgItemShop)
        val txtName: TextView = view.findViewById(R.id.txtItemShopName)
        val txtCount: TextView = view.findViewById(R.id.txtItemShopCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_seller_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val application = getItem(position)

        if (application.status == 1) {

            holder.imgLogo.setImageBitmap(application.logo.toBitmap())
            holder.txtName.text = application.name

            holder.txtCount.text = "${application.count} order(s) "
            fn(holder, application)
        }
    }
}

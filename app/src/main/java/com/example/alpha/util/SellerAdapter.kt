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

class SellerAdapter (
    val fn: (ViewHolder, Seller) -> Unit = { _, _ -> }
) : ListAdapter<Seller, SellerAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Seller>() {
        override fun areItemsTheSame(a: Seller, b: Seller)    = a.docId == b.docId
        override fun areContentsTheSame(a: Seller, b: Seller) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val imgLogo : ImageView = view.findViewById(R.id.imgLogo)
        val txtUsername    : TextView = view.findViewById(R.id.txtUsername)
        val txtShopName  : TextView = view.findViewById(R.id.txtShopName)
        val txtStatusForm   : TextView = view.findViewById(R.id.txtStatusForm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_approval_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val form = getItem(position)

        holder.txtUsername.text   = form.username
        holder.txtShopName.text = form.name

        when(form.status){
            0 -> holder.txtStatusForm.text  = "pending"
            1 -> holder.txtStatusForm.text  = "approved"
            2 -> holder.txtStatusForm.text  = "rejected"
            else -> holder.txtStatusForm.text  = "???"
        }

        holder.imgLogo.setImageBitmap(form.logo.toBitmap())

        fn(holder, form)
    }

}
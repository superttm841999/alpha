package com.example.alpha.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alpha.R
import com.example.alpha.data.Voucher

class VoucherAdapter (
    val fn: (ViewHolder, Voucher) -> Unit = { _, _ -> }
) : ListAdapter<Voucher, VoucherAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Voucher>() {
        override fun areItemsTheSame(a: Voucher, b: Voucher)    = a.docId == b.docId
        override fun areContentsTheSame(a: Voucher, b: Voucher) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val txtId    : TextView = view.findViewById(R.id.txtId)
        val txtName  : TextView = view.findViewById(R.id.txtName)
        val txtCode  : TextView = view.findViewById(R.id.txtCode)
        val txtStatus   : TextView = view.findViewById(R.id.txtStatus)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_voucher_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val voucher = getItem(position)

        holder.txtId.text   = voucher.docId
        holder.txtName.text = voucher.name
        holder.txtCode.text = voucher.code

        when(voucher.status){
            0 -> holder.txtStatus.text  = "Invalid"
            1 -> holder.txtStatus.text  = "Valid"
            else -> holder.txtStatus.text  = "???"
        }

        fn(holder, voucher)
    }

}
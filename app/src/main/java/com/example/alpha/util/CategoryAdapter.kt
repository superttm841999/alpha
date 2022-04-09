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
import com.example.alpha.data.Category

class CategoryAdapter (
    val fn: (ViewHolder, Category) -> Unit = { _, _ -> }
) : ListAdapter<Category, CategoryAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(a: Category, b: Category)    = a.docId == b.docId
        override fun areContentsTheSame(a: Category, b: Category) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val txtId    : TextView = view.findViewById(R.id.txtId)
        val txtName  : TextView = view.findViewById(R.id.txtName)
        val txtStatus   : TextView = view.findViewById(R.id.txtStatus)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category1 = getItem(position)

        holder.txtId.text   = category1.docId
        holder.txtName.text = category1.name
        holder.txtStatus.text  = category1.status

        fn(holder, category1)
    }

}
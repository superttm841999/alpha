package com.example.alpha.user

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alpha.R
import com.example.alpha.util.toBitmap

class UserAdapter(
    val fn: (ViewHolder, LUser) -> Unit = { _, _ -> }
) : ListAdapter<LUser, UserAdapter.ViewHolder>(DiffCallback) {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<LUser>() {
        override fun areItemsTheSame(a: LUser, b: LUser)    = a.id == b.id
        override fun areContentsTheSame(a: LUser, b: LUser) = a == b
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
        val staffUsername: TextView = view.findViewById(R.id.staffUsername)
        val staffName: TextView = view.findViewById(R.id.staffName)
        val staffEmail: TextView = view.findViewById(R.id.staffEmail)
        val staffImage: ImageView = view.findViewById(R.id.image)
        val staffGender: TextView = view.findViewById(R.id.gender)
        val staffStatus: TextView = view.findViewById(R.id.status)
        val activeUserBtn: Button = view.findViewById(R.id.activeBtn)
        var blockUserBtn: Button = view.findViewById(R.id.blockBtn)
        var deleteUserBtn: Button = view.findViewById(R.id.deleteBtn)

        fun bind(user: LUser, isActivated: Boolean = false) {
            staffUsername.text = user.username
            staffName.text = user.name
            staffEmail.text = user.email
            if(user.image.toString() != "Blob { bytes= }"){
                staffImage.setImageBitmap(user.image.toBitmap())
            }
            var gender = when(user.gender){
                0 -> "Male"
                1 -> "Female"
                else -> ""
            }
            var color = when(user.status){
                0 -> Color.parseColor("#FF0000")
                1 -> Color.parseColor("#00FF00")
                else -> Color.parseColor("#FF0000")
            }

            var status = when(user.status){
                0 -> "Pending"
                1 -> "Active"
                2 -> "Blocked"
                else -> ""
            }

            staffGender.text = gender
            staffStatus.text = status
            staffStatus.setTextColor(color)
            root.isActivated = isActivated
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user = getItem(position)
        if(user.image.toString() != "Blob { bytes= }"){
            holder.staffImage.setImageBitmap(user.image.toBitmap())
            Log.d("testimage", user.image.toString())
        }

        holder.staffUsername.text = user.username
        holder.staffName.text = user.name
        holder.staffEmail.text = user.email
        var gender = when(user.gender){
            0 -> "Male"
            1 -> "Female"
            else -> ""
        }
        var color = when(user.status){
            0 -> Color.parseColor("#FF0000")
            1 -> Color.parseColor("#00FF00")
            else -> Color.parseColor("#FF0000")
        }

        var status = when(user.status){
            0 -> "Pending"
            1 -> "Active"
            2 -> "Blocked"
            else -> ""
        }

        holder.staffGender.text = gender
        holder.staffStatus.text = status
        holder.staffStatus.setTextColor(color)
        tracker?.let {
            holder.bind(user, it.isSelected(position.toLong()))
        }

        fn(holder, user)
    }

}
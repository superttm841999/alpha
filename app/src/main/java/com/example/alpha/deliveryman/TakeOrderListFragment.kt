package com.example.alpha.deliveryman

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpha.R
import com.example.alpha.databinding.FragmentMngStaffBinding
import com.example.alpha.databinding.FragmentTakeOrderListBinding
import com.example.alpha.ui.LoginViewModel
import com.example.alpha.user.MyItemDetailsLookup
import com.example.alpha.user.MyItemKeyProvider
import com.example.alpha.user.UserAdapter
import com.example.alpha.user.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TakeOrderListFragment : Fragment() {
    private lateinit var binding: FragmentTakeOrderListBinding
    private val userModel: UserViewModel by activityViewModels()
    private val model: PickUpViewModel by activityViewModels()
    private var tracker: SelectionTracker<Long>? = null
    private val nav by lazy { findNavController() }
    private val loginModel: LoginViewModel by activityViewModels()
    private val orderDb by lazy { Firebase.firestore.collection("Order")}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        if(model.pickupStatus.value == null){
//        if(model.canPickUpAnother.value?.canOrNot == false) {
//            model.canPickUpAnother.value?.progress?.let { statusDialog(it) }
//        }}

        if(model.canPickUpAnother.value?.canOrNot == false){
                model.canPickUpAnother.value?.progress?.let { statusDialog(it) }
        }

        // Inflate the layout for this fragment
        binding = FragmentTakeOrderListBinding.inflate(inflater, container, false)
        val adapter = PickUpAdapter() { holder, pickup ->
            holder.detailBtn.setOnClickListener {
                var pickUpStatus = PickUpStatus(pickup.orderId, 0)
                model.pickupStatus.value = pickUpStatus

                var order = mutableMapOf<String, Any>()
                order["deliveryMan"] = loginModel.user.value?.id.toString()
                order["progress"] = 1

                orderDb.document(pickup.orderId).update(order).addOnSuccessListener {
                   // nav.navigate(R.id.action_takeOrderListFragment_to_pickUpSellerFragment)
                }


            }
        }
        val rv = binding.pickUpRV
        rv.itemAnimator = null
        val layoutManager = LinearLayoutManager(activity)
        rv.adapter = adapter
        rv.layoutManager = layoutManager
        rv.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        tracker = SelectionTracker.Builder<Long>(
            "mySelection",
            rv,
            PickUpItemKeyProvider(rv),
            PickUpItemDetailsLookup(rv),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker

        model.pickups.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                tracker?.clearSelection()
            }
            adapter.submitList(it)
        }


        return binding.root
    }
    private fun statusDialog(status: Int){
        var message = when(status){
            1 -> "Please go to take your accepted order food from seller"
            2 -> "Please send your picked up food to customer"
            else -> ""
        }
        var dialog = AlertDialog.Builder(requireContext())
            .setTitle("Delivery Order Status")
            .setMessage(message)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                when(status){
                    1 -> nav.navigate(R.id.pickUpSellerFragment)
                    2 -> nav.navigate(R.id.deliveryToCustomerFragment)
                    else -> nav.navigate(R.id.takeOrderListFragment)
                }
                dialogInterface.dismiss()

            }).create()
        dialog.setCancelable(false)
        dialog.show()
    }
}
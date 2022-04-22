package com.example.alpha.deliveryman

import android.content.DialogInterface
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpha.LoginActivity
import com.example.alpha.R
import com.example.alpha.databinding.FragmentPUItemDetailBinding
import com.example.alpha.databinding.FragmentTakeOrderListBinding
import com.example.alpha.util.rmUserSR
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class PUItemDetailFragment : Fragment() {
    private lateinit var binding: FragmentPUItemDetailBinding
    private val order = Firebase.firestore.collection("OrderFood")
    private val food = Firebase.firestore.collection("Food")
    private val id by lazy { requireArguments().getString("orderId") ?: "" }
    private val model: PickUpViewModel by activityViewModels()
    private val nav by lazy { findNavController() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var orderId = id
        if(model.canPickUpAnother.value?.canOrNot == false){
            model.canPickUpAnother.value?.progress?.let { statusDialog(it) }
        }
        // Inflate the layout for this fragment
        binding = FragmentPUItemDetailBinding.inflate(inflater, container, false)



        binding.pickupBtn.setOnClickListener {
            var pickUpStatus = PickUpStatus(orderId, 0)
            model.pickupStatus.value = pickUpStatus
           // nav.navigate(R.id.action_PUItemDetailFragment_to_pickUpSellerFragment)
        }

        return binding.root
    }

    private suspend fun getOrderItems(orderId: String): List<PickUpItem>{
        var puItems = mutableListOf<PickUpItem>()
        val result = order.whereEqualTo("orderId", orderId).get().await()
        for(data in result.documents){
            var quantity = data.data?.get("quantity").toString().toInt()
            var food = runBlocking { getFoodName(data.data?.get("foodId").toString()) }
            var foodName = food["name"].toString()
            var foodImage = food["image"]
            puItems.add(PickUpItem(data.id, foodImage as Blob, foodName , quantity))
        }
        return puItems.toList()
    }

    private suspend fun getFoodName(foodId: String): MutableMap<String, Any> {
        var foodItem = mutableMapOf<String, Any>()
        val result = food.document(foodId).get().await()
        foodItem["name"] = result.data?.get("name").toString()
        foodItem["image"] = result.data?.get("image") ?: Blob.fromBytes(ByteArray(0))
        return foodItem

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
package com.example.alpha.deliveryman

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpha.R
import com.example.alpha.databinding.FragmentPUItemDetailBinding
import com.example.alpha.databinding.FragmentPickUpSellerBinding
import com.example.alpha.ui.LoginViewModel
import com.example.alpha.util.showToast
import com.example.alpha.util.toBitmap
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class PickUpSellerFragment : Fragment() {

    private val model: PickUpViewModel by activityViewModels()
    private lateinit var binding: FragmentPickUpSellerBinding
    private val order = Firebase.firestore.collection("Order")
    private val orderFood = Firebase.firestore.collection("OrderFood")
    private val seller = Firebase.firestore.collection("Seller")
    private val food = Firebase.firestore.collection("Food")
    private var orderId:String? = null
    private val sellerId: String? = null
    private val loginModel: LoginViewModel by activityViewModels()
    private val nav by lazy { findNavController() }
    private val orderDb by lazy { Firebase.firestore.collection("Order")}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        orderId = model.pickupStatus.value?.orderId
        // Inflate the layout for this fragment
        binding = FragmentPickUpSellerBinding.inflate(inflater, container, false)

        binding.orderId.text = "#Order ${orderId.toString()}"
        var list = runBlocking { getOrderItems(orderId!!) }
        val adapter = PickUpItemAdapter() { holder, pickup ->
        }
        orderId?.let {
            order.document(it).get().addOnSuccessListener { it ->
                seller.document(it.data?.get("sellerId").toString()).get().addOnSuccessListener { it ->
                    binding.name.text = it.data?.get("name").toString()
                    binding.addressTxt.text = it.data?.get("address").toString()
                    it.data?.get("logo")?.let { it->
                        var image = it as Blob
                        binding.sImage.setImageBitmap(it.toBitmap())
                    }
                }
            }
        }
        val rv = binding.itemsRV
        rv.itemAnimator = null
        val layoutManager = LinearLayoutManager(activity)
        rv.adapter = adapter
        rv.layoutManager = layoutManager
        adapter.submitList(list)

        binding.pickupBtn.setOnClickListener {
            var pickUpStatus = orderId?.let { it1 -> PickUpStatus(it1, 1) }
            model.pickupStatus.value = pickUpStatus
            var order = mutableMapOf<String, Any>()
            order["deliveryMan"] = loginModel.user.value?.id.toString()
            order["progress"] = 2

            orderDb.document(orderId!!).update(order).addOnSuccessListener {
               // nav.navigate(R.id.action_pickUpSellerFragment_to_deliveryToCustomerFragment)
            }


        }

        Log.d("oicnm", model.canPickUpAnother.value.toString())

        return binding.root
    }

    private suspend fun getOrderItems(orderId: String): List<PickUpItem>{
        var puItems = mutableListOf<PickUpItem>()
        val result = orderFood.whereEqualTo("orderId", orderId).get().await()
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
}
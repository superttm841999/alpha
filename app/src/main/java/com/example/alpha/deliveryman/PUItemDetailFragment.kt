package com.example.alpha.deliveryman

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpha.R
import com.example.alpha.databinding.FragmentPUItemDetailBinding
import com.example.alpha.databinding.FragmentTakeOrderListBinding
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPUItemDetailBinding.inflate(inflater, container, false)
        var list = runBlocking { getOrderItems(id) }
        val adapter = PickUpItemAdapter() { holder, pickup ->
        }

        val rv = binding.itemsRV
        rv.itemAnimator = null
        val layoutManager = LinearLayoutManager(activity)
        rv.adapter = adapter
        rv.layoutManager = layoutManager
        adapter.submitList(list)
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
}
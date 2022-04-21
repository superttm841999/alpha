package com.example.alpha.deliveryman

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alpha.user.LUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class PickUpViewModel: ViewModel() {
    private val order = Firebase.firestore.collection("Order")

    var pickupList = listOf<PickUp>()
    var pickups = MutableLiveData<List<PickUp>>()
    var pickupStatus = MutableLiveData<PickUpStatus>()

    init{
        order.whereEqualTo("status", 1).whereEqualTo("progress", 1).addSnapshotListener { snapshots, _ ->
            if(snapshots!=null){
                var pList = mutableListOf<PickUp>()
                for (dc in snapshots) {
                    var id = dc.id
                    var userAddr= dc.data?.get("address").toString()
                    var sellerAddr = runBlocking { getSellerAddress(dc.data?.get("sellerId").toString()) }
                    var status = dc.data?.get("status")
                    var progress = dc.data?.get("progress")
                    var quantity = runBlocking { getOrderItemsQuantity(id)}
                    var pickup = PickUp(id, sellerAddr, userAddr, status.toString().toInt(), progress.toString().toInt(), quantity)
                    pList.add(pickup)
                }
                pickups.value = pList.toList()

            }else{

            }
        }
    }

    suspend fun getSellerAddress(sellerId: String): String{
        val seller = Firebase.firestore.collection("Seller")
        val result = seller.document(sellerId)
            .get()
            .await()
        return result.data?.get("address")?.toString() ?: ""
    }

    suspend fun getOrderItemsQuantity(orderId: String): Int{
        val orderFood = Firebase.firestore.collection("OrderFood")
        var result = orderFood.whereEqualTo("orderId", orderId).get().await()
        var quantity = 0
        for (r in result.documents){
            quantity+=r.data?.get("quantity").toString().toInt()
        }
        return quantity
    }

}


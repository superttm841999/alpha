package com.example.alpha.deliveryman

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class DeliveryHistoryViewModel(var userId: String): ViewModel() {
    private val order = Firebase.firestore.collection("Order")
    var dList = listOf<DeliveryHistory>()
    var ds = MutableLiveData<List<DeliveryHistory>?>()

    init {
        order.whereEqualTo("deliveryMan", userId).addSnapshotListener { value, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (value?.documents?.size == 0) {
               ds.value = null
            }else{
                if (value != null) {
                    var deliverList = mutableListOf<DeliveryHistory>()
                    for (dc in value.documents) {
                        var id = dc.id
                        var userAddr= dc.data?.get("address").toString()
                        var sellerAddr = runBlocking { getSellerAddress(dc.data?.get("sellerId").toString()) }
                        var status = dc.data?.get("status").toString().toInt()
                        var progress = dc.data?.get("progress").toString().toInt()
                        var quantity = runBlocking { getOrderItemsQuantity(id)}
                        var dh = DeliveryHistory(
                            id,
                            userAddr,
                            sellerAddr,
                            progress = progress,
                            status = status,
                            quantity =quantity
                        )
                        deliverList.add(dh)
                    }
                    ds.value = deliverList.toList()
                }
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
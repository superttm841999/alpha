package com.example.alpha.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class SellerOrderViewModel : ViewModel() {

    private var sellers = listOf<Seller>()
    private val result = MutableLiveData<List<Seller>>()

    private var orders = listOf<Order>()
    private val orderResult = MutableLiveData<List<Order>>()

    private var name = ""
    private var id = ""
    private var orderId = ""

    init {
        viewModelScope.launch {
            APPLICATION_FORM.addSnapshotListener { snap, _ -> result.value = snap?.toObjects()
                sellers = snap!!.toObjects()
                runBlocking {
                    updateResult()
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            ORDERS.addSnapshotListener { snap, _ -> orderResult.value = snap?.toObjects()
                orders = snap!!.toObjects()
                runBlocking {
                    updateOrderResult()
                }
            }
        }
    }


     fun getAll() = result

     fun getAllOrder() = orderResult

    suspend fun get(id: String): Seller? {
        return APPLICATION_FORM.document(id).get().await().toObject()
    }

    suspend fun getFruits(id: String): List<Order> {
        val fruits = ORDERS.whereEqualTo("sellerId", id).get().await().toObjects<Order>()

        val category = get(id)

        for (f in fruits) {
            f.application = category!!
        }

        return fruits
    }

    private suspend fun updateResult(){
        var list = sellers.filter{ f->
            f.name.contains(name,true) && f.status == 1
        }

        list.sortedBy { f-> f.name }

        for(l in list){
            l.count = ORDERS.whereEqualTo("sellerId", l.docId).get().await().size()
        }

        result.value = list
    }


    private suspend fun updateOrderResult(){
        var list = orders.filter{ f->
            f.docId.contains(orderId,true) && f.sellerId == id
        }

        list.sortedBy { f-> f.docId }

        for(l in list){
            l.count = ORDERS.whereEqualTo("docId", l.docId).get().await().size()
        }

        orderResult.value = list
    }

    fun search(name:String){
        this.name = name
        runBlocking { updateResult()
        }
    }

    fun searchOrder(orderId:String){
        this.orderId = orderId
        runBlocking {
            updateOrderResult()
        }
    }

    fun getSellerId(id:String){
        this.id = id
        runBlocking { updateOrderResult() }
    }

    suspend fun getOrderId(orderId: String): List<OrderFood> {

        return ORDER_FOODS.whereEqualTo("orderId", orderId).get().await().toObjects<OrderFood>()
    }
}
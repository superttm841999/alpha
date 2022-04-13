package com.example.alpha.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CategoryViewModel : ViewModel() {

    private val col = Firebase.firestore.collection("Category")
    private val countCol = Firebase.firestore.collection("Count")
    private val cat = listOf<Category>()
    private val categories = MutableLiveData<List<Category>>()
    private val counts = MutableLiveData<List<Count>>()


    init {
        col.addSnapshotListener { snap, _ -> categories.value = snap?.toObjects() }
        viewModelScope.launch {
            val categories = col.get().await().toObjects<Category>()
            col
        }
    }

    init {
        countCol.addSnapshotListener { snap, _ -> counts.value = snap?.toObjects() }
        viewModelScope.launch {
            val counts = countCol.get().await().toObjects<Count>()
            countCol
        }
    }

    fun get(docId: String): Category? {
        return categories.value?.find{ f -> f.docId == docId }
    }

    fun getAll() = categories

    fun delete(id: String) {
        col.document(id).delete()
    }

    fun deleteAll() {
        //col.get().addOnSuccessListener { snap -> snap.documents.forEach{ doc -> delete(doc.id) } }
        categories.value?.forEach { f-> delete(f.docId) }
    }

    fun set(f: Category) {
        col.document(f.docId).set(f)
    }

    fun getCategories() = getAll()

    //Read COUNT
    suspend fun getCount(docId: String): Int? {
        val result = countCol.document(docId).get().await()
        return result.data?.get("count").toString().toIntOrNull()
    }

    //Update COUNT
    fun setCount(f: Count) {
        countCol.document(f.docId).set(f)
    }

    private fun idExists(docId: String): Boolean {
        return categories.value?.any{ f -> f.docId == docId} ?: false
    }

    private fun nameExists(name: String): Boolean {
        return categories.value?.any{ f -> f.name == name} ?: false
    }

    fun validate(f: Category, insert: Boolean = true): String {
        var e = ""

        if (insert) {
            e += if (f.docId == "") "- Id is required.\n"
            else if (idExists(f.docId)) "- Id is duplicated.\n"
           // else if (f.docId == "1001") "- Please submit again if only Id got problem.\n"
            else ""

            e += if (f.name == "") "- Name is required.\n"
            else if (f.name.length < 3) "- Name is too short.\n"
            else if (nameExists(f.name)) "- Name is duplicated.\n"
            else ""
        }

        e += if (f.name == "") "- Name is required.\n"
        else if (f.name.length < 3) "- Name is too short.\n"
        else ""

        return e
    }
}

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

class SellerViewModel : ViewModel() {
    private val col = Firebase.firestore.collection("Seller")
    private val forms = MutableLiveData<List<Seller>>()

    init {
        col.addSnapshotListener { snap, _ -> forms.value = snap?.toObjects() }
    }


    fun get(docId: String): Seller? {
        return forms.value?.find{ f -> f.docId == docId }
    }

    fun getAll() = forms

    private fun delete(docId: String) {
        col.document(docId).delete()
    }

    fun deleteAll() {
        forms.value?.forEach { f-> delete(f.docId) }
    }

    fun set(f: Seller) {
        col.document(f.docId).set(f)
    }

    private fun idExists(id: String): Boolean {
        return forms.value?.any{ f -> f.docId == id} ?: false
    }

    private fun nameExists(name: String): Boolean {
        return forms.value?.any{ f -> f.name == name} ?: false
    }


    fun validate(f: Seller, insert: Boolean = true): String {
        val regexId = Regex("""^[0-9A-Z]{4}$""")
        var e = ""

        if (insert) {
            e += if (f.docId == "") "- Id is required.\n"
            //else if (!f.id.matches(regexId)) "- Id format is invalid.\n"
            else if (idExists(f.docId)) "- Id is duplicated.\n"
            //else if (f.docId == "1001") "- Please submit again if only Id got problem.\n"
            else ""
        }

        e += if (f.name == "") "- Name is required.\n"
        else if (f.name.length < 3) "- Name is too short.\n"
        else if (nameExists(f.name)) "- Name is duplicated.\n"
        else ""

        e += if (f.logo.toBytes().isEmpty()) "- Photo is required.\n"
        else ""

        e += if (f.category == "--SELECT--") "- Category is required. \n"
        else ""

        return e
    }
}
package com.example.alpha.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VoucherViewModel : ViewModel() {
    private val col = Firebase.firestore.collection("Voucher")
    private val forms = MutableLiveData<List<Voucher>>()
    private val countCol = Firebase.firestore.collection("Count")
    private val counts = MutableLiveData<List<Count>>()

    init {
        col.addSnapshotListener { snap, _ -> forms.value = snap?.toObjects() }
    }

    init {
        countCol.addSnapshotListener { snap, _ -> counts.value = snap?.toObjects() }
        viewModelScope.launch {
            val counts = countCol.get().await().toObjects<Count>()
            countCol
        }
    }

    fun get(docId: String): Voucher? {
        return forms.value?.find{ f -> f.docId == docId }
    }

    fun getAll() = forms

    fun delete(docId: String) {
        col.document(docId).delete()
    }

    fun deleteAll() {
        forms.value?.forEach { f-> delete(f.docId) }
    }

    fun set(f: Voucher) {
        col.document(f.docId).set(f)
    }

    private fun idExists(id: String): Boolean {
        return forms.value?.any{ f -> f.docId == id} ?: false
    }

    private fun nameExists(name: String): Boolean {
        return forms.value?.any{ f -> f.name == name} ?: false
    }

    private fun codeExists(code: String): Boolean {
        return forms.value?.any{ f -> f.code == code} ?: false
    }

    //Read COUNT
    fun getCount(docId: String): Count? {
        return counts.value?.find{ f -> f.docId == docId }
    }

    //Update COUNT
    fun setCount(f: Count) {
        countCol.document(f.docId).set(f)
    }

    fun validate(f: Voucher, insert: Boolean = true): String {
        var e = ""

        if (insert) {
            e += if (f.docId == "") "- Id is required.\n"
            //else if (!f.id.matches(regexId)) "- Id format is invalid.\n"
            else if (idExists(f.docId)) "- Id is duplicated.\n"
            else ""

            e += if (f.name == "") "- Name is required.\n"
            else if (f.name.length < 3) "- Name is too short.\n"
            else if (nameExists(f.name)) "- Name is duplicated.\n"
            else ""

            e += if (f.code == "") "- Code is required.\n"
            else if (f.code.length < 3) "- Code is too short.\n"
            else if (codeExists(f.code)) "- Code is duplicated.\n"
            else ""
        }



        e += if (f.value == 0.0) "- Value is required.\n"
        else ""


        return e
    }
}
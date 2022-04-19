package com.example.alpha.user

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class UserViewModel: ViewModel() {
    private val userDB = Firebase.firestore.collection("Users")

    var oriList = listOf<LUser>()
    var result = MutableLiveData<List<LUser>>()

    private var search = ""

    init {
        userDB.whereIn("role", listOf(0, 1)).addSnapshotListener { snapshots, _ ->

            if (snapshots != null) {
                var adminList = mutableListOf<LUser>()
                for (dc in snapshots) {
                    var id = dc.id
                    var username = dc.data?.get("username") as String
                    var name = dc.data?.get("name").toString()
                    var email = dc.data?.get("email") as String
                    var role = (dc.data?.get("role").toString()).toIntOrNull()?:0
                    var phone = (dc.data?.get("phone")?:"-1") as String
                    var gender = (dc.data?.get("gender").toString()).toIntOrNull()?:-1
                    var status = (dc.data?.get("status").toString()).toIntOrNull()?:0
                    var image = dc.data["image"]?: Blob.fromBytes(ByteArray(0))
                    val user = LUser(id = id,
                        username = username,
                        name = name,
                        email = email,
                        role = role,
                        phone = phone,
                        gender = gender,
                        status = status,
                        image = image as Blob
                    )
                    Log.d("test", user.toString())
                    adminList.add(user)
                }
                oriList = adminList.toList()
                updateResult()
            }
        }
    }

    fun search(searchString: String){
        this.search = searchString
        updateResult()
    }

    private fun updateResult(){
        var list = oriList.filter { f->
            f.username.contains(search, true)
        }
        Log.d("list", list.toString())
        result.value = list
    }

    fun getStaff(id: String): LUser {
        var staff = oriList.filter{
            it.id == id
        }
        return staff[0]
    }
}
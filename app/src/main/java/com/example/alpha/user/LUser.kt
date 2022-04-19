package com.example.alpha.user

import com.google.firebase.firestore.Blob
import java.nio.ByteBuffer

data class LUser(
    var id: String,
    var username: String,
    var name: String,
    var email: String,
    var phone: String?,
    var gender: Int?,
    var role: Int,
    var status: Int?,
    var image : Blob = Blob.fromBytes(ByteArray(0))
){
    fun getID(): Long {
        return ByteBuffer.wrap(this.id.toByteArray()).int.toLong()
    }
}
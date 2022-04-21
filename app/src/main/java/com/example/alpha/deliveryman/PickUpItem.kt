package com.example.alpha.deliveryman

import com.google.firebase.firestore.Blob

data class PickUpItem(
    var id: String,
    var image : Blob = Blob.fromBytes(ByteArray(0)),
    var name: String,
    var quantity: Int
)
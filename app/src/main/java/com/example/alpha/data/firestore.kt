package com.example.alpha.data

import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId
    var docId : String = "",
    var name : String = "",
//Different firebase field different with data class field
//@PropertyName("umur") var age : Int = 0
    var status : String = "",
)

data class Count(
    @DocumentId
    var docId   : String = "",
    var count : Int = 0,
)

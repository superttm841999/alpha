package com.example.alpha.deliveryman

data class PickUp(
    var orderId: String = "",
    var sellerAddress: String = "",
    var userAddress: String = "",
    var orderStatus: Int,
    var progressStatus:Int,
    var orderQuantity: Int
)
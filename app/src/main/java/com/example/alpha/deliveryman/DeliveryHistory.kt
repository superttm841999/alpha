package com.example.alpha.deliveryman

data class DeliveryHistory(
    var id: String,
    var customerAddress: String,
    var sellerAddress: String,
    var progress: Int,
    var status: Int,
    var quantity: Int
)
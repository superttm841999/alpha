package com.example.alpha.deliveryman

data class PickUpStatus(
    var orderId: String,
    var pickUpStatus: Int
    )
{
}

data class Another(
var canOrNot: Boolean,
var progress: Int
)
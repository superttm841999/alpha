package com.example.alpha.deliveryman

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DeliveryHistoryViewModelFactory(private var userid: String):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DeliveryHistoryViewModel(userid) as T
    }
}

package com.example.alpha.deliveryman

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpha.R
import com.example.alpha.databinding.FragmentDeliveryHistoryBinding
import com.example.alpha.databinding.FragmentDeliveryToCustomerBinding
import com.example.alpha.ui.LoginViewModel
import com.example.alpha.user.UserViewModel
import com.example.alpha.user.UserViewModelFactory
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class DeliveryHistoryFragment : Fragment() {
    private val order = Firebase.firestore.collection("Order")
    private lateinit var binding: FragmentDeliveryHistoryBinding
    private lateinit var model: DeliveryHistoryViewModel
    private val loginModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var loginId = loginModel.user.value?.id
        model = activity?.let {
            ViewModelProvider(
                it,
                DeliveryHistoryViewModelFactory(loginId.toString()!!)
            ).get( DeliveryHistoryViewModel::class.java)
        }!!


        binding = FragmentDeliveryHistoryBinding.inflate(inflater, container, false)
        val adapter = OrderHistoryItemAdapter() { holder, pickup ->
        }
        val rv = binding.deliveryHistoryRV
        rv.itemAnimator = null
        val layoutManager = LinearLayoutManager(activity)
        rv.adapter = adapter
        rv.layoutManager = layoutManager
        rv.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        model.ds.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }
}
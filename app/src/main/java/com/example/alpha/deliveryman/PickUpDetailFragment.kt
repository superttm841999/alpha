package com.example.alpha.deliveryman

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alpha.R


class PickUpDetailFragment : Fragment() {

    private val orderId by lazy { requireArguments().getString("orderId") ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pick_up_detail, container, false)
    }
}
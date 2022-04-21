package com.example.alpha.deliveryman

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpha.R
import com.example.alpha.databinding.FragmentMngStaffBinding
import com.example.alpha.databinding.FragmentTakeOrderListBinding
import com.example.alpha.user.MyItemDetailsLookup
import com.example.alpha.user.MyItemKeyProvider
import com.example.alpha.user.UserAdapter
import com.example.alpha.user.UserViewModel

class TakeOrderListFragment : Fragment() {
    private lateinit var binding: FragmentTakeOrderListBinding
    private val userModel: UserViewModel by activityViewModels()
    private val model: PickUpViewModel by activityViewModels()
    private var tracker: SelectionTracker<Long>? = null
    private val nav by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTakeOrderListBinding.inflate(inflater, container, false)
        val adapter = PickUpAdapter() { holder, pickup ->
            holder.detailBtn.setOnClickListener {
                nav.navigate(R.id.PUItemDetailFragment, bundleOf("orderId" to pickup.orderId))
            }
        }
        val rv = binding.pickUpRV
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
        tracker = SelectionTracker.Builder<Long>(
            "mySelection",
            rv,
            PickUpItemKeyProvider(rv),
            PickUpItemDetailsLookup(rv),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker

        model.pickups.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                tracker?.clearSelection()
            }
            adapter.submitList(it)
        }


        return binding.root
    }
}
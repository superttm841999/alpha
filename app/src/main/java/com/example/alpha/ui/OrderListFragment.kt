package com.example.alpha.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.alpha.R
import com.example.alpha.data.SellerOrderViewModel
import com.example.alpha.databinding.FragmentOrderListBinding
import com.example.alpha.util.OrderListAdapter

class OrderListFragment : Fragment() {

    private lateinit var binding: FragmentOrderListBinding
    private val id by lazy { requireArguments().getString("id") ?: "" }
    private val nav by lazy { findNavController() }
    private val vm: SellerOrderViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentOrderListBinding.inflate(inflater,container,false)

        vm.search("")

        val adapter = OrderListAdapter() { holder, order ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.orderDetailFragment, bundleOf("orderId" to order.docId,"total" to order.payment))
            }
        }

        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vm.getSellerId(id)
        vm.getAllOrder().observe(viewLifecycleOwner){ list ->

            adapter.submitList(list)
            binding.txtCount.text = "${list.size} 记录"

        }

        binding.sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(name:String) = true

            override fun onQueryTextChange(name:String): Boolean {
                vm.searchOrder(name)
                return true
            }

        }
        )


        return binding.root
    }


}
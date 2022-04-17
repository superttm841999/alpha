package com.example.alpha.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.alpha.R
import com.example.alpha.data.SellerListAdminViewModel
import com.example.alpha.data.SellerOrderViewModel
import com.example.alpha.databinding.FragmentOrderBinding
import com.example.alpha.databinding.FragmentSellerListAdminBinding
import com.example.alpha.util.ShopListAdapter
import com.example.alpha.util.ShopOrderAdapter
import kotlinx.coroutines.launch


class OrderFragment : Fragment() {

    private lateinit var binding: FragmentOrderBinding
    private val nav by lazy { findNavController() }
    private val vm: SellerOrderViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentOrderBinding.inflate(inflater,container,false)

        val adapter = ShopOrderAdapter() { holder, order ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.orderListFragment, bundleOf("id" to order.docId))
            }
        }

        vm.search("")

        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))


        vm.getAll().observe(viewLifecycleOwner){ list ->
            adapter.submitList(list)
            binding.txtCount.text = "${list.size} record(s)"
        }

        binding.sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(name:String) = true

            override fun onQueryTextChange(name:String): Boolean {
                vm.search(name)
                return true
            }

        }
        )

        return binding.root
    }

}
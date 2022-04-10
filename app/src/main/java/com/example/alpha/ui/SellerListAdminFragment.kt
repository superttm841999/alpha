package com.example.alpha.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.alpha.R
import com.example.alpha.data.SellerListAdminViewModel
import com.example.alpha.databinding.FragmentSellerListAdminBinding
import com.example.alpha.util.ShopListAdapter
import kotlinx.coroutines.launch

class SellerListAdminFragment : Fragment() {

    private lateinit var binding: FragmentSellerListAdminBinding
    private val nav by lazy { findNavController() }
    private val vm: SellerListAdminViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSellerListAdminBinding.inflate(inflater,container,false)

        val adapter = ShopListAdapter() { holder, category ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.shopFoodAdminFragment, bundleOf("id" to category.docId))
            }
        }
        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        lifecycleScope.launch{
            var application = vm.getAllAdmin()
            adapter.submitList(application)

            binding.txtCount.text = "${application.size} Shop(s)"
        }


        return binding.root
    }

}
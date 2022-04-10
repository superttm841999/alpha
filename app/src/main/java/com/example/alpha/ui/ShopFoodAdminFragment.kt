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
import com.example.alpha.databinding.FragmentShopFoodAdminBinding
import com.example.alpha.util.ShopFoodAdapter
import kotlinx.coroutines.launch

class ShopFoodAdminFragment : Fragment() {

    private lateinit var binding:FragmentShopFoodAdminBinding
    private val nav by lazy { findNavController() }
    private val vm: SellerListAdminViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentShopFoodAdminBinding.inflate(inflater,container,false)

        binding.btnInsert.setOnClickListener {
            nav.navigate(R.id.insertFoodFragment,bundleOf("shopId" to id))
        }

        val adapter = ShopFoodAdapter(){ holder, food ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.updateFoodFragment, bundleOf("id" to food.id, "shopId" to id))
            }
        }
        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        lifecycleScope.launch {
            val category = vm.get(id)!!
            binding.txtName.text = "${category.name} ($id)"

            val foods = vm.getFoodsAdmin(id)
            adapter.submitList(foods)
            binding.txtCount.text = "${foods.size} Food(s)"
        }

        return binding.root
    }

}
package com.example.alpha.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.alpha.R
import com.example.alpha.databinding.FragmentShopFoodBinding

class ShopFoodFragment : Fragment() {

    private lateinit var binding: FragmentShopFoodBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShopFoodBinding.inflate(inflater,container,false)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.shopfood, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.shopCategoryFragment -> nav.navigate(R.id.shopCategoryFragment)
            R.id.about2Fragment -> nav.navigate(R.id.about2Fragment)
        }

        return super.onOptionsItemSelected(item)
    }

}
package com.example.alpha.ui

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.alpha.R
import com.example.alpha.data.CategoryViewModel
import com.example.alpha.databinding.FragmentShopCategoryBinding
import com.example.alpha.util.CategoryAdapter
import com.example.alpha.util.deleteAllCategoryDialog

class ShopCategoryFragment : Fragment() {

    private lateinit var binding: FragmentShopCategoryBinding
    private val nav by lazy { findNavController() }
    private val vm: CategoryViewModel by activityViewModels()
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShopCategoryBinding.inflate(inflater,container,false)

        setHasOptionsMenu(true)

        binding.btnInsert.setOnClickListener {
            nav.navigate(R.id.insertCategoryFragment)
        }
        binding.btnDeleteAll.setOnClickListener {
            deleteAll()
        }

        adapter = CategoryAdapter() { holder, category ->
            // Item click
            holder.root.setOnClickListener {
                nav.navigate(R.id.updateCategoryFragment, bundleOf("id" to category.docId))
            }
            // Delete button click
            holder.btnDelete.setOnClickListener { deleteAllCategoryDialog(category.docId) }
        }
        binding.rvCat.adapter = adapter
        binding.rvCat.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vm.getAll().observe(viewLifecycleOwner){ list ->
            adapter.submitList(list)
            binding.txtCount.text = "${list.size} record(s)"
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.shopfood, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.shopCategoryFragment -> nav.navigate(R.id.shopCategoryFragment)
            R.id.insertFragment -> nav.navigate(R.id.about2Fragment)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun deleteAll() {
        vm.deleteAll()
    }

}
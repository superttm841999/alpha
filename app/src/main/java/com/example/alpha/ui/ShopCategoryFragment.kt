package com.example.alpha.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.alpha.R
import com.example.alpha.data.CategoryViewModel
import com.example.alpha.data.Seller
import com.example.alpha.databinding.FragmentShopCategoryBinding
import com.example.alpha.util.CategoryAdapter
import com.example.alpha.util.deleteAllCategoryDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

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

            holder.btnDelete.setOnClickListener {
                lifecycleScope.launch{

                    Firebase.firestore.collection("Seller").whereEqualTo("category",category.name).get().addOnSuccessListener {
                            snap ->
                        val list = snap.toObjects<Seller>()
                        if(list.isNotEmpty()){
                            AlertDialog.Builder(context)
                                .setIcon(R.drawable.ic_error)
                                .setTitle("Error")
                                .setMessage("Category cannot be deleted, some sellers is using it.")
                                .setPositiveButton("Back", null)
                                .show()
                        }else{
                            deleteAllCategoryDialog(category.docId) }
                        }

                    }
                }


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
        inflater.inflate(R.menu.shopcategory, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.shopCategoryFragment -> nav.navigate(R.id.shopCategoryFragment)
            R.id.insertCategoryFragment -> nav.navigate(R.id.insertCategoryFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAll() {
        vm.deleteAll()
    }

}
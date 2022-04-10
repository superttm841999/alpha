package com.example.alpha.ui

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.alpha.R
import com.example.alpha.data.VoucherViewModel
import com.example.alpha.databinding.FragmentVoucherBinding
import com.example.alpha.util.CategoryAdapter
import com.example.alpha.util.VoucherAdapter
import com.example.alpha.util.deleteAllCategoryDialog
import com.example.alpha.util.deleteAllVoucherDialog

class VoucherFragment : Fragment() {

    private lateinit var binding: FragmentVoucherBinding
    private val nav by lazy { findNavController() }
    private val vm: VoucherViewModel by activityViewModels()
    private lateinit var adapter: VoucherAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentVoucherBinding.inflate(inflater,container,false)

        setHasOptionsMenu(true)

        binding.btnInsert.setOnClickListener {
            nav.navigate(R.id.insertVoucherFragment)
        }
        binding.btnDeleteAll.setOnClickListener {
            deleteAll()
        }

        adapter = VoucherAdapter() { holder, voucher ->
            // Item click
            holder.root.setOnClickListener {
                nav.navigate(R.id.updateVoucherFragment, bundleOf("id" to voucher.docId))
            }
            // Delete button click
            holder.btnDelete.setOnClickListener { deleteAllVoucherDialog(voucher.docId) }
        }
        binding.rvVoucherList.adapter = adapter
        binding.rvVoucherList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vm.getAll().observe(viewLifecycleOwner){ list ->
            adapter.submitList(list)
            binding.txtCount.text = "${list.size} record(s)"
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.voucher, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.voucherFragment -> nav.navigate(R.id.voucherFragment)
            R.id.insertVoucherFragment -> nav.navigate(R.id.insertVoucherFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAll() {
        vm.deleteAll()
    }
}
package com.example.alpha.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.alpha.R
import com.example.alpha.data.SellerViewModel
import com.example.alpha.databinding.FragmentApprovalListBinding
import com.example.alpha.util.SellerAdapter

class ApprovalListFragment : Fragment() {

    private lateinit var binding: FragmentApprovalListBinding
    private val nav by lazy { findNavController() }
    private val vm: SellerViewModel by activityViewModels()
    private lateinit var adapter: SellerAdapter
    private val model: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("lolsell", model.user.value.toString())
        binding = FragmentApprovalListBinding.inflate(inflater,container,false)

        adapter = SellerAdapter() { holder, form ->
            // Item click
            holder.root.setOnClickListener {
                nav.navigate(R.id.sellerApprovalFragment, bundleOf("id" to form.docId))
            }
        }

        binding.rvApplicationForm.adapter = adapter
        binding.rvApplicationForm.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vm.getAll().observe(viewLifecycleOwner){ list ->
            adapter.submitList(list)
            binding.txtCount.text = "${list.size} record(s)"
        }

        return binding.root

    }


}
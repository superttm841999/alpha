package com.example.alpha.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.alpha.R
import com.example.alpha.data.Seller
import com.example.alpha.data.SellerViewModel
import com.example.alpha.databinding.FragmentSellerApprovalBinding
import com.example.alpha.util.cropToBlob
import com.example.alpha.util.successDialog
import com.example.alpha.util.toBitmap

class SellerApprovalFragment : Fragment() {

    private lateinit var binding: FragmentSellerApprovalBinding
    private val nav by lazy { findNavController() }
    private val vm: SellerViewModel by activityViewModels()
    private val model: LoginViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSellerApprovalBinding.inflate(inflater,container,false)

        val spinner: Spinner = binding.spnStatus

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.apply_Status,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        reset()
        binding.btnReset.setOnClickListener { reset() }
        binding.btnSubmit.setOnClickListener { submit() }

        return binding.root
    }

    private fun reset() {
        val f = vm.get(id)
        if(f == null){
            nav.navigateUp()
            return
        }
        load(f)
    }

    private fun load(f: Seller) {
        binding.txtName.text = f.name
        binding.txtUsername.text = f.username
        binding.txtCategoryUpdateStatus.text = f.category
        binding.imgPhoto.setImageBitmap(f.logo.toBitmap())
        binding.txtAddress.text = f.address
        binding.txtApprovalUsername.text = f.approvalUser
        binding.spnStatus.requestFocus()
        setStatus(f.status)
    }

    private fun setStatus(status: Int) {
        when (status) {
            2 -> binding.spnStatus.setSelection(2)
            1 -> binding.spnStatus.setSelection(1)
            0 -> binding.spnStatus.setSelection(0)
        }
    }

    private fun submit() {
        var select =0
        when (binding.spnStatus.selectedItem) {
            "Rejected" -> select = 2
            "Approved" -> select = 1
            "Pending" -> select = 0
            else -> -1
        }
        val f = Seller(
            docId = id,
            name = binding.txtName.text.toString().trim(),
            username = binding.txtUsername.text.toString(),
            category = binding.txtCategoryUpdateStatus.text.toString(),
            logo = binding.imgPhoto.cropToBlob(300,300),
            address = binding.txtAddress.text.toString(),
            status = select,
            approvalUser = model.user.value!!.username,
            approvalEmail = model.user.value!!.email,
            approvalName = model.user.value!!.name,
        )

        vm.set(f)
        successDialog("Update the status successfully")
        nav.navigateUp()
    }

}
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
import com.example.alpha.data.Category
import com.example.alpha.data.CategoryViewModel
import com.example.alpha.databinding.FragmentUpdateCategoryBinding
import com.example.alpha.util.errorDialog
import com.example.alpha.util.successDialog

class UpdateCategoryFragment : Fragment() {

    private lateinit var binding: FragmentUpdateCategoryBinding
    private val nav by lazy { findNavController() }
    private val vm: CategoryViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentUpdateCategoryBinding.inflate(inflater,container,false)

        val spinner: Spinner = binding.spnStatus

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.cat_Status,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        reset()

        binding.btnReset.setOnClickListener{ reset() }
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

    private fun load(f: Category) {
        binding.txtId.text = f.docId
        binding.edtName.setText(f.name)
        setStatus(f.status)

        binding.edtName.requestFocus()
    }

    private fun setStatus(status: String) {
        when (status) {
            "Published" -> binding.spnStatus.setSelection(1)
            "Drafted" -> binding.spnStatus.setSelection(0)
        }
    }

    private fun submit() {
        val f = Category(
            docId = id,
            name = binding.edtName.text.toString().uppercase().trim(),
            status = binding.spnStatus.selectedItem.toString(),
        )

        val err = vm.validate(f,false)
        if(err != ""){
            errorDialog(err)
            return
        }

        vm.set(f)
        successDialog("Record updated successfully")
        nav.navigateUp()
    }

}
package com.example.alpha.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.alpha.R
import com.example.alpha.data.Category
import com.example.alpha.data.CategoryViewModel
import com.example.alpha.data.Count
import com.example.alpha.databinding.FragmentInsertCategoryBinding
import com.example.alpha.util.errorDialog
import com.example.alpha.util.successDialog
import kotlinx.coroutines.runBlocking


class InsertCategoryFragment : Fragment() {

    private lateinit var binding: FragmentInsertCategoryBinding
    private val nav by lazy { findNavController() }
    private val vm: CategoryViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentInsertCategoryBinding.inflate(inflater,container,false)


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

        binding.edtName.requestFocus()

        binding.btnSubmit.setOnClickListener {
           runBlocking { submit() }
        }

        binding.btnReset.setOnClickListener{ reset() }

        return binding.root
    }

    private fun reset() {
        binding.edtName.text.clear()
        binding.spnStatus.setSelection(0)

        binding.edtName.requestFocus()
    }

    private suspend fun submit() {
        val c = vm.getCount("CountCategory")
        var count = c?.toInt()
        count = count?.plus(1000 + 1)

        var setCount = count?.minus(1000)
        var f = setCount?.let {
            Count(
                docId = "CountCategory",
                count = it
            )
        }


        val cat = Category(
            docId = count.toString(),
            name = binding.edtName.text.toString().uppercase().trim(),
            status = binding.spnStatus.selectedItem.toString(),
        )

        val err = vm.validate(cat)
        if(err != ""){
            errorDialog(err)
            return
        }
        vm.set(cat)
        if (f != null) {
            vm.setCount(f)
        }
        successDialog("Record added successfully")
        nav.navigateUp()

    }


}
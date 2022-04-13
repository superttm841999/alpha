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
import com.example.alpha.data.Count
import com.example.alpha.data.Voucher
import com.example.alpha.data.VoucherViewModel
import com.example.alpha.databinding.FragmentInsertVoucherBinding
import com.example.alpha.util.errorDialog
import com.example.alpha.util.successDialog
import kotlinx.coroutines.runBlocking

class InsertVoucherFragment : Fragment() {

    private lateinit var binding: FragmentInsertVoucherBinding
    private val nav by lazy { findNavController() }
    private val vm: VoucherViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentInsertVoucherBinding.inflate(inflater,container,false)

        val spinner: Spinner = binding.spnStatus

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.voucher_Status,
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
        binding.edtCode.text.clear()
        binding.edtValue.text.clear()
        binding.spnStatus.setSelection(0)
        binding.edtName.requestFocus()
    }

    private suspend fun submit() {

        val c = vm.getCount("CountVoucher")
        var count = c?.toInt()
        count = count?.plus(3000 + 1)

        var setCount = count?.minus(3000)
        var f = setCount?.let {
            Count(
                docId = "CountVoucher",
                count = it
            )
        }


        var select =0
        when (binding.spnStatus.selectedItem) {
            "Valid" -> select = 1
            "Invalid" -> select = 0
            else -> -1
        }

        val v = Voucher(
            docId = count.toString(),
            name = binding.edtName.text.toString().trim(),
            code = binding.edtCode.text.toString().trim(),
            value = binding.edtValue.text.toString().toIntOrNull()?:0,
            status = select,
            startDate = binding.edtStartDate.text.toString().trim(),
            endDate = binding.edtEndDate.text.toString().trim(),
        )

        val err = vm.validate(v)
        if(err != ""){
            errorDialog(err)
            return
        }
        vm.set(v)
        if (f != null) {
            vm.setCount(f)
        }
        successDialog("Voucher added successfully")
        nav.navigateUp()
    }

}
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
import com.example.alpha.data.Voucher
import com.example.alpha.data.VoucherViewModel
import com.example.alpha.databinding.FragmentUpdateVoucherBinding
import com.example.alpha.util.errorDialog
import com.example.alpha.util.successDialog

class UpdateVoucherFragment : Fragment() {

    private lateinit var binding: FragmentUpdateVoucherBinding
    private val nav by lazy { findNavController() }
    private val vm: VoucherViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentUpdateVoucherBinding.inflate(inflater,container,false)

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

    private fun load(f: Voucher) {
        binding.txtId.text = f.docId
        binding.edtName.setText(f.name)
        binding.edtCode.setText(f.code)
        binding.edtValue.setText(f.value.toString())
        setStatus(f.status)

        binding.edtName.requestFocus()
    }

    private fun setStatus(status: Int) {
        when (status) {
            1 -> binding.spnStatus.setSelection(1)
            0 -> binding.spnStatus.setSelection(0)
        }
    }

    private fun submit() {
        var select =0
        when (binding.spnStatus.selectedItem) {
            "Valid" -> select = 1
            "Invalid" -> select = 0
            else -> -1
        }

        val f = Voucher(
            docId = id,
            name = binding.edtName.text.toString().trim(),
            status = select,
            code = binding.edtCode.text.toString().trim(),
            value = binding.edtValue.text.toString().toDoubleOrNull()?:0.0,
        )

        val err = vm.validate(f,false)
        if(err != ""){
            errorDialog(err)
            return
        }

        vm.set(f)
        successDialog("Voucher Updated")
        nav.navigateUp()
    }


}
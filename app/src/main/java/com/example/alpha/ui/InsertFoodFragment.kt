package com.example.alpha.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.alpha.R
import com.example.alpha.data.Count
import com.example.alpha.data.Food
import com.example.alpha.data.FoodViewModel
import com.example.alpha.data.Seller
import com.example.alpha.databinding.FragmentInsertFoodBinding
import com.example.alpha.util.cropToBlob
import com.example.alpha.util.errorDialog
import com.example.alpha.util.successDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking


class InsertFoodFragment : Fragment() {

    private lateinit var binding: FragmentInsertFoodBinding
    private val nav by lazy { findNavController() }
    private val vm: FoodViewModel by activityViewModels()
    private val shopId by lazy { requireArguments().getString("shopId") ?: "" }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imgPhoto.setImageURI(it.data?.data)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentInsertFoodBinding.inflate(inflater,container,false)

        val spinner: Spinner = binding.spnStatus
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.cat_Status,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        var name = ArrayList<String>()

        Firebase.firestore
            .collection("Seller")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.toObjects<Seller>()
                var result = ""

                list.forEach{ f->
                    if(f.status == 1){
                        name.add(f.name)
                    }
                }
            }

        name.add("--SELECT--")
        val arrayAdapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_item,name)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnShop.adapter = arrayAdapter

        reset()
        binding.imgPhoto.setOnClickListener { select() }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnSubmit.setOnClickListener { submit() }

        return binding.root
    }

    private fun select() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun reset(){
        binding.edtName.text.clear()
        binding.imgPhoto.setImageDrawable(null)
        binding.edtDescription.text.clear()
        binding.edtPrice.text.clear()
        binding.spnShop.setSelection(0)
        binding.spnStatus.setSelection(0)

        binding.edtName.requestFocus()
    }

    private fun submit(){
        val c = vm.getCount("CountFood")
        var count = c?.count.toString().toIntOrNull()?:0
        count += 5000 + 1

        var setCount = count - 5000
        var cc = Count(
            docId = "CountFood",
            count = setCount
        )

        val f = Food(
            id = count.toString(),
            name = binding.edtName.text.toString().trim(),
            status = binding.spnStatus.selectedItem.toString(),
            image = binding.imgPhoto.cropToBlob(300,300),
            description = binding.edtDescription.text.toString(),
            price = binding.edtPrice.text.toString().toDoubleOrNull()?:0.00,
            applicationId = shopId
        )

        val err = vm.validate(f)
        if(err != ""){
            errorDialog(err)
            return
        }

        vm.set(f)
        vm.setCount(cc)
        successDialog("Food added successfully")
        nav.navigateUp()

    }


}
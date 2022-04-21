package com.example.alpha.manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.example.alpha.R
import com.example.alpha.databinding.FragmentEditStaffBinding
import com.example.alpha.user.UserViewModel
import com.example.alpha.util.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class EditStaffFragment : Fragment() {
    private lateinit var binding: FragmentEditStaffBinding
    private val userDb by lazy { Firebase.firestore.collection("Users")}
    private val id by lazy { requireArguments().getString("id") ?: "" }
    private val model: UserViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditStaffBinding.inflate(inflater, container, false)

        val username = binding.usernameTextInput
        val usernameLayout = binding.usernameTextInputLayout
        val name = binding.nameTextInput
        val nameLayout = binding.nameTextInputLayout
        val email = binding.emailTextInput
        val emailLayout = binding.emailTextInputLayout
        val phone = binding.phoneTextInput
        val phoneLayout = binding.phoneTextInputLayout

        val staff = model.getStaff(id)
        username.setText(staff.username)
        name.setText(staff.name)
        email.setText(staff.email)
        if(staff.phone!="-1") {
            phone.setText(staff.phone)
        }
        when(staff.status){
            1->binding.activeRB.isChecked = true
            2->binding.blockedRB.isChecked = true
            else -> binding.blockedRB.isChecked = true
        }

        when(staff.gender){
            0 -> binding.maleRB.isChecked = true
            1 -> binding.femaleRB.isChecked = true
            else -> ""
        }

        var validationItem = mutableMapOf(
            "name" to true,
            "email" to true,
            "phone" to true
        )

        var validationItemMsg = mutableMapOf(
            "name" to getString(R.string.blank_name),
            "email" to getString(R.string.blank_email),
            "phone" to getString(R.string.invalid_phone)
        )

        name.addTextChangedListener(
            afterTextChanged = {
                when{
                    isEmptyString(name.text.toString()) -> {
                        nameLayout.error = validationItemMsg["name"].toString()
                        validationItem["name"] = false
                    }
                    else -> {
                        nameLayout.error = null
                        validationItem["name"] = true
                    }
                }
            }
        )

        email.addTextChangedListener(
            afterTextChanged = {
                when{
                    isEmptyString(email.text.toString()) -> {
                        emailLayout.error = validationItemMsg["email"].toString()
                        validationItem["email"]=false
                    }
                    (!validateEmail(email.text.toString().trim())) ->{
                        validationItemMsg["email"] = getString(R.string.invalid_email)
                        emailLayout.error = validationItemMsg["email"].toString()
                        validationItem["email"] = false
                    }
                    else -> {
                        emailLayout.error = null
                        validationItem["email"] = true
                    }
                }
            }
        )

        var formatPhoneTime = 0
        phone.addTextChangedListener (
            afterTextChanged = {
                when{
                    (!isEmptyString(phone.text.toString()) && formatPhoneTime == 0)-> {
                        if(!validateMYPhone(phone.text.toString())){
                            phoneLayout.error = validationItemMsg["phone"]
                            validationItem["phone"] = false
                            formatPhoneTime = 0
                        }
                        if(validateMYPhone(phone.text.toString())){
                            phoneLayout.error = null
                            validationItem["phone"] = true
                            formatPhoneTime+=1
                            phone.setText(formatMYPhone(phone.text.toString()))
                        }
                    }
                    (!isEmptyString(phone.text.toString()) && formatPhoneTime == 1)-> {
                        if(!validateMYPhone(phone.text.toString())){
                            phoneLayout.error = validationItemMsg["phone"]
                            formatPhoneTime = 0
                            validationItem["phone"] = false
                        }
                    }
                    else -> {
                        phoneLayout.error = null
                        validationItem["phone"] = true
                    }
                }
            }
        )

        binding.updateStaffBtn.setOnClickListener {
            if(checkValidation(validationItem)){
                var updateProfile = mutableMapOf<String, Any>()
                updateProfile["name"] = name.text.toString()
                updateProfile["email"] = email.text.toString()
                if(phone.text.toString().trim()!="-1" || phone.text.toString().trim()!=""){
                    updateProfile["phone"] = phone.text.toString()
                }
                val gender = when{
                    binding.maleRB.isChecked -> 0
                    binding.femaleRB.isChecked -> 1
                    else -> null
                }

                gender?.let{
                    updateProfile["gender"] = it
                }

                updateProfile["image"] = binding.image.cropToBlob(300,300)

                val status = when{
                    binding.activeRB.isChecked -> 1
                    binding.blockedRB.isChecked -> 2
                    else -> null
                }

                status?.let{
                    updateProfile["status"] = status
                }

                userDb.document(id).update(updateProfile)
                    .addOnSuccessListener {
                        activity?.let { it2 -> "Update Profile Successfully".showToast(it2) }
                    }.addOnFailureListener {
                        activity?.let { it2 -> "Update Failed. Please Try Again".showToast(it2) }
                    }
            }else{

                if(validationItem["name"] == false){
                    nameLayout.error = validationItemMsg["name"]
                }

                if(validationItem["email"] == false){
                    emailLayout.error = validationItemMsg["email"]
                }

                if(validationItem["phone"] == false){
                    phoneLayout.error = validationItemMsg["phone"]
                }
            }
        }

        return binding.root
    }

}
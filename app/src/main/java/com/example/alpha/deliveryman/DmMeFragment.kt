package com.example.alpha.deliveryman

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alpha.R
import com.example.alpha.databinding.FragmentDmMeBinding
import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.alpha.LoginActivity
import com.example.alpha.databinding.FragmentMngMeBinding
import com.example.alpha.ui.LoginViewModel
import com.example.alpha.util.rmUserSR
import com.example.alpha.util.toBitmap


class DmMeFragment : Fragment() {
    private lateinit var binding: FragmentDmMeBinding
    private val model: LoginViewModel by activityViewModels()
    private val nav by lazy{ findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.actionBar?.setDisplayHomeAsUpEnabled(false)
        activity?.actionBar?.setHomeButtonEnabled(false)
        setHasOptionsMenu(false)
        // Inflate the layout for this fragment
        binding = FragmentDmMeBinding.inflate(inflater, container, false)
        binding.userNameTxt.text = model.user.value?.username.toString()
        binding.nameTxt.text = model.user.value?.name.toString()
        binding.image.setImageBitmap(model.image.value?.toBitmap())

        binding.profileBtn.setOnClickListener {
            nav.navigate(R.id.action_dmMeFragment_to_profileFragment)
        }

        binding.changePwdBtn.setOnClickListener {
            nav.navigate(R.id.action_dmMeFragment_to_accountChangePasswordFragment2)
        }

        binding.deliveryHistoryBtn.setOnClickListener {
            nav.navigate(R.id.action_dmMeFragment_to_deliveryHistoryFragment)
        }

        binding.logOutBtn.setOnClickListener {
            activity?.let { it1 -> rmUserSR(it1) }
            var intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        model.user.observe(viewLifecycleOwner, Observer {
            if(it == null){
                return@Observer
            }
            binding.userNameTxt.text = it.username
            binding.nameTxt.text = it.name
        })



        return binding.root
    }

}
package com.example.alpha.manager

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.alpha.R
import com.example.alpha.databinding.ActivityManagerBinding
import com.example.alpha.ui.LoginViewModel
import com.example.alpha.util.BaseActivity
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ManagerActivity : BaseActivity() {
    private val nav by lazy { supportFragmentManager.findFragmentById(R.id.managerNavHost)!!.findNavController() }
    private lateinit var binding: ActivityManagerBinding
    private lateinit var loginViewModel: LoginViewModel
    private val db by lazy { Firebase.firestore.collection("Users")}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNav = binding.managerBottomNav
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.managerNavHost) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNav, navController)

        setupActionBarWithNavController(nav)
    }

    override fun onSupportNavigateUp(): Boolean {
        return nav.navigateUp() || super.onSupportNavigateUp()
    }
}
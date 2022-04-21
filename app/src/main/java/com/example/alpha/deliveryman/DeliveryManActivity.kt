package com.example.alpha.deliveryman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.alpha.R
import com.example.alpha.databinding.ActivityDeliveryManBinding
import com.example.alpha.databinding.ActivityManagerBinding
import com.example.alpha.ui.LoginViewModel
import com.example.alpha.util.BaseActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DeliveryManActivity : BaseActivity() {
    private val nav by lazy { supportFragmentManager.findFragmentById(R.id.dmNavHost)!!.findNavController() }
    private lateinit var model: PickUpViewModel
    private lateinit var loginViewModel: LoginViewModel

    private lateinit var binding: ActivityDeliveryManBinding
    private val userDb by lazy { Firebase.firestore.collection("Users")}
    private val orderDb by lazy { Firebase.firestore.collection("Order")}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryManBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNav = binding.dmBottomNav
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.dmNavHost) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNav, navController)

        setupActionBarWithNavController(nav)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        var userId = loginViewModel.user.value?.id

        model = ViewModelProvider(this).get(PickUpViewModel::class.java)

        model.pickupStatus.observe(this, Observer {
            if(it.pickUpStatus == 0){

            }

            if(it.pickUpStatus == 1){

            }


        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return nav.navigateUp() || super.onSupportNavigateUp()
    }
}
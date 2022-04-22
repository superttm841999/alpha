package com.example.alpha.deliveryman

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
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
    private val order = Firebase.firestore.collection("Order")
    private lateinit var binding: ActivityDeliveryManBinding
    private val userDb by lazy { Firebase.firestore.collection("Users")}
    private val orderDb by lazy { Firebase.firestore.collection("Order")}
    private var orderIdIng: String? = null

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

        orderDb.whereEqualTo("deliveryMan", userId).whereEqualTo("status", 1).addSnapshotListener { value, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (value?.documents?.size == 0) {
                model.canPickUpAnother.value = Another(true, 0)
                Log.d("oi", "true")
            }else{
                var index = 0
                var rindex = 0
                if (value != null) {
                    for (so in value.documents){
                        if(value?.documents?.get(index)?.data?.get("progress").toString().toInt() == 3){
                            index++
                        }else{
                            rindex = index
                        }
                    }
                }

                if(index == value?.documents?.size){
                    model.canPickUpAnother.value =
                        Another(true, 0)
                }else {
                    var orderStatus = value?.documents?.get(rindex)?.data
                    if (model.pickupStatus.value.toString() == "null") {
                        var pickupStatus = orderStatus?.get("progress").toString().toInt() - 1
                        var id = value?.documents?.get(rindex)?.id.toString()
                        val progress = orderStatus?.get("progress").toString().toInt()
                        model.pickupStatus.value = PickUpStatus(id, pickupStatus)
                        statusDialog(orderStatus?.get("progress").toString().toInt())
                    }

                    if (orderStatus?.get("progress").toString().toInt() == 3) {
                        model.canPickUpAnother.value =
                            Another(true, orderStatus?.get("progress").toString().toInt())

                    }else if(orderStatus?.get("progress").toString().toInt() == 1){
                        model.canPickUpAnother.value =
                            Another(false, orderStatus?.get("progress").toString().toInt())
                        var pickupStatus = orderStatus?.get("progress").toString().toInt() - 1
                        var id = value?.documents?.get(rindex)?.id.toString()
                        model.pickupStatus.value = PickUpStatus(id, pickupStatus)
                        statusDialog(orderStatus?.get("progress").toString().toInt(), 1)

                    }else {
                        model.canPickUpAnother.value =
                            Another(false, orderStatus?.get("progress").toString().toInt())
                        var pickupStatus = orderStatus?.get("progress").toString().toInt() - 1
                        var id = value?.documents?.get(rindex)?.id.toString()
                        model.pickupStatus.value = PickUpStatus(id, pickupStatus)
                        statusDialog(orderStatus?.get("progress").toString().toInt(), 1)
                    }
                }
                Log.d("oi", "false")
            }
        }
    }

    private fun statusDialog(status: Int, firstPick: Int = 0){
        if(status == 3){
            return
        }
        var message = when(status){
            1 -> {
                if(firstPick == 1){
                    "Please start your delivery"
                }else{

                    "Please go to take your accepted order food from seller"
                }

            }
            2 -> {
                if(firstPick == 1){
                    "Please continue your delivery"
                }else{

                    "Please send your picked food to customer"
                }

            }
            else -> ""
        }
        var dialog = AlertDialog.Builder(this)
            .setTitle("Delivery Order Status")
            .setMessage(message)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                when(status){
                    1 -> {
                        if (firstPick == 0) {
                            dialogInterface.dismiss()
                        }else{
                            nav.navigate(R.id.pickUpSellerFragment)
                            dialogInterface.dismiss()
                        }

                    }
                    2 -> {
                        if (firstPick == 0) {
                            dialogInterface.dismiss()
                        }else{
                            nav.navigate(R.id.deliveryToCustomerFragment)
                            dialogInterface.dismiss()
                        }
                    }

                    else -> {
                        nav.navigate(R.id.takeOrderListFragment)
                        dialogInterface.dismiss()
                    }
                }


            }).create()
        dialog.setCancelable(false)
        dialog.show()
    }



    //pickupStatus 0 - take order, 1 - pick up from store, 2 - finish

    override fun onSupportNavigateUp(): Boolean {
        return nav.navigateUp() || super.onSupportNavigateUp()
    }
}
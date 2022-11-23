package com.curb.curbuserapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.curb.curbuserapplication.databinding.ActivityAccountDetailsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


//import com.example.curb.databinding.ActivityAccountDetailsBinding

class AccountDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAccountDetailsBinding
    var userID=""
    var phoneNumberr=""
    var userNamee = ""
    var userEmaill = ""
    var userData = UserClass()
    var constraintLayout : ConstraintLayout? =  null
    var networkConnectionForAccountDetails : IsInternetAvailable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewUtils.enableTransparentStatusBar(window)
        binding.goBackButton.setOnClickListener {
            finish()
        }
        val intent = intent
        userID = intent.getStringExtra("userID").toString()
        userNamee = intent.getStringExtra("userName").toString()
        phoneNumberr = intent.getStringExtra("userPhoneNumber").toString()
        userEmaill = intent.getStringExtra("userEmail").toString()
        binding.showMobileDetails.userDetailText!!.text = phoneNumberr.toString()
        binding.showNameDetails.userDetailText!!.text = userNamee.toString()
        binding.showEmailDetails.userDetailText!!.text = userEmaill.toString()
        networkConnectionForAccountDetails = IsInternetAvailable(applicationContext)
        constraintLayout = findViewById(R.id.activity_account_constraint_layout)
        binding.showNameDetails.editDataButton?.setOnClickListener {
            binding.showNameDetails.visibility = View.GONE
            binding.editNameView.visibility = View.VISIBLE
            binding.editNameView.editDetialsUi!!.setText(userNamee.toString())
            if(!binding.UpdateChangesButton.isVisible){
                binding.UpdateChangesButton.visibility = View.VISIBLE
            }
        }
        binding.userNameOfTheUserToNeEdited.text = userNamee.toString()
        binding.showMobileDetails.editDataButton!!.visibility = View.GONE
        binding.showMobileDetails.verifyPhonenumberLogo!!.visibility = View.VISIBLE
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@AccountDetailsActivity,SettingsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
//        binding.showMobileDetails.editDataButton?.setOnClickListener {
//            binding.showMobileDetails.visibility = View.GONE
//            binding.editMobileView.visibility = View.VISIBLE
//            binding.editMobileView.editDetialsUi!!.setText(phoneNumberr.toString())
//            if(!binding.UpdateChangesButton.isVisible){
//                binding.UpdateChangesButton.visibility = View.VISIBLE
//            }
//            binding.editMobileView.editDetialsUi!!.doOnTextChanged { text, start, before, count ->
//                if(binding.editMobileView.editDetialsUi!!.length() < 10){
//                    binding.editMobileView.editDetialsUi!!.error = "Enter a valid mobile number"
//                }
//                else{
//                    binding.editMobileView.editDetialsUi!!.error = "perfect"
//                }
//            }
//        }

        binding.showEmailDetails.editDataButton?.setOnClickListener {
            binding.showEmailDetails.visibility = View.GONE
            binding.editEmailView.visibility = View.VISIBLE
            binding.editEmailView.editDetialsUi!!.setText(userEmaill.toString())
            if(!binding.UpdateChangesButton.isVisible){
                binding.UpdateChangesButton.visibility = View.VISIBLE
            }
            binding.editEmailView.editDetialsUi!!.doOnTextChanged { text, start, before, count ->
                if(Patterns.EMAIL_ADDRESS.matcher(binding.editEmailView.editDetialsUi!!.text.toString()).matches()){
                }
                else{
                    binding.editEmailView.editDetialsUi!!.error = "Enter a valid email ID"
                }
            }
        }
        binding.UpdateChangesButton.setOnClickListener {
//             userNamee= binding.editNameView.editDetialsUi!!.text.toString()
//            phoneNumberr = binding.editMobileView.editDetialsUi!!.text.toString()
            networkConnectionForAccountDetails!!.observe(this, androidx.lifecycle.Observer { isConnected->
                if(isConnected) {
                    if(binding.editNameView.visibility == View.VISIBLE){
                        binding.editNameView.visibility = View.GONE
                        binding.showNameDetails.visibility = View.VISIBLE
                        userNamee= binding.editNameView.editDetialsUi!!.text.toString()
                        binding.showNameDetails.userDetailText!!.text = userNamee
                        binding.userNameOfTheUserToNeEdited.text = userNamee
                        updateUserDetails()
                        binding.UpdateChangesButton.visibility = View.GONE
                    }
                    if(binding.editEmailView.visibility == View.VISIBLE){
                        binding.editEmailView.visibility = View.GONE
                        userEmaill = binding.editEmailView.editDetialsUi!!.text.toString()
                        binding.showEmailDetails.visibility = View.VISIBLE
                        binding.showEmailDetails.userDetailText!!.text = userEmaill.toString()
                        updateUserDetails()
                        binding.UpdateChangesButton.visibility = View.GONE
                    }
                }
                else{
                    val snackBar = Snackbar.make(constraintLayout!!, "You must have an internet connection!", Snackbar.LENGTH_INDEFINITE)
                    snackBar.setActionTextColor(resources.getColor(R.color.teal_700))
                    val snackbarView: View = snackBar.view
                    ViewCompat.setFitsSystemWindows(snackbarView, true)
                    ViewCompat.setOnApplyWindowInsetsListener(snackbarView, null)
                    val sbView: View = snackbarView
                    snackBar.setTextColor(Color.WHITE)
                    sbView.setBackgroundColor(Color.DKGRAY)
                    snackBar.setAction("OK") { // Call your action method here
                        snackBar.dismiss()
                    }
                    snackBar.show()
                }
            })
        }
    }
    private fun updateUserDetails(){
        val userName = binding.showNameDetails.userDetailText!!.text
        val phoneNumber = binding.showMobileDetails.userDetailText!!.text
        val emailId = binding.showEmailDetails.userDetailText!!.text
        val db = FirebaseFirestore.getInstance()
        val trips = hashMapOf(
            "displayName" to userName,
            "phoneNumber" to phoneNumber,
            "email" to emailId

        )
        db.collection("users")
            .whereEqualTo("userId", userID)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //  val tripId = db.collection("tripDetails").document().id

                    db.collection("users").document(document.id).set(trips, SetOptions.merge())

                }
            }

    }
}
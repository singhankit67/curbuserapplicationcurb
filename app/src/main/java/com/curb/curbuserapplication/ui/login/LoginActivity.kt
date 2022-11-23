package com.curb.curbuserapplication.ui.login

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.curb.curbuserapplication.AutoDetectOTP
import com.curb.curbuserapplication.MapActivity
import com.curb.curbuserapplication.R
import com.curb.curbuserapplication.SignoutBottomsheet
import com.curb.curbuserapplication.auth.PhoneAuth
import com.curb.curbuserapplication.databinding.ActivityLoginBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private val phoneAuth: PhoneAuth = PhoneAuth()
    var phoneNumberString = ""
    var nameString = ""
    var phoneOTP = ""
    var checker = ""
    private var autoDetectOTP: AutoDetectOTP? = null
    private lateinit var mAuth: FirebaseAuth
    private var mapFragment : SupportMapFragment ? = null
    private lateinit var mMap: GoogleMap

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val w: Window = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        autoDetectOTP = AutoDetectOTP(this)
        autoDetectOTP!!.requestPhoneNoHint()

        mAuth = FirebaseAuth.getInstance()
        mapFragment = supportFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment!!.getMapAsync(this)

//        val name = binding.name
//        val phoneNumber = binding.phoneNumber
//        val oneTimePassword = binding.OTP
//        val login = binding.login
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        binding.resendOtpButton!!.setOnClickListener {
            binding.resendOtpButton!!.visibility = View.GONE
            binding.resendCodeInLayout!!.visibility = View.VISIBLE
            binding.timer!!.visibility = View.VISIBLE
            phoneAuth.sendVerificationCode(phoneNumberString, this@LoginActivity)
            object : CountDownTimer(30000, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    binding.timer!!.text = (millisUntilFinished / 1000).toString()
//                    binding.resendOtpButton!!.setTextColor(resources.getColor(R.color.lightGrey))
//                    binding.resendOtpButton!!.isEnabled = false
//                    binding.resendOtpButton!!.isClickable = false
                    if(binding.timer!!.text == "0"){
                        binding.resendCodeInLayout!!.visibility = View.GONE
                        binding.resendOtpButton!!.visibility = View.VISIBLE
                        binding.resendOtpButton!!.isClickable = true
                        phoneAuth.sendVerificationCode(phoneNumberString, this@LoginActivity)
                    }
                }
                override fun onFinish() {
                    binding.resendCodeInLayout!!.visibility = View.GONE
                }
            }.start()
        }
//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if( binding.loginCardLayoutForFrontSide!!.visibility == View.VISIBLE) {
//                    binding.loginCardLayoutForBackside!!.visibility = View.GONE
//                    binding.loginCardLayoutForFrontSide!!.visibility = View.VISIBLE
//                }
//                else if( binding.loginCardLayoutForFrontSide!!.visibility == View.VISIBLE){
//                    finish()
//                }
//            }
//        }
//        this@LoginActivity.onBackPressedDispatcher.addCallback(this@LoginActivity,callback)
//        binding.changeNumberButton!!.setOnClickListener { binding.loginCardLayoutForBackside!!.visibility = View.GONE
//            binding.loginCardLayoutForFrontSide!!.visibility = View.VISIBLE}


        binding.verifyButton!!.setOnClickListener {
            if(TextUtils.isEmpty(binding.OTP!!.text.toString())){

            }
            else
            {
                verifyCode(binding.OTP!!.text.toString())
            }
        }
//            binding.verifyButton!!.setBackgroundResource(R.drawable.green_button)
//            binding.verifyButton!!.setTextColor(resources.getColor(R.color.white))
//            binding.verifyButton!!.isEnabled = true
//            binding.verifyButton!!.isClickable = true
//            if (oneTimePassword!!.text.toString() == phoneAuth.verificationId) {
//                println("reached here line 141")
//                loginWithCredentials()
//            }


        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both name / phoneNumber is valid
            // login.isEnabled = loginState.isDataValid
            if(loginState.isDataValid){
                if(binding.phoneNumber.length() == 10) {
                    binding.loginWithMobileNumber!!.setBackgroundResource(R.drawable.green_button)
                    binding.loginWithMobileNumber!!.setTextColor(resources.getColor(R.color.white))
                    binding.loginWithMobileNumber!!.isEnabled = true
                    binding.loginWithMobileNumber!!.isClickable = true
                }
            }
            else if(!loginState.isDataValid){
                binding.loginWithMobileNumber!!.setBackgroundResource(R.drawable.otp_field_ui)
                binding.loginWithMobileNumber!!.setTextColor(resources.getColor(R.color.lightGrey))
                binding.loginWithMobileNumber!!.isEnabled = false
                binding.loginWithMobileNumber!!.isClickable = false
            }

            if (loginState.nameError != null) {
                binding.name.error = getString(loginState.nameError)
            }
            if (loginState.phoneNumberError != null) {
                binding.phoneNumber.error = getString(loginState.phoneNumberError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            //  loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if(loginResult.success != null && checker == "AutoVerificationActiviated"){
//                binding.verifyButton!!.visibility = View.GONE
//                binding.resendOtpButton!!.visibility = View.GONE
//                binding.verificationAnim!!.visibility = View.GONE
//                binding.OTP!!.visibility = View.GONE
//                binding.phoneNoTextSent!!.visibility = View.GONE
//                binding.verificationText!!.visibility = View.GONE
//                binding.timer!!.visibility = View.GONE
//                binding.otpSentOnText!!.visibility = View.GONE
                binding.loginCardLayoutForBackside!!.visibility = View.GONE
                binding.loadingAnimForLoginActToMapAct!!.visibility = View.VISIBLE
                binding.loadingAnimForLoginActToMapAct!!.playAnimation()
                updateUiWithUser(loginResult.success)
                finish()
            }
            if (loginResult.success != null) {
                binding.OTP!!.setText(phoneOTP)
                binding.verifyButton!!.setBackgroundResource(R.drawable.green_button)
                binding.verifyButton!!.setTextColor(resources.getColor(R.color.white))
                binding.verifyButton!!.setOnClickListener {
//                    binding.verifyButton!!.visibility = View.GONE
//                    binding.resendOtpButton!!.visibility = View.GONE
//                    binding.verificationAnim!!.visibility = View.GONE
//                    binding.OTP!!.visibility = View.GONE
//                    binding.phoneNoTextSent!!.visibility = View.GONE
//                    binding.verificationText!!.visibility = View.GONE
//                    binding.timer!!.visibility = View.GONE
//                    binding.otpSentOnText!!.visibility = View.GONE
                    binding.loginCardLayoutForBackside!!.visibility = View.GONE
                    binding.loadingAnimForLoginActToMapAct!!.visibility = View.VISIBLE
                    binding.loadingAnimForLoginActToMapAct!!.playAnimation()
                    updateUiWithUser(loginResult.success)


                    setResult(Activity.RESULT_OK)
                    //Complete and destroy login activity once successful
                    finish()}
            }
        })

        binding.name.afterTextChanged {
            nameString = binding.name.text.toString()
            phoneNumberString = binding.phoneNumber.text.toString()
            loginViewModel.loginDataChanged(
                nameString,
                phoneNumberString
            )
        }

        binding.phoneNumber.apply {
            afterTextChanged {
                nameString = binding.name.text.toString()
                phoneNumberString = binding.phoneNumber.text.toString()
                loginViewModel.loginDataChanged(
                    nameString,
                    phoneNumberString
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        binding.name.keyListener = null
                        binding.phoneNumber.keyListener = null
                        nameString = binding.name.text.toString()
                        phoneNumberString = this.text.toString()
                        authenticatePhoneNumber(phoneNumberString)
                        binding.OTP!!.visibility = View.VISIBLE

                    }
                }
                false
            }
//            backFace.visibility = View.VISIBLE
            binding.loginWithMobileNumber!!.setOnClickListener {
                nameString = binding.name.text.toString()
                phoneNumberString = this.text.toString()
                authenticatePhoneNumber(phoneNumberString)
                binding.name.keyListener = null
                binding.phoneNumber.keyListener = null
                binding.phoneNoTextSent!!.text =  "+91"+ binding.phoneNumber.text.toString()
                binding.loginCardLayoutForFrontSide!!.visibility = View.GONE
                binding.loginCardLayoutForBackside!!.visibility = View.VISIBLE
                object : CountDownTimer(30000, 1000) {
                    @SuppressLint("SetTextI18n")
                    override fun onTick(millisUntilFinished: Long) {
                        binding.timer?.text = (millisUntilFinished / 1000).toString()
                    }
                    override fun onFinish() {
                        binding.resendCodeInLayout!!.visibility = View.GONE
                        binding.resendOtpButton!!.visibility = View.VISIBLE
                        binding.resendOtpButton!!.isEnabled = true
                        binding.resendOtpButton!!.isClickable = true
                        binding.resendOtpButton!!.setTextColor(resources.getColor(R.color.teal_700))
                        binding.timer!!.visibility = View.GONE
                    }
                }.start()



                println("reached here line 127")
//
            }
        }
        binding.OTP?.apply {
            afterTextChanged {
//                oneTimePassword.setText(phoneOTP)
                if (binding.OTP!!.text.length == 6) {
                    binding.verifyButton!!.setBackgroundResource(R.drawable.green_button)
                    binding.verifyButton!!.setTextColor(resources.getColor(R.color.white))
                    binding.verifyButton!!.isEnabled = true
                    binding.verifyButton!!.isClickable = true
                }
            }
        }

        phoneAuth.userCredential.observe(this, Observer<PhoneAuthCredential>() {
            phoneAuth.signIn(it, ::loginWithCredentials)
        })
        autoDetectOTP!!.startSmsRetriver(object : AutoDetectOTP.SmsCallback {
            override fun connectionfailed() {

            }
            override fun connectionSuccess(aVoid: Void?) {


            }
            override fun smsCallback(sms: String?) {
                val otp  = sms!!.replace("<#> Your ExampleApp code is:","") .split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]  //!!.substring(sms.indexOf(":") + 1, sms.indexOf(".")).trim()
                binding.OTP!!.setText(otp)
                checker = "AutoVerificationActiviated"
                binding.verifyButton!!.visibility = View.GONE
            }
        })
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(phoneAuth.verificationId, code)
        // phoneAuth.userCredential.observe(this, Observer<PhoneAuthCredential>() {
        // phoneAuth.signIn(credential, ::loginWithCredentials)
        // })
        signInWithCredential(credential)
        // binding.timer?.visibility = View.GONE
    }
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if(task.isSuccessful){
                        loginWithCredentials()
                        val intent = Intent(this@LoginActivity, MapActivity::class.java)
                        startActivity(intent)
                        finish()
//                        startActivity(Intent(this,MapActivity::class.java))
                    }

                }

            })
    }
    private fun authenticatePhoneNumber(phoneNumber: String) {
        phoneAuth.sendVerificationCode(phoneNumber, this)
    }
    private fun loginWithCredentials() {
        loginViewModel.login(nameString, phoneNumberString,"","","")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val phoneNumberTextBox = findViewById<EditText>(R.id.phoneNumber)
        if (requestCode == AutoDetectOTP.RC_HINT) {
            if (resultCode == RESULT_OK) {
                autoDetectOTP!!.getPhoneNo(data!!)
                val new_number = autoDetectOTP!!.getPhoneNo(data).substring(3)

                phoneNumberTextBox.setText(new_number)
            } else {
            }
        }
    }
    private fun updateUiWithUser(model: LoggedInUserView) {
        val displayName = model.displayName
        // TODO : initiate successful logged in experience

        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun showLoginFailed(@StringRes errorString: Int) {

    }
    private fun cancelRide(){

    }

    @SuppressLint("LongLogTag")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            var success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.googlemap_custom
                ))
            if (!success) {
                Log.e(SignoutBottomsheet.TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(SignoutBottomsheet.TAG, "Can't find style. Error: ", e)
        }
    }
}
/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

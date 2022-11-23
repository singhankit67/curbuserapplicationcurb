package com.curb.curbuserapplication.auth

import android.app.Activity
import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.curb.curbuserapplication.ui.login.LoginActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuth {
    private val mAuth = FirebaseAuth.getInstance()
    var verificationId = ""
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    val userCredential = MutableLiveData<PhoneAuthCredential>()

    private val mCallbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                progress.visibility = View.INVISIBLE
            println("reached line PhoneAuth.24")
            userCredential.postValue(credential)
            var code = credential.smsCode
            var loginAct = LoginActivity()
            //  var eT = loginAct.findViewById<EditText>(R.id.OTP)
            if (code != null) {
                //  eT.setText(code)
            }
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            println("reached here PhoneAuth line 27, e: $p0")
        }

        override fun onCodeSent(verfication: String, token: PhoneAuthProvider.ForceResendingToken) {
            println("reached line PhoneAuth.33")
            super.onCodeSent(verfication, token)
            verificationId = verfication
            resendToken = token
        }
    }

    fun sendVerificationCode(phoneNumber: String, callbackActivity: Activity) {
        val formattedPhoneNumber = PhoneNumberUtils.formatNumberToE164(phoneNumber, "IN")
        println("phone number: $phoneNumber")
        println("formattedPhoneNumber: $formattedPhoneNumber")
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(formattedPhoneNumber)       // Phone number to verify
            .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(callbackActivity)                 // Activity (for callback binding)
            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
            .build()
        println("reached line PhoneAuth.50")
        PhoneAuthProvider.verifyPhoneNumber(options)
        println("reached line PhoneAuth.52")
    }

    fun signIn(credential: PhoneAuthCredential, callback: () -> Unit) {
        mAuth.signInWithCredential(credential).addOnCompleteListener {
            callback()
        }
    }

}
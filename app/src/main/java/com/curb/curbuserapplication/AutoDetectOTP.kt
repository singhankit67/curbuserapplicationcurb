package com.curb.curbuserapplication

import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.credentials.IdentityProviders
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status

class AutoDetectOTP(context: Context) {
    private var googleApiClient: GoogleApiClient? = null
    private val context: Context
    private val appCompatActivity: AppCompatActivity = context as AppCompatActivity
    private var intentFilter: IntentFilter? = null
    private var smsCallback: SmsCallback? = null
    private var chargerReceiver: BroadcastReceiver? = null

    init {
        this.context = appCompatActivity.applicationContext
    }

    fun requestPhoneNoHint() {
        googleApiClient = GoogleApiClient.Builder(context)
            .enableAutoManage(
                appCompatActivity
            ) { }
            .addApi(Auth.CREDENTIALS_API)
            .build()
        val hintRequest = HintRequest.Builder()
            .setHintPickerConfig(
                CredentialPickerConfig.Builder()
                    .setShowCancelButton(true)
                    .build()
            )
            .setPhoneNumberIdentifierSupported(true)
//            .setAccountTypes(IdentityProviders.GOOGLE)
            .build()

        val intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient!!, hintRequest)
        try {
            appCompatActivity.startIntentSenderForResult(
                intent.intentSender,
                RC_HINT,
                null,
                0,
                0,
                0
            )
        } catch (e: IntentSender.SendIntentException) {
            Log.e("PHONE_HINT", "Could not start hint picker Intent", e)
        }

    }

    fun requestPhoneNoHint(callback: Callback) {
        googleApiClient = GoogleApiClient.Builder(context)
            .enableAutoManage(
                appCompatActivity
            ) { }
            .addApi(Auth.CREDENTIALS_API)
            .build()
        googleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(@Nullable bundle: Bundle?) {
                    callback.connectionSuccess(bundle)
                }

                override fun onConnectionSuspended(i: Int) {
                    callback.connectionSuspend(i)
                }
            })
            .enableAutoManage(
                appCompatActivity
            ) { connectionResult -> callback.connectionFailed(connectionResult) }
            .addApi(Auth.CREDENTIALS_API)
            .build()
        val hintRequest = HintRequest.Builder()
            .setHintPickerConfig(
                CredentialPickerConfig.Builder()
                    .setShowCancelButton(true)
                    .build()
            )
            .setPhoneNumberIdentifierSupported(true)
            .build()


        val intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient!!, hintRequest)
        try {
            appCompatActivity.startIntentSenderForResult(
                intent.intentSender,
                RC_HINT,
                null,
                0,
                0,
                0
            )
        } catch (e: IntentSender.SendIntentException) {
            Log.e("PHONE_HINT", "Could not start hint picker Intent", e)
        }

    }


    fun getPhoneNo(data: Intent): String {
        val cred = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
        return cred!!.id

    }

    fun startSmsRetriver(smsCallback: SmsCallback) {
        registerReceiver()
        this.smsCallback = smsCallback
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
// SMS message.
        val client = SmsRetriever.getClient(context)

// Starts SmsRetriever, which waits for ONE matching SMS message until timeout
// (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
// action SmsRetriever#SMS_RETRIEVED_ACTION.
        val task = client.startSmsRetriever()
        // Listen for success/failure of the start Task. If in a background thread, this
// can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener { aVoid ->
            Log.e("SMSRE", "success")
            smsCallback.connectionSuccess(aVoid)
        }
        task.addOnFailureListener { smsCallback.connectionfailed() }
    }


    private fun registerReceiver() {
//        filter to receive SMS
        intentFilter = IntentFilter()
        intentFilter!!.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)

//        receiver to receive and to get otp from SMS
        chargerReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                    val extras = intent.extras
                    val status = extras!![SmsRetriever.EXTRA_STATUS] as Status?
                    when (status!!.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            // Get SMS message contents
                            val message = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                            // Extract one-time code from the message and complete verification
                            // by sending the code back to your server for SMS authenticity.
                            smsCallback?.smsCallback(message)
                            stopSmsReciever()
                        }
                        CommonStatusCodes.TIMEOUT ->                             // Waiting for SMS timed out (5 minutes)
                            smsCallback?.connectionfailed()
                    }
                }
            }
        }
        appCompatActivity.application.registerReceiver(chargerReceiver, intentFilter)
    }

    fun stopSmsReciever() {
        try {
            appCompatActivity.applicationContext.unregisterReceiver(chargerReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }


    interface Callback {
        fun connectionFailed(connectionResult: ConnectionResult)
        fun connectionSuspend(i: Int)
        fun connectionSuccess(bundle: Bundle?)
    }
    interface SmsCallback {
        fun connectionfailed()
        fun connectionSuccess(aVoid: Void?)
        fun smsCallback(sms: String?)
    }



    companion object {
        val RC_HINT = 1000

    }


}

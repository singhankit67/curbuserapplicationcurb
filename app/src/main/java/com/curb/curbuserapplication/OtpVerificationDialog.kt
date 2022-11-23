package com.curb.curbuserapplication

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.String
import java.util.ArrayList
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Int


class OtpVerificationDialog(context: Context) : Dialog(context) {
    lateinit var otpET1 : EditText
    lateinit var otpET2 : EditText
    lateinit var otpET3 : EditText
    lateinit var otpET4 : EditText
    var verifyAndProceedButton : AppCompatButton? = null
    var selectedETPosition = 0
    var mDialogResult: OnMyDialogResult? = null
    var getOTP = ""
    var db = FirebaseFirestore.getInstance()
    private var upcomingTripList = ArrayList<TripDetailsClass>()
    private var tripDetailsList = ArrayList<TripDetailsClass>()
    var calculationUtilClass = CalculationUtilsClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        window!!.setBackgroundDrawable(ColorDrawable(context.resources.getColor(android.R.color.transparent)))
        setContentView(R.layout.enter_otp_layout)
        otpET1 = findViewById(R.id.otpET1)
        otpET2 = findViewById(R.id.otpET2)
        otpET3 = findViewById(R.id.otpET3)
        otpET4 = findViewById(R.id.otpET4)
        verifyAndProceedButton = findViewById(R.id.verify_proceed_button)
        otpET1.addTextChangedListener(textWatcher)
        otpET2.addTextChangedListener(textWatcher)
        otpET3.addTextChangedListener(textWatcher)
        otpET4.addTextChangedListener(textWatcher)
        getDataWithCurrentUserId()
        upcomingTripList.sort()
        showKeyboard(otpET1)

        verifyAndProceedButton!!.setOnClickListener(OKListener())
    }
    private val textWatcher = object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if(s!!.isNotEmpty()){
                if(selectedETPosition == 0){
                    selectedETPosition = 1
                    showKeyboard(otpET2)

                }
                else if(selectedETPosition == 1){
                    selectedETPosition = 2
                    showKeyboard(otpET3)

                }
                else if(selectedETPosition == 2){
                    selectedETPosition = 3
                    showKeyboard(otpET4)

                }
                else{
                    if(otpET1.text.toString().trim().isNotEmpty() && otpET2.text.toString().trim().isNotEmpty() && otpET3.text.toString().trim().isNotEmpty() && otpET1.text.toString().trim().isNotEmpty()) {
                        verifyAndProceedButton!!.setBackgroundResource(R.drawable.green_button)
                        verifyAndProceedButton!!.setTextColor(context.resources.getColor(R.color.white))
                        verifyAndProceedButton!!.isEnabled = true
                        verifyAndProceedButton!!.isClickable = true
                    }
                    else{
                        verifyAndProceedButton!!.setBackgroundResource(R.drawable.otp_field_ui)
                        verifyAndProceedButton!!.setTextColor(context.resources.getColor(R.color.lightGrey))
                        verifyAndProceedButton!!.isEnabled = false
                        verifyAndProceedButton!!.isClickable = false
                    }
                }

            }
        }

    }
    private fun showKeyboard(otpET:EditText){
        otpET.requestFocus()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(otpET,InputMethodManager.SHOW_IMPLICIT)
    }
    private fun hideKeyboard(otpET:EditText){
        otpET.requestFocus()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(otpET,InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if(keyCode == KeyEvent.KEYCODE_DEL){
            if(selectedETPosition == 3){
                selectedETPosition = 2
                showKeyboard(otpET3)
            }
            else if(selectedETPosition == 2){
                selectedETPosition = 1
                showKeyboard(otpET2)
            }
            else if(selectedETPosition == 1){
                selectedETPosition = 0
                showKeyboard(otpET1)
            }
            verifyAndProceedButton!!.setBackgroundResource(R.drawable.otp_field_ui)
            verifyAndProceedButton!!.setTextColor(context.resources.getColor(R.color.lightGrey))
            verifyAndProceedButton!!.isEnabled = false
            verifyAndProceedButton!!.isClickable = false
            return true
        }
        else {
            return super.onKeyUp(keyCode, event)
        }
    }

    inner class OKListener : View.OnClickListener {
        override fun onClick(v: View?) {
            if (mDialogResult != null) {
                getOTP = otpET1.text.toString()+otpET2.text.toString()+otpET3.text.toString()+otpET4.text.toString()
                upcomingTripList.sort()
                try {
                    if (getOTP == upcomingTripList[0].OTP) {
                        mDialogResult!!.finish(String.valueOf(getOTP))
                        dismiss()
                        val imm =
                            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(verifyAndProceedButton!!.windowToken, 0)
                    } else {

                        otpET1.text.clear()
                        otpET2.text.clear()
                        otpET3.text.clear()
                        otpET4.text.clear()
                        Selection.setSelection(
                            otpET1.text as Editable,
                            otpET1.selectionStart
                        )
                        otpET1.requestFocus()
                        //   if(otpET1.text.toString().trim().isEmpty()) {
                        verifyAndProceedButton!!.setBackgroundResource(R.drawable.otp_field_ui)
                        verifyAndProceedButton!!.setTextColor(context.resources.getColor(R.color.lightGrey))
                        verifyAndProceedButton!!.isClickable = false
                        verifyAndProceedButton!!.isEnabled = false
                        // }
                    }
                }
                catch (e:Exception){

                    otpET1.text.clear()
                    otpET2.text.clear()
                    otpET3.text.clear()
                    otpET4.text.clear()
                    Selection.setSelection(
                        otpET1.text as Editable,
                        otpET1.selectionStart
                    )
                    otpET1.requestFocus()
                    //   if(otpET1.text.toString().trim().isEmpty()) {
                    verifyAndProceedButton!!.setBackgroundResource(R.drawable.otp_field_ui)
                    verifyAndProceedButton!!.setTextColor(context.resources.getColor(R.color.lightGrey))
                    verifyAndProceedButton!!.isClickable = false
                    verifyAndProceedButton!!.isEnabled = false
                }
            }
        }
    }
    @SuppressLint("LongLogTag", "SetTextI18n")
    private fun getDataWithCurrentUserId() {
        db.collection("tripDetails")
            .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener{ documents ->
                for(document in documents) {
                    Log.i(SignoutBottomsheet.TAG, "${document.id} => ${document.data}")
                    var OTP = document.getString("OTP")
                    var PaymentId = document.getString("PaymentId")
                    var carId = document.getString("carId")
                    var distance = document.getString("distance")
                    var driverId = document.getString("driverId")
                    var endTime = document.getString("endTime")
                    var price = document.getString("price")
                    var ridersRating = document.getString("ridersRating")
                    var startTime = document.getString("startTime")
                    var startLocationAddress = document.getString("startLocationAddress")
                    var endLocationAddress = document.getString("endLocationAddress")
                    var statuso = document.getString("status")
                    var tripId = document.getString("tripId")
                    var tripType = document.getString("tripType")
                    var userIdd = document.getString("userId")
                    var startLocationLat = document.getString("startLocationLat")
                    var startLocationLng = document.getString("startLocationLng")
                    var endLocationLat = document.getString("endLocationLat")
                    var endLocationLng = document.getString("endLocationLng")
                    var paymentStatus = document.getString("paymentStatus")
                    var timeTakenForRide = document.getString("timeTakenForRide")
                    var driverName = document.getString("driverName")
                    var driverContact = document.getString("driverContact")
                    var carbonEmission = document.getString("carbonEmissionValue")
                    //Log.d("TAG", document.getId() + " => " + document.getData());
                    var carName = document.getString("carName")
                    var orderId = document.getString("orderId")
                    var paymentMethod = document.getString("paymentMethod")
                    var tripDetail = TripDetailsClass(
                        OTP.toString(),
                        PaymentId.toString(),
                        carId.toString(),
                        distance.toString(),
                        driverId.toString(),
                        endTime.toString(),
                        price.toString(),
                        ridersRating.toString(),
                        startTime.toString(),
                        startLocationAddress.toString(),
                        endLocationAddress.toString(),
                        statuso.toString(),
                        tripId.toString(),
                        tripType.toString(),
                        userIdd.toString(),
                        startLocationLat.toString(),
                        startLocationLng.toString(),
                        endLocationLat.toString(),
                        endLocationLng.toString(),
                        paymentStatus.toString(),
                        timeTakenForRide.toString(),
                        driverName.toString(),
                        driverContact.toString(),
                        carbonEmission.toString(),
                        carName.toString(),
                        orderId.toString(),
                        paymentMethod.toString()
                    )

                    tripDetailsList.add(tripDetail)

                    var currentTineInMillis = calculationUtilClass.currentDateToEpochCoverter()
                    if (tripDetail.startTime != "null"){
                        if(tripDetail.startTime != "") {
                            if (tripDetail.startTime.toLong() > currentTineInMillis!! && tripDetail.statuso == "Schedule" || tripDetail.startTime.toLong() > currentTineInMillis && tripDetail.statuso == "Assign" || tripDetail.startTime.toLong() > currentTineInMillis && tripDetail.statuso == "Arrived") {
                                // var startDateInProperFormat  = getDateFromEpoch(startTime.toString())
                                var upcomingTripDetail = TripDetailsClass(
                                    OTP.toString(),
                                    PaymentId.toString(),
                                    carId.toString(),
                                    distance.toString(),
                                    driverId.toString(),
                                    endTime.toString(),
                                    price.toString(),
                                    ridersRating.toString(),
                                    startTime.toString(),
                                    startLocationAddress.toString(),
                                    endLocationAddress.toString(),
                                    statuso.toString(),
                                    tripId.toString(),
                                    tripType.toString(),
                                    userIdd.toString(),
                                    startLocationLat.toString(),
                                    startLocationLng.toString(),
                                    endLocationLat.toString(),
                                    endLocationLng.toString(),
                                    paymentStatus.toString(),
                                    timeTakenForRide.toString(),
                                    driverName.toString(),
                                    driverContact.toString(),
                                    carbonEmission.toString(),
                                    carName.toString(),
                                    orderId.toString(),
                                    paymentMethod.toString()

                                )
                                upcomingTripList.add(upcomingTripDetail)
                            }
                        }
                    }

                    //textView_result2!!.text = "${tripDetailsList.size}"
                }
                // textView_result1!!.text = tripDetailsList.size.toString()+"value"
                // textView_result2!!.text = tripDetailsList[0].toString()
            }
            .addOnFailureListener { exception ->

            }
    }

    fun setDialogResult(dialogResult: OnMyDialogResult) {
        mDialogResult = dialogResult
    }

    interface OnMyDialogResult {
        fun finish(result: kotlin.String?)
    }

}
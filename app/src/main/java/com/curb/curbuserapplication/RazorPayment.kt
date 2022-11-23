package com.curb.curbuserapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.functions.FirebaseFunctions
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class RazorPayment: Activity(), PaymentResultListener {
    var tripDetailClass = TripDetailsClass()
    var curbRideOrderId = "null"
    var userId = ""
    var tripId = ""
    var rideStatus = ""
    var rideStatusForPayment = ""
    var userIdForPayment = ""
    var tripIdForPayment = ""
    var ridePriceForCompletedRide = ""
    var rideDistanceForCompletedRide = ""
    var rideTimeForCompletedRide = ""
    var startLocationLatOfCompletedRide = ""
    var startLocationLngOfCompletedRide = ""
    var endLocationLatOfCompletedRide = ""
    var endLocationLngOfCompletedRide = ""
    var driverContact = ""
    var driverName = ""
    var calculationUtilClass = CalculationUtilsClass()
    var orderIdForTheRide = "nulll"
    val db = FirebaseFirestore.getInstance()
    val tripDetailListForRazor = ArrayList<TripDetailsClass>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val paymentData = intent.getSerializableExtra("paymentData") as PaymentData
        startPayment(paymentData)
        userId = intent.getStringExtra("userId").toString()
        tripId = intent.getStringExtra("tripId").toString()
        rideStatus = intent.getStringExtra("rideStatusF").toString()
        rideStatusForPayment = intent.getStringExtra("rideStatusF").toString()
        userIdForPayment = intent.getStringExtra("userId").toString()
        tripIdForPayment = paymentData.tripId
        startLocationLatOfCompletedRide = intent.getStringExtra("startLatitude").toString()
        startLocationLngOfCompletedRide = intent.getStringExtra("startLongitude").toString()
        endLocationLatOfCompletedRide = intent.getStringExtra("endLatitude").toString()
        endLocationLngOfCompletedRide = intent.getStringExtra("endLongitude").toString()
        rideDistanceForCompletedRide = intent.getStringExtra("RideDistance").toString()
        ridePriceForCompletedRide = intent.getStringExtra("RidePrice").toString()
        rideTimeForCompletedRide = intent.getStringExtra("RideTime").toString()
        driverContact = intent.getStringExtra("driverContact").toString()
        driverName = intent.getStringExtra("driverName").toString()
        // onBackPressed()

    }

    //    override fun onBackPressed() {
//        super.onBackPressed()
//        finish()
//    }
    private fun createOrderId(amountInPaisa: String, tripId: String) {
        // Create the arguments to the callable function, which are two integers
        val data = JSONObject()
        data.put("amountInPaisa",amountInPaisa)
        data.put("tripId",tripId)
        // Call the function and extract the operation from the result

        FirebaseFunctions.getInstance("asia-south1")
            .getHttpsCallable("function-razorpay-create-order-java")
            .call(data)
            .continueWith {
                if(it.isSuccessful){
                    if(rideStatus == "Completed") {
                        val db = FirebaseFirestore.getInstance()
                        val result = it.result?.data.toString()
                        val valueinJson = JSONObject(result)
                        val orderIdOnly = valueinJson.get("orderId")
                        val trips = hashMapOf("orderId" to orderIdOnly)
                        val query = db.collection("tripDetails")
                            .whereEqualTo("userId", userId)
                            .whereEqualTo("tripId", tripId)
                            .whereEqualTo("status", "Completed")
                            .get()
                        query.addOnSuccessListener { result ->
                            for (document in result) {
                                db.collection("tripDetails").document(document.id).set(trips, SetOptions.merge())

                            } }
                    }
                    else if(rideStatus == "Initiated"){
                        val db = FirebaseFirestore.getInstance()
                        val result = it.result?.data.toString()
                        val valueinJson = JSONObject(result)
                        val orderIdOnly = valueinJson.get("orderId")
                        val trips = hashMapOf("orderId" to orderIdOnly)
                        val query = db.collection("tripDetails")
                            .whereEqualTo("userId", userId)
                            .whereEqualTo("tripId",tripId)
                            // .whereEqualTo("status","Initiated")
                            .get()
                        query.addOnSuccessListener { result ->
                            for (document in result) {
                                db.collection("tripDetails").document(document.id).set(trips, SetOptions.merge())

                            }
                        }

                    }
                    else{

                    }

                }
                else{
                    Log.e("Test","Jaadu")
                }
            }
    }

    private fun startPayment(paymentData: PaymentData) {
        val checkout = Checkout()
        val amtInPaisa = paymentData.amountInPaisa
        val amtInPaisaInt = amtInPaisa.toInt()
        val amtInPasiaToString = amtInPaisaInt.toString()
        val db = FirebaseFirestore.getInstance()
        val intent = intent
        var userIdd = intent.getStringExtra("userId").toString()
        var tripIdd = intent.getStringExtra("tripId").toString()
        val docRef = db.collection("tripDetails")
            .whereEqualTo("userId",  userIdd)
            .whereEqualTo("tripId",tripIdd)
        docRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val orderIdd = document.getString("orderId")
                    orderIdForTheRide = orderIdd.toString()
                    if (orderIdd == null || orderIdd == "") {
                        createOrderId(amtInPasiaToString, paymentData.tripId)
                    } else if (orderIdd != null || orderIdd != "") {

                    }
                }
            }
        // tripDetailClass.PaymentId = curbRideOrderId
        // updateOrderIdAndPaymentStatusRecord()
        checkout.setKeyID(getRazorPayKeyId())
        try {
            Handler().postDelayed({val docRefForOrderId = db.collection("tripDetails")
                .whereEqualTo("userId",  userIdd)
                .whereEqualTo("tripId",tripIdd)
                docRefForOrderId.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val orderIddForRef = document.getString("orderId")
                            val options = JSONObject()
                            // TODO: Update to use constant
                            options.put("name", "Curb Mobility")
                            options.put("description", paymentData.description)
                            //You can omit the image option to fetch the image from dashboard
//            options.put("image", "http://yatiyamaha.com/images/yati-logo.png")
                            // TODO: Update to use app wide theme color
                            options.put("theme.color", "#1eb46f")
                            options.put("currency", paymentData.currency)
                            options.put("amount", paymentData.amountInPaisa)
                            options.put("order_id", orderIddForRef)

                            val preFill = JSONObject()
                            preFill.put("email", paymentData.Email)
                            preFill.put("contact", paymentData.phoneNumber)
                            options.put("prefill", preFill)
                            checkout.open(this, options)
                        }
                    }
            },1500)

        } catch (e: Exception){
            //TODO: Return to trip review page, and display error message
            println("Exception when trying to startPayment: $e")
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(paymentID: String) {

        try {
            tripDetailClass.paymentStatus = "Paid"
            if(rideStatusForPayment == "Initiated"){

                val newOTP = calculationUtilClass.getOTP(4)
                val paymentStatusForRide = tripDetailClass.paymentStatus
                val db = FirebaseFirestore.getInstance()
                val trips = hashMapOf("paymentStatus" to "Paid",
                    "status" to "Schedule",
                    "paymentId" to paymentID,
                    "paymentMethod" to "PaidOnline",
                    "OTP" to newOTP)
                val query = db.collection("tripDetails")
                    .whereEqualTo("userId", userIdForPayment)
                    .whereEqualTo("tripId", tripIdForPayment)
                    //.whereEqualTo("status", "Completed")
                    .get()
                query.addOnSuccessListener { result ->
                    for (document in result) {
                        if(paymentID.isBlank() || paymentID.isEmpty()){
                        }
                        else if(paymentID.isNotEmpty() || paymentID.isNotBlank()) {
                            db.collection("tripDetails").document(document.id)
                                .set(trips, SetOptions.merge())
                            val intent = Intent(this, MapActivity::class.java)
                            intent.putExtra("paymentStatus", "Paid")
                            intent.putExtra("paymentMethod","PaidOnline")
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
            else if(rideStatusForPayment == "Completed"){

                val paymentStatusForRide = tripDetailClass.paymentStatus
                val db = FirebaseFirestore.getInstance()
                val trips = hashMapOf("paymentStatus" to "Paid",
                    "paymentId" to paymentID,
                    "paymentMethod" to "PaidOnline")
                val query = db.collection("tripDetails")
                    .whereEqualTo("userId", userIdForPayment)
                    .whereEqualTo("tripId", tripIdForPayment)
                    .whereEqualTo("status", "Completed")
                    .get()
                query.addOnSuccessListener { result ->
                    for (document in result) {
                        db.collection("tripDetails").document(document.id).set(trips, SetOptions.merge())
                        val intent = Intent(this,PaymentSuccessfullActivityMain::class.java)
                        intent.putExtra("userId",userIdForPayment)
                        intent.putExtra("tripId",tripIdForPayment)
                        intent.putExtra("paymentStatus","Paid")
                        intent.putExtra("RideTime",rideTimeForCompletedRide)
                        intent.putExtra("RideDistance",rideDistanceForCompletedRide)
                        intent.putExtra("RidePrice",ridePriceForCompletedRide)
                        intent.putExtra("startLatitude",startLocationLatOfCompletedRide)
                        intent.putExtra("startLongitude",startLocationLngOfCompletedRide)
                        intent.putExtra("endLatitude",endLocationLatOfCompletedRide)
                        intent.putExtra("endLongitude",endLocationLngOfCompletedRide)
                        intent.putExtra("driverContact",driverContact)
                        intent.putExtra("driverName",driverName)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            else{

            }

        } catch (e: Exception) {
            //TODO: Handle exception
            println("Exception when payment was successful: $e")
        }
    }

    override fun onPaymentError(errorCode: Int, response: String) {
        try {
            tripDetailClass.paymentStatus = "notPaid"
            //updatePaymentStatusRecordonSchedule()
            //updatePaymentStatusRecordonComplete()
//            val intent = Intent(this,MapActivity::class.java)
//            intent.putExtra("paymentStatus","notPaid")
//            startActivity(intent)
//            finish()
        } catch (e: Exception) {
            //TODO: Handle exception
            println("Exception when payment was a failure: $e")
        }
    }

    private fun getRazorPayKeyId(): String {
        //TODO: retrieve key from a secure database
        return "rzp_live_N9ivBljdAkBS7K"
    }
}
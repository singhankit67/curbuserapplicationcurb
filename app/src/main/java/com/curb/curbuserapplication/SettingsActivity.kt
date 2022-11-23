package com.curb.curbuserapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import com.curb.curbuserapplication.databinding.ActivitySettingsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.math.RoundingMode
import java.text.DecimalFormat


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    var userData = UserClass()
    var userId = ""
    var totalrides = 0
    val db = FirebaseFirestore.getInstance()
    val calculationUtlisClass = CalculationUtilsClass()
    var totalCo2EmissionSaved = 0.0
    private var tripDetailsList11 = ArrayList<TripDetailsClass>()
    private var tripDetailsListRideHistory = ArrayList<TripDetailsClass>()
    private var upcomingTripListFromMapactivity = ArrayList<TripDetailsClass>()
    private var tripDetailListForUpcoming = ArrayList<TripDetailsClass>()

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  window.statusBarColor = this.resources.getColor(R.color.transparent)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewUtils.enableTransparentStatusBar(window)
        window.statusBarColor = resources.getColor(R.color.layer_color)
        getUserDetails()
        getDataWithCurrentUserIdForSettings()

        binding.rll1.setOnClickListener {
            val intent = Intent(this@SettingsActivity, AccountDetailsActivity::class.java)
            intent.putExtra("userID", userId)
            intent.putExtra("userPhoneNumber", binding.phoneNumber.text)
            intent.putExtra("userName", binding.username.text)
            intent.putExtra("userEmail", userData.emailId)
            startActivity(intent)
        }
        val normalReturnValue = intent.getStringExtra("normal back hit")
        getTotalco2savedandridefunction()
        binding.addWorkView.lineDivider!!.visibility = View.GONE
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@SettingsActivity,MapActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
        binding.goBackButtononSettingsScreen.setOnClickListener {
            val intent = Intent(this,MapActivity::class.java)
            startActivity(intent)
            finish() }
        binding.privacyView.nextButton!!.setOnClickListener { goFromSettingsToPrivacySettings() }
        binding.privacyView.setOnClickListener {goFromSettingsToPrivacySettings()}
        binding.signoutView.nextButton!!.setOnClickListener {openSignOutBottomSheet()  }
        binding.signoutView.setOnClickListener {openSignOutBottomSheet()}
        binding.addHomeView.setOnClickListener {goFromSettingsToHome()}
        binding.addHomeView.nextButton!!.setOnClickListener {goFromSettingsToHome()}
        binding.addWorkView.setOnClickListener { goFromSettingsToWork()}
        binding.addWorkView.nextButton!!.setOnClickListener { goFromSettingsToWork() }
        binding.rideHistoryView.setOnClickListener { goFromSettingsToRideHistory() }
        binding.rideHistoryView.nextButton!!.setOnClickListener { goFromSettingsToRideHistory() }
        binding.contactUsView.setOnClickListener { callBackendTeam() }
        binding.contactUsView.nextButton!!.setOnClickListener { callBackendTeam() }
    }

    fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.v("TAG", "Permission is granted")
                true
            } else {
                Log.v("TAG", "Permission is revoked")
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted")
            true
        }
    }
    @SuppressLint("SetTextI18n")
    private fun callBackendTeam() {
        if (isPermissionGranted()) {
            var contactUsDialog: ContactUsDialog = ContactUsDialog(this)
            //   var mainbsbehavior = BottomSheetBehavior.from(ma)
            contactUsDialog.setCancelable(false)
            contactUsDialog.show()
            var cancelButton = contactUsDialog.findViewById<AppCompatButton>(R.id.cancel_button_incontactus_dialog)
            var phnToCallTo = contactUsDialog.findViewById<TextView>(R.id.contact_no_toCall)
            phnToCallTo!!.text = "+91 9820798181"
            phnToCallTo.setOnClickListener {
                val driverNumberToCallTo = "tel:" + "9820798181"
                var intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse(driverNumberToCallTo)
                startActivity(intent) }
            cancelButton.setOnClickListener {
                contactUsDialog.dismiss()
            }
        }
        else if(!isPermissionGranted()){

            val snackBar = Snackbar.make(binding.settingsLayout, "Curb needs permission to access your phone To make calls ", Snackbar.LENGTH_INDEFINITE)
            snackBar.setActionTextColor(Color.WHITE)
            val snackbarView: View = snackBar.view
            val sbView: View = snackbarView
            snackBar.setTextColor(Color.WHITE)
            sbView.setBackgroundColor(Color.DKGRAY);
            snackBar.setAction("OK") { // Call your action method here
                snackBar.dismiss()
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), 1)
            }
            snackBar.show()
        }
    }
    private fun goFromSettingsToPrivacySettings(){
        val intent = Intent(this@SettingsActivity, PrivacySettings::class.java)
        startActivity(intent)
    }
    private fun openSignOutBottomSheet(){
        SignoutBottomsheet().apply {
            show(supportFragmentManager, SignoutBottomsheet.TAG)
        }
    }
    private fun goFromSettingsToHome(){
        val intent = Intent(this,PlacePickerUsingMapActivity::class.java)
        intent.putExtra("userId",userId)
        startActivity(intent)
    }
    private fun goFromSettingsToWork(){
        val intent = Intent(this,PlacePickerUsingMapWorkAddress::class.java)
        intent.putExtra("userId",userId)
        startActivity(intent)
    }
    private fun goFromSettingsToRideHistory(){
        val intent = Intent(this,RideHistoryActivity::class.java)
        intent.putExtra("userId",userId)
        startActivity(intent)
        //finish()
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    @SuppressLint("SetTextI18n")
    private fun getUserDetails() {
        val db = FirebaseFirestore.getInstance()
        val intent = intent
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        val docRef = db.collection("users").document(userId!!)
//            .whereEqualTo("userId", userId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    //userData.userId = document.getString("userId").toString()
                    userData.userId = document.getString("userId").toString()
                    userData.displayName = document.getString("displayName").toString()
                    userData.phoneNumber = document.getString("phoneNumber").toString()
                    userData.emailId = document.getString("email").toString()
                    binding.username.text = document.getString("displayName").toString()
                    binding.phoneNumber.text = document.getString("phoneNumber").toString()
                    userData.homeAddress = document.getString("homeLocation").toString()
                    userData.workAddress = document.getString("workLocation").toString()

                    if (userData.homeAddress == "null" || userData.homeAddress.isBlank() || userData.homeAddress.isEmpty()) {
                        binding.addHomeView.locationVerifier!!.visibility = View.GONE
                    } else if (userData.homeAddress != "null" || userData.homeAddress.isNotBlank() || userData.homeAddress.isNotEmpty()) {
                        binding.addHomeView.locationVerifier!!.visibility = View.VISIBLE
                    }
                    if (userData.workAddress == "null" || userData.workAddress.isBlank() || userData.workAddress.isEmpty()) {
                        binding.addWorkView.locationVerifier!!.visibility = View.GONE
                    } else if (userData.workAddress != "null" || userData.workAddress.isNotBlank() || userData.workAddress.isNotEmpty()) {
                        binding.addWorkView.locationVerifier!!.visibility = View.VISIBLE
                    }
                }


            }
    }
    private fun getTotalco2savedandridefunction(){
        val intent = intent
        var userIdForTotalFun = intent.getStringExtra("userId").toString()
        db.collection("tripDetails")
            .whereEqualTo("userId", userIdForTotalFun)
            .get()
            .addOnSuccessListener{ documents ->
                for(document in documents) {
                    // Log.i(TAG, "${document.id} => ${document.data}")
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
                    var driverContact = document.getString("driverContact")
                    var driverName = document.getString("driverName")
                    var carbonEmissionValue = document.getString("carbonEmissionValue")
                    var carName = document.getString("carName")
                    var orderId = document.getString("orderId")
                    var paymentMethod = document.getString("paymentMethod")
                    //Log.d("TAG", document.getId() + " => " + document.getData());
                    var tripDetail1 = TripDetailsClass(
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
                        driverContact.toString(),
                        driverName.toString(),
                        carbonEmissionValue.toString(),
                        carName.toString(),
                        orderId.toString(),
                        paymentMethod.toString()
                    )
                    tripDetailsList11.add(tripDetail1)
                    //textView_result2!!.text = "${tripDetailsList.size}"
                    if(tripDetail1.statuso == "Completed"){
                        var ongoingTripValue = TripDetailsClass(
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
                            driverContact.toString(),
                            driverName.toString(),
                            carbonEmissionValue.toString(),
                            carName.toString(),
                            orderId.toString(),
                            paymentMethod.toString()
                        )
                        tripDetailsListRideHistory.add(ongoingTripValue)
                        totalCo2EmissionSaved += ongoingTripValue.carbonEmissionValue.toDouble()
                    }
                }
                // textView_result2!!.text = tripDetailsList[0].toString()
                var df = DecimalFormat("0.00")
                df.setRoundingMode(RoundingMode.UP)
                var totalco2EmissionSavedString = df.format(totalCo2EmissionSaved).toString()
                binding.totalco2Text.text = totalco2EmissionSavedString.toString()
                binding.totalRidesText.text = tripDetailsListRideHistory.size.toString()
            }
            .addOnFailureListener { exception ->

            }
    }

    @SuppressLint("LongLogTag", "SetTextI18n")
    private fun getDataWithCurrentUserIdForSettings() {
        db.collection("tripDetails")
            .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.i(SignoutBottomsheet.TAG, "${document.id} => ${document.data}")
                    val OTP = document.getString("OTP")
                    val PaymentId = document.getString("PaymentId")
                    val carId = document.getString("carId")
                    val distance = document.getString("distance")
                    val driverId = document.getString("driverId")
                    val endTime = document.getString("endTime")
                    val price = document.getString("price")
                    val ridersRating = document.getString("ridersRating")
                    val startTime = document.getString("startTime")
                    val startLocationAddress = document.getString("startLocationAddress")
                    val endLocationAddress = document.getString("endLocationAddress")
                    val statuso = document.getString("status")
                    val tripId = document.getString("tripId")
                    val tripType = document.getString("tripType")
                    val userIdd = document.getString("userId")
                    val startLocationLat = document.getString("startLocationLat")
                    val startLocationLng = document.getString("startLocationLng")
                    val endLocationLat = document.getString("endLocationLat")
                    val endLocationLng = document.getString("endLocationLng")
                    val paymentStatus = document.getString("paymentStatus")
                    val timeTakenForRide = document.getString("timeTakenForRide")
                    val driverContact = document.getString("driverContact")
                    val driverName = document.getString("driverName")
                    val carbonEmissionValue = document.getString("carbonEmissionValue")
                    val carName = document.getString("carName")
                    val orderId = document.getString("orderId")
                    val paymentMethod = document.getString("paymentMethod")
                    val tripDetail = TripDetailsClass(
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
                        driverContact.toString(),
                        driverName.toString(),
                        carbonEmissionValue.toString(),
                        carName.toString(),
                        orderId.toString(),
                        paymentMethod.toString()
                    )

                    tripDetailListForUpcoming.add(tripDetail)

                    val currentTineInMillis = calculationUtlisClass.currentDateToEpochCoverter()
                    if (tripDetail.startTime != "null") {
                        if (tripDetail.startTime != "") {
                            if (tripDetail.startTime.toLong() > currentTineInMillis!! && tripDetail.statuso == "Schedule" || tripDetail.startTime.toLong() > currentTineInMillis && tripDetail.statuso == "Assign" || tripDetail.startTime.toLong() > currentTineInMillis && tripDetail.statuso == "Arrived") {
                                // var startDateInProperFormat  = getDateFromEpoch(startTime.toString())
                                val upcomingTripDetail = TripDetailsClass(
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
                                    carbonEmissionValue.toString(),
                                    carName.toString(),
                                    orderId.toString(),
                                    paymentMethod.toString()

                                )
                                upcomingTripListFromMapactivity.add(upcomingTripDetail)
                            }
                        }
                    }
                }
                if(upcomingTripListFromMapactivity.size == 0){
                    binding.rideHistoryView.upcomingRideLocater!!.visibility = View.GONE

                }
                else if(upcomingTripListFromMapactivity.size > 0){
                    binding.rideHistoryView.upcomingRideLocater!!.visibility = View.VISIBLE

                }

            }
            .addOnFailureListener { exception ->

            }
    }


}
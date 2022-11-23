package com.curb.curbuserapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.w3c.dom.Text


class PaymentSuccessfullActivityMain : AppCompatActivity(){
    var ratingBar : RatingBar? = null
    var apiKey :String? = null
    var rideFare:String? = null
    var rideDistance:String? = null
    var rideTime:String? = null
    var mapActivity = MapActivity()
    var didYouEnjoyUrRide : TextView? =  null
    var ridePriceTextInLayout : TextView? = null
    var rideTimeTextInLayout : TextView? = null
    var rideDistanceInLayout : TextView? = null
    var userId:String? = null
    var rideStartTime:String? = null
    var tripId:String? = null
    var goToHomeButton : TextView?= null
    var userNamePlaceone : TextView? = null
    var userNamePlaceTwo : TextView? = null
    var lostItemText : TextView? = null
    var driverMisconductText: TextView? = null
    var driverNameString:String? = null
    var driverNameText:TextView? = null
    var goBackButton:ImageButton? = null
    var thankyouText : TextView? = null
    var driverContactText : String? = null
    var paymentSuccessfullMain : ConstraintLayout? = null
    var counter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val w: Window = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setContentView(R.layout.activity_payment_successfull_main)
        rideDistanceInLayout = findViewById(R.id.distance_text)
        userNamePlaceone = findViewById(R.id.userName1Text)
        userNamePlaceTwo = findViewById(R.id.userName_text2)
        driverMisconductText = findViewById(R.id.driver_misconduct_text)
        lostItemText = findViewById(R.id.lost_items_text)
        goToHomeButton = findViewById(R.id.go_to_home_button)
        ridePriceTextInLayout = findViewById(R.id.payment_text)
        rideTimeTextInLayout = findViewById(R.id.time_text)
        thankyouText = findViewById(R.id.thankyou_text)
        driverNameText = findViewById(R.id.driverNameHeading)
        ratingBar = findViewById(R.id.rating_bar)
        paymentSuccessfullMain = findViewById(R.id.payment_successfull_mIN)
        goBackButton = findViewById(R.id.go_back_button_onPayment_successfull)
        didYouEnjoyUrRide = findViewById(R.id.did_you_enjoy_ride_text)
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        apiKey = value.toString()
        val intent = intent
        rideDistance = intent.getStringExtra("RideDistance")
        rideFare = intent.getStringExtra("RidePrice")
        getUserDetails()
        rideTime = intent.getStringExtra("RideTime")  + "mins"
        userId = intent.getStringExtra("userId")
        driverNameString = intent.getStringExtra("driverName")
        rideStartTime = intent.getStringExtra("RideStartTime")
        driverContactText = intent.getStringExtra("driverContact")
        tripId = intent.getStringExtra("tripId")
        rideTimeTextInLayout!!.text = rideTime.toString()
        rideDistanceInLayout!!.text = rideDistance.toString()
        ridePriceTextInLayout!!.text = rideFare.toString()
        //driverNameText!!.text = driverContactText.toString()
        if(TextUtils.isDigitsOnly(driverContactText)){
            driverNameText!!.text = driverNameString.toString()

        }else{
            driverNameText!!.text = driverContactText.toString()
        }
        driverMisconductText!!.setOnClickListener { callBackendTeam() }
        lostItemText!!.setOnClickListener { callBackendTeam() }
        goToHomeButton!!.setOnClickListener { val intent = Intent(this,MapActivity::class.java)
            intent.putExtra("comingBackFromRideCompletedLayout","comingBackFromRideCompletedLayout")
            startActivity(intent)
            finish()  }

        ratingBar!!.rating = 0f
        ratingBar!!.stepSize = 1f
        ratingBar!!.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            //didYouEnjoyUrRide!!.visibility = View.GONE
            mapActivity.ongoingTrip.ridersRating = rating.toString()
            //thankyouText!!.visibility = View.VISIBLE
            //ratingBar.visibility = View.GONE
            updateRecord()
            //ratingBar!!.isEnabled = false
            ratingBar!!.isClickable = false
            ratingBar!!.setIsIndicator(true)
            ratingBar!!.isFocusable = false

        }
        goBackButton!!.setOnClickListener {
//            val intent = Intent(this,MapActivity::class.java)
//            intent.putExtra("comingBackFromRideCompletedLayout","comingBackFromRideCompletedLayout")
//            startActivity(intent)
            finish() }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@PaymentSuccessfullActivityMain,MapActivity::class.java)
                intent.putExtra("comingBackFromRideCompletedLayout","comingBackFromRideCompletedLayout")
                startActivity(intent)
                finish()
            }
        }
        this.onBackPressedDispatcher.addCallback(this,callback)
    }
    private fun updateRecord() {
        val ridersRating = mapActivity.ongoingTrip.ridersRating

        val db = FirebaseFirestore.getInstance()
        // val userId = "tPOBQPp3yuTDWZL5K13NiQcUNkc2"
        //  val tripId = db.collection("tripDetails").document().id
        val trips = hashMapOf(
            "ridersRating" to ridersRating,

            )
        val query = db.collection("tripDetails")
            .whereEqualTo("userId", userId)
            .whereEqualTo("tripId", tripId.toString())
            .get()
        query.addOnSuccessListener { result ->
            for (document in result) {
                //  val tripId = db.collection("tripDetails").document().id

                db.collection("tripDetails").document(document.id).set(trips, SetOptions.merge())

            }
        }
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
                    userNamePlaceone!!.text = document.getString("displayName").toString() + ","
                    userNamePlaceTwo!!.text = document.getString("displayName").toString()


                }


            }
    }
    fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
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
            cancelButton.setOnClickListener { contactUsDialog.dismiss() }
        }
        else if(!isPermissionGranted()){

            val snackBar = Snackbar.make(paymentSuccessfullMain!!, "Curb needs permission to access your phone To make calls ", Snackbar.LENGTH_INDEFINITE)
            snackBar.setActionTextColor(Color.WHITE)
            val snackbarView: View = snackBar.view
            val sbView: View = snackbarView
            snackBar.setTextColor(Color.WHITE)
            sbView.setBackgroundColor(Color.DKGRAY);
            snackBar.setAction("OK") { // Call your action method here
                snackBar.dismiss()
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
            snackBar.show()
        }
    }
}
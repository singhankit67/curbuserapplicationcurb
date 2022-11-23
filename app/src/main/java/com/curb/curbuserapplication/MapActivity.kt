package com.curb.curbuserapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.curb.curbuserapplication.SignoutBottomsheet.Companion.TAG
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MapActivity : OnMapReadyCallback, GeoTask.Geo, AppCompatActivity() {
    lateinit var calendar: Calendar
    var changeMonth: Calendar? = null
    var arrayList = ArrayList<Date>()
    private var settingsActivityOpener: ImageButton? = null
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]
    private var selectedDay: Int = currentDay
    private var selectedMonth: Int = currentMonth
    private var selectedYear: Int = currentYear
    private var calendarRecyclerView: RecyclerView? = null
    private var pickupLatLng: LatLng? = null
    val handler = Handler()
    private var pickupLatLngForMarker: LatLng? = null
    private var dropLatLng: LatLng? = null
//    private var textView_result1: TextView? = null
//    private var textView_result2: TextView? = null
    var apiKey: String? = null
    var ongoingTrip = TripDetailsClass()
    private var distValue: Double = 0.0
    private var onresumeCounter = 0
    private var rideClickedOnUpcomingView = 0
    private var arrayListPositionClicked = 0
    private var arrayListPositionClickedd = 0
    private lateinit var tripDetailsClass: TripDetailsClass
    private var startTimeForDirectionUrl: String? = null
    private lateinit var functions: FirebaseFunctions
    private lateinit var mMap: GoogleMap
    var pickupTimeTextt: TextView? = null
    var pickupTimeTextView: TextView? = null
    var dropDownTimeTextView: TextView? = null
    var idCatcher = 0
    var count = 0
    var greyPolyLine: Polyline? = null
    var blackPolyline: Polyline? = null
    var destinationMarker: Marker? = null
    var originMarker: Marker? = null
    private var pickupLocalityValue: String? = null
    private var dropLocalityValue: String? = null
    var pickupRequestCode = 1
    var dropOfRquestCode = 2
    var pickupValue1: String? = null
    var pickupValue: String? = null
    var dropOffValue1: String? = null
    var dropofvalue: String? = null
    private var airportTerminal2Text = "Airport T2"
    private var timePicker: TimePicker? = null
    private var hour = 0
    val polylineAnimator = AnimationUtils.polyLineAnimator()
    private var minute = 0
    private var durationInTraffic: String? = null
    private var distanceFromSourceToDestination: String? = null
    private var mapFragment: SupportMapFragment? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var ongoingTripIfUserDestroysTheAppList = ArrayList<TripDetailsClass>()
    private var tripDetailsList = ArrayList<TripDetailsClass>()
    private var tripDetailsList1 = ArrayList<TripDetailsClass>()
    private var upcomingTripList = ArrayList<TripDetailsClass>()
    private var fareFinalAns: String? = null
    var rideEndTimeInDateFormat: Date? = null
    private var selectedDayForRide: String? = null
    private var selectedMonthForRide: String? = null
    private var selectedYearForRide: String? = null
    private var selectedHourForRide: String? = null
    private var selectedAmOrPmForRide: String? = null
    private var selectedMinuteForRide: String? = null
    private var rideDetailsBottomSheet: View? = null
    private var mainActBottomSheet: View? = null
    private var weDoNotProvideServiceHereBs: View? = null
    private var rideInProgressBottomSheet: View? = null
    private var rideInProgressBottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var weDoNotProvideServiceHereBsBehavior: BottomSheetBehavior<View>? = null
    private var rideScheduledSuccessfullyBottomSheet: View? = null
    var currentTripId = "lkjhgf"
    var calculationUtilClass = CalculationUtilsClass()
    private var rideScheduledSuccessfullyBottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var mainActBottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var rideDetailsBottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var durationInSeconds: String? = null
    var finalFareOfRide: String? = null
    var db = FirebaseFirestore.getInstance()
    var userId = FirebaseAuth.getInstance().currentUser!!.uid
    var currentUserphoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber
    var currentUserName : String? = null
    var currentUserEmailId :String? = null
    private var internetnotEnabledUi: View? = null
    private var networkConnection: IsInternetAvailable? = null
    private var fromToCardView: View? = null
    private var BottomSheetForTimeAndDatePicker: View? = null
    private var TimeAndDatePickerBottomSheetBehavior: BottomSheetBehavior<View>? = null
    var endDestinationArrivalTime: TextView? = null
    var coordinatorLayout: CoordinatorLayout? = null
    var permissionUtlis = PermissionUtils
    var thirdCardView: MaterialCardView ? = null
    var shineEffectForMBS : ImageView ?= null
    var yourDriverHasArrivedText : TextView? = null
    var upcomingRideText : TextView? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        ViewUtils.enableTransparentStatusBar(window)

        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        apiKey = value.toString()
        // userId = FirebaseAuth.getInstance().currentUser?.uid
        tripDetailsClass = TripDetailsClass()
        //  db = FirebaseFirestore.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        networkConnection = IsInternetAvailable(applicationContext)
        // Map Fragment
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)
        coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinator)
//        textView_result1 = findViewById(R.id.textView_result1)
//        textView_result2 = findViewById(R.id.textView_result2)
        settingsActivityOpener = coordinatorLayout!!.findViewById(R.id.open_settings_menu_button)
        fromToCardView =
            coordinatorLayout!!.findViewById<ConstraintLayout>(R.id.pickup_drop_layout_seperate)
        internetnotEnabledUi =
            coordinatorLayout!!.findViewById<ConstraintLayout>(R.id.enable_internet_service_ui)
        //tripDetailsList.clear()
        setBlankData()
        // getDataWithCurrentUserId()
        getDataForOngoingTrip()
        settingsActivityOpener!!.setOnClickListener {
            val intent = Intent(this@MapActivity, SettingsActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
            finish()
        }
        internetnotEnabledUi!!.visibility = View.GONE
        networkConnection!!.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (isConnected) {
                internetnotEnabledUi!!.visibility = View.GONE
            } else {
                internetnotEnabledUi!!.visibility = View.VISIBLE
                internetnotEnabledUi!!.setOnTouchListener { _, event -> true }
            }
        })
        mainActBottomSheet = coordinatorLayout!!.findViewById<ConstraintLayout>(R.id.activity_main_bottomsheet)
        rideScheduledSuccessfullyBottomSheet = coordinatorLayout!!.findViewById<LinearLayout>(R.id.ride_scheduled_bottomsheet1)
        weDoNotProvideServiceHereBs = coordinatorLayout!!.findViewById<ConstraintLayout>(R.id.we_dont_provide_service_here_layout)
        weDoNotProvideServiceHereBsBehavior = BottomSheetBehavior.from(weDoNotProvideServiceHereBs!!)
        rideInProgressBottomSheet = coordinatorLayout!!.findViewById<ConstraintLayout>(R.id.ride_in_progress_bottom_sheet)
        rideInProgressBottomSheetBehavior = BottomSheetBehavior.from(rideInProgressBottomSheet!!)
        rideInProgressBottomSheetBehavior!!.isHideable = true
        rideInProgressBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        weDoNotProvideServiceHereBsBehavior!!.isHideable = true
        weDoNotProvideServiceHereBsBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        rideScheduledSuccessfullyBottomSheetBehavior = BottomSheetBehavior.from(rideScheduledSuccessfullyBottomSheet!!)
        mainActBottomSheetBehavior = BottomSheetBehavior.from(mainActBottomSheet!!)
        rideDetailsBottomSheet = coordinatorLayout!!.findViewById<ConstraintLayout>(R.id.ride_details_bottomsheet)
        rideDetailsBottomSheetBehavior = BottomSheetBehavior.from(rideDetailsBottomSheet!!)
        BottomSheetForTimeAndDatePicker = coordinatorLayout!!.findViewById<LinearLayout>(R.id.activity_bottomsheet)
        thirdCardView = mainActBottomSheet!!.findViewById(R.id.thirdCardView)
        shineEffectForMBS = mainActBottomSheet!!.findViewById(R.id.shine_effect_in_main_bottomsheet)
        yourDriverHasArrivedText = mainActBottomSheet!!.findViewById(R.id.driver_has_arrived_text)
        upcomingRideText = mainActBottomSheet!!.findViewById(R.id.upcoming_ride_text)
        TimeAndDatePickerBottomSheetBehavior = BottomSheetBehavior.from(BottomSheetForTimeAndDatePicker!!)
        tripDetailsClass.paymentStatus = "notPaid"
        init_payment_successful()
        init_book_ride_details()
        init_main_act_bottomsheet()
        endDestinationArrivalTime = fromToCardView!!.findViewById<TextView>(R.id.drop_time)
        pickupTimeTextt = fromToCardView!!.findViewById(R.id.pickup_time)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            init_from_to_airport_values_layout()
        }
        init_persistent_bottomsheet()
    }

    @SuppressLint("SetTextI18n")
    private fun getDataForOngoingTrip() {
        db.collection("tripDetails")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Log.i(TAG, "${document.id} => ${document.data}")
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
                    val tripDetail1 = TripDetailsClass(
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
                    tripDetailsList1.add(tripDetail1)
                    //textView_result2!!.text = "${tripDetailsList.size}"
                    if (tripDetail1.statuso == "Ongoing") {
                        val ongoingTripValue = TripDetailsClass(
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
                        ongoingTripIfUserDestroysTheAppList.add(ongoingTripValue)
                    }
                }
//                textView_result1!!.text =
//                    ongoingTripIfUserDestroysTheAppList.size.toString() + "value"
                // textView_result2!!.text = tripDetailsList[0].toString()
            }
            .addOnFailureListener {

            }
    }

//    private fun updateDeleteDataRecordInDb() {
//        val userId = userId
//        val db = FirebaseFirestore.getInstance()
//        val tripIdOfTheFieldToBeDeleted = upcomingTripList[rideClickedOnUpcomingView].tripId
//        val tripss = hashMapOf("status" to "Cancelled")
//        val collection = db.collection("tripDetails")
//            .whereEqualTo("userId", userId)
//            .whereEqualTo("tripId", tripIdOfTheFieldToBeDeleted)
//            // .whereEqualTo("status","Schedule")
//            .get()
//        //.document(tripIdOfTheFieldToBeDeleted)
//        //.delete()
//        collection.addOnSuccessListener { result ->
//            for (document in result) {
//                //  val tripId = db.collection("tripDetails").document().id
//                db.collection("tripDetails").document(document.id).set(tripss, SetOptions.merge())
//            }
//        }
//
//
//    }

    //Add BLANK DATA Trip
    private fun setBlankData() {
        // val userId = FirebaseAuth.getInstance().currentUser?.uid
        // val userId = "tPOBQPp3yuTDWZL5K13NiQcUNkc2"
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("tripDetails")
        val tripId = db.collection("tripDetails").document().id
        // currentTripId = tripId
        val tripDetails =
            hashMapOf(
                "OTP" to "",
                "tripId" to tripId,
                "userId" to userId,
                "status" to "Initiated",
                "tripType" to "",
                "distance" to "",
                "endTime" to "",
                "startTime" to "",
                "startLocationAddress" to "",
                "endLocationAddress" to "",
                "price" to "",
                "driverId" to "",
                "carId" to "",
                "paymentId" to "",
                "ridersRating" to "",
                "startLocationLat" to "",
                "startLocationLng" to "",
                "endLocationLat" to "",
                "endLocationLng" to "",
                "paymentStatus" to "",
                "paymentMethod" to "",
                "timeTakenForRide" to "",
                "driverContact" to "",
                "driverName" to "",
                "carbonEmissionValue" to "",
                "carName" to "",
                "orderId" to ""
            )

        db.collection("tripDetails")
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "Initiated")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.isEmpty) {
                        collection.document(tripId).set(tripDetails, SetOptions.merge())
                    } else {

                    }
                }
            }
    }

    private fun updateRecord() {
        val rideStatus = tripDetailsClass.statuso.trim()
        val tripType = tripDetailsClass.tripType.trim()
        val distance = tripDetailsClass.distance.trim()
        val startTime = tripDetailsClass.startTime.trim()
        val endTime = tripDetailsClass.endTime.trim()
        val startLocationAddress = tripDetailsClass.startLocationAddress.trim()
        val endLocationAddress = tripDetailsClass.endLocationAddress.trim()
        val startLocationLat = tripDetailsClass.startLocationLat.trim()
        val startLocationLng = tripDetailsClass.startLocationLng.trim()
        val endLocationLng = tripDetailsClass.endLocationLng.trim()
        val endLocationLat = tripDetailsClass.endLocationLat.trim()
        val price = tripDetailsClass.price.trim()
        val driverId = ""
        val carId = ""
        val paymentId = ""
        //val orderId = ""
        val ridersRating = ""
        val otp = tripDetailsClass.OTP.trim()
        val paymentStatus = tripDetailsClass.paymentStatus.trim()
        val timeTakenForRide = tripDetailsClass.timeTakenForRide.trim()
        val carbonSaved = tripDetailsClass.carbonEmissionValue
        val paymentMethod = tripDetailsClass.paymentMethod
        // val carName = tripDetailsClass.carName.toString().trim()

        val db = FirebaseFirestore.getInstance()
        // val userId = "tPOBQPp3yuTDWZL5K13NiQcUNkc2"
        //  val tripId = db.collection("tripDetails").document().id
        val trips = hashMapOf(
            "OTP" to otp,
            "status" to rideStatus,
            "tripType" to tripType,
            "distance" to distance,
            "endTime" to endTime,
            "startTime" to startTime,
            "startLocationAddress" to startLocationAddress,
            "endLocationAddress" to endLocationAddress,
            "startLocationLat" to startLocationLat,
            "startLocationLng" to startLocationLng,
            "endLocationLat" to endLocationLat,
            "endLocationLng" to endLocationLng,
            "price" to price,
            "driverId" to driverId,
            "carId" to carId,
            "paymentId" to paymentId,
            "ridersRating" to ridersRating,
            "paymentStatus" to paymentStatus,
            "timeTakenForRide" to timeTakenForRide,
            "carbonEmissionValue" to carbonSaved,
            "paymentMethod" to paymentMethod
            //"orderId" to orderId
            // "carName" to carName

        )
        val query = db.collection("tripDetails")
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "Initiated")
            .get()
        query.addOnSuccessListener { result ->
            for (document in result) {
                //  val tripId = db.collection("tripDetails").document().id
                db.collection("tripDetails").document(document.id).set(trips, SetOptions.merge())
            }
        }
    }

    @SuppressLint("LongLogTag", "SetTextI18n")
    private fun getDataWithCurrentUserId() {
        db.collection("tripDetails")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.i(TAG, "${document.id} => ${document.data}")
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

                    tripDetailsList.add(tripDetail)

                    val currentTineInMillis = calculationUtilClass.currentDateToEpochCoverter()
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
                                upcomingTripList.add(upcomingTripDetail)
                            }
                        }
                    }

                    //textView_result2!!.text = "${tripDetailsList.size}"
                }

//                textView_result2!!.text = upcomingTripList.size.toString()
                if(upcomingTripList.isNotEmpty() || upcomingTripList.size> 0){
                    thirdCardView!!.visibility = View.VISIBLE
                    var value = false
                    for (i in 0..upcomingTripList.lastIndex){
                        if(upcomingTripList[i].statuso == "Arrived"){
                            value = true
                        }
                    }
                    if (value){
                        upcomingRideText!!.visibility = View.GONE
                        yourDriverHasArrivedText!!.visibility = View.VISIBLE
                        var scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
                        for(i in 1..8) {
                            scheduledExecutorService.scheduleAtFixedRate(object : Runnable {
                                override fun run() { runOnUiThread(object : Runnable { override fun run() { shineStart() } }) } }, 3, 3, TimeUnit.SECONDS) }

                    }
                    else if(!value){
                        upcomingRideText!!.visibility = View.VISIBLE
                        yourDriverHasArrivedText!!.visibility = View.GONE
                        var scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
                        for(i in 1..8) {
                            scheduledExecutorService.scheduleAtFixedRate(object : Runnable {
                                override fun run() { runOnUiThread(object : Runnable { override fun run() { shineStart() } }) } }, 5, 5, TimeUnit.SECONDS) }
                    }

                }
                else if(upcomingTripList.isEmpty() || upcomingTripList.size == 0){
                    if(thirdCardView!!.visibility == View.VISIBLE)
                        thirdCardView!!.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->

            }
    }

    private fun addNumbers(distance: Double) {
        // Create the arguments to the callable function, which are two integers
        val data = JSONObject()
        data.put("distance", distance.toString())
        // Call the function and extract the operation from the result

        FirebaseFunctions.getInstance("asia-south1")
            .getHttpsCallable("function-fare-estimate-java")
            .call(data)
            .continueWith {
                if (it.isSuccessful) {

                    val result = it.result?.data.toString()
                    val valueinJson = JSONObject(result)
                    val finalPriceOfRide = valueinJson.get("fare")
                    fareFinalAns = finalPriceOfRide.toString()
                    val fareFianlAnsInDouble = fareFinalAns!!.toDouble()
                    val df = DecimalFormat("0.00")
                    df.roundingMode = RoundingMode.UP
                    val finalFareOfRide1 = df.format(fareFianlAnsInDouble)
                    val finalPrice = findViewById<TextView>(R.id.total_price_of_the_ride_cost)
                    finalPrice.text = finalFareOfRide1.toString()
                    finalFareOfRide = finalFareOfRide1.toString()
                    Log.e("SWAG", finalFareOfRide1.toString())
                } else {
                    Log.e("Test", it.exception.toString())
                }
            }
    }
    @SuppressLint("MissingPermission")
    private fun launchLocationAutoCompleteActivity(requestCode: Int) {
        Places.initialize(applicationContext, getString(R.string.api_key))
        var intent: Intent? = null
        val fields: List<Place.Field> =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        if (requestCode == pickupRequestCode || requestCode == dropOfRquestCode) {
            when (requestCode) {
                pickupRequestCode -> {
                    intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setCountry("IND")//setLocationBias(RectangularBounds.newInstance(LatLng(19.076090, 72.877426), LatLng(19.076090, 72.877426))) //INDIA
                        .setHint("Search Pickup location")
                        .build(this@MapActivity)
                }
                dropOfRquestCode -> {
                    intent = Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields
                    )
                        .setCountry("IND")//setLocationBias(RectangularBounds.newInstance(LatLng(19.076090, 72.877426), LatLng(19.076090, 72.877426))) //INDIA
                        .setHint("Search Drop Location")
                        .build(this@MapActivity)

                }
            }
        }
        startActivityForResult(intent, requestCode)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun init_from_to_airport_values_layout() {
        pickupTimeTextView = findViewById(R.id.pickup_time)
        dropDownTimeTextView = findViewById(R.id.drop_time)
        val PickupFromTextInputLayout: TextInputLayout? = findViewById(R.id.pickUpTextView_main)
        val DropToTextInputLayout: TextInputLayout? = findViewById(R.id.dropTextView_main)
        fromToCardView!!.visibility = View.GONE
        //settingsActivityOpener!!.visibility = View.VISIBLE
        networkConnection!!.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (isConnected) {
                if (count == 0) {
//                    val intentAnswer = intent
//                    val extraVal = intentAnswer.getStringExtra("paymentStatus")
//                    val paymentStatus = intentAnswer.getStringExtra("paymentStatus")
                    if (tripDetailsClass.paymentStatus == "notPaid") {

                        count++
                    } else if (tripDetailsClass.paymentStatus == "Paid") {
//                        if(PickupFromTextInputLayout.editText == null || DropToTextInputLayout.editText == null) {
//                            fromToCardView!!.visibility = View.GONE
//                        }
                        count++
                    }
                } else if (count > 0) {
                    if (mainActBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                        fromToCardView!!.visibility = View.GONE
                        settingsActivityOpener!!.visibility = View.VISIBLE
                    } else if (rideInProgressBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                        fromToCardView!!.visibility = View.GONE
                        settingsActivityOpener!!.visibility = View.GONE
                    } else if (weDoNotProvideServiceHereBsBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                        fromToCardView!!.visibility = View.GONE
                        settingsActivityOpener!!.visibility = View.VISIBLE
                    } else if (mainActBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_HIDDEN) {
                        fromToCardView!!.visibility = View.VISIBLE
                        settingsActivityOpener!!.visibility = View.GONE
                        count++
                    }
                }
            } else {
                fromToCardView!!.visibility = View.GONE
                settingsActivityOpener!!.visibility = View.VISIBLE
            }
        })
        DropToTextInputLayout!!.editText!!.text = null
        PickupFromTextInputLayout!!.editText!!.text = null
        if (pickupValue != null && dropofvalue != null) {
            val typeface = ResourcesCompat.getFont(this, R.font.open_sans)
            DropToTextInputLayout.typeface = typeface
            PickupFromTextInputLayout.typeface = typeface
            DropToTextInputLayout.editText!!.setText(pickupValue)
            DropToTextInputLayout.isEnabled = false
            DropToTextInputLayout.isClickable = false
            PickupFromTextInputLayout.editText!!.setText(dropofvalue)
            PickupFromTextInputLayout.isEnabled = false
            PickupFromTextInputLayout.isClickable = false
        }
        if ((pickupTimeTextView!!.text == "" || pickupTimeTextView!!.text == null) && (dropDownTimeTextView!!.text == "" || dropDownTimeTextView!!.text == null)) {
            pickupTimeTextView!!.visibility = View.INVISIBLE
            dropDownTimeTextView!!.visibility = View.INVISIBLE
        }
        val pickup_editIcon = findViewById<ImageButton>(R.id.pickup_location_edit_icon)
        val drop_editIcon = findViewById<ImageButton>(R.id.drop_to_location_edit_icon)
        //  val persistantBottomSheet1 = coordinatorLayout!!.findViewById<LinearLayout>(R.id.ride_scheduled_bottomsheet1)
        // val behavior1 = BottomSheetBehavior.from(persistantBottomSheet1)
        if (rideScheduledSuccessfullyBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            pickup_editIcon.visibility = View.GONE
            drop_editIcon.visibility = View.GONE
        }
        if (PickupFromTextInputLayout.editText!!.text.toString() != airportTerminal2Text) {
            pickup_editIcon.visibility = View.VISIBLE
            drop_editIcon.visibility = View.GONE
            pickup_editIcon.setOnClickListener {
                fromToCardView!!.visibility = View.GONE
                tripDetailsClass.OTP = ""
                // val persistantBottomSheetRideDetails = coordinatorLayout!!.findViewById<ConstraintLayout>(R.id.ride_details_bottomsheet)
                //val behaviorRideDetailsBS = BottomSheetBehavior.from(persistantBottomSheetRideDetails)

                if (TimeAndDatePickerBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    TimeAndDatePickerBottomSheetBehavior!!.isHideable = true
                    TimeAndDatePickerBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                } else if (rideDetailsBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    rideDetailsBottomSheetBehavior!!.isHideable = true
                    rideDetailsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                }
                launchLocationAutoCompleteActivity(pickupRequestCode)
            }
        } else if (DropToTextInputLayout.editText!!.text.toString() != airportTerminal2Text) {
            pickup_editIcon.visibility = View.GONE
            drop_editIcon.visibility = View.VISIBLE
            drop_editIcon.setOnClickListener {
                fromToCardView!!.visibility = View.GONE
                tripDetailsClass.OTP = ""
                // val persistantBottomSheetRideDetails = coordinatorLayout!!.findViewById<ConstraintLayout>(R.id.ride_details_bottomsheet)
                // val behaviorRideDetailsBS = BottomSheetBehavior.from(persistantBottomSheetRideDetails)
                if (TimeAndDatePickerBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    TimeAndDatePickerBottomSheetBehavior!!.isHideable = true
                    TimeAndDatePickerBottomSheetBehavior!!.state =
                        BottomSheetBehavior.STATE_HIDDEN
                } else if (rideDetailsBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    rideDetailsBottomSheetBehavior!!.isHideable = true
                    rideDetailsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                }
                launchLocationAutoCompleteActivity(dropOfRquestCode)
            }
        }
        //handling the onBack pressed
        if (pickup_editIcon.visibility == View.GONE && drop_editIcon.visibility == View.VISIBLE) {
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    fromToCardView!!.visibility = View.GONE
                    TimeAndDatePickerBottomSheetBehavior!!.isHideable = true
                    TimeAndDatePickerBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                    launchLocationAutoCompleteActivity(dropOfRquestCode)
                }
            }
            this@MapActivity.onBackPressedDispatcher.addCallback(this@MapActivity, callback)
        } else if (pickup_editIcon.visibility == View.VISIBLE && drop_editIcon.visibility == View.GONE) {
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    fromToCardView!!.visibility = View.GONE
                    TimeAndDatePickerBottomSheetBehavior!!.isHideable = true
                    TimeAndDatePickerBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                    launchLocationAutoCompleteActivity(pickupRequestCode)
                }
            }
            this@MapActivity.onBackPressedDispatcher.addCallback(this@MapActivity, callback)
        }
    }

    private fun getDirectionURL(origin: LatLng, dest: LatLng, secret: String): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
//                            "&sensor=false" +
                "&mode=driving" +
                "&traffic_model=best_guess" +
                "&departure_time=${startTimeForDirectionUrl}" +
                "&key=$secret"
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val result = ArrayList<List<LatLng>>()
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val data = response.body!!.string()
                try {
                    val respObj = Gson().fromJson(data, MapData::class.java)
                    val path = ArrayList<LatLng>()
                    for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                        path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                    }
                    result.add(path)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result

        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.geodesic(true)
//                            lineoption.geodesic(true)
                showPath(result[i])
                mMap.projection.visibleRegion.latLngBounds.center

            }
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    private fun getDistance() {
        val url =
            "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + pickupLatLng!!.latitude + "," + pickupLatLng!!.longitude + "&destinations=" + dropLatLng!!.latitude + "," + dropLatLng!!.longitude + "&mode=driving&traffic_model=best_guess&departure_time=1652448181000&key=AIzaSyBKZRgWTxGOZTwzXIN-glCSfeirDUomV5Q"
        GeoTask(this@MapActivity).execute(url)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init_main_act_bottomsheet() {
        //    mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        if (tripDetailsClass.paymentStatus == "notPaid") {
            networkConnection!!.observe(this, androidx.lifecycle.Observer { isConnected ->
                if (isConnected) {
                    if (TimeAndDatePickerBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                        mainActBottomSheetBehavior!!.isHideable = true
                        mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                        if (fromToCardView!!.visibility == View.GONE) {
                            fromToCardView!!.visibility = View.VISIBLE
                            settingsActivityOpener!!.visibility = View.GONE
                        }
                    } else if (rideDetailsBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                        mainActBottomSheetBehavior!!.isHideable = true
                        mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                        if (fromToCardView!!.visibility == View.GONE) {
                            fromToCardView!!.visibility = View.VISIBLE
                            settingsActivityOpener!!.visibility = View.GONE
                        }
                    } else if (rideScheduledSuccessfullyBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                        mainActBottomSheetBehavior!!.isHideable = true
                        mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                        if (fromToCardView!!.visibility == View.GONE) {
                            fromToCardView!!.visibility = View.VISIBLE
                            settingsActivityOpener!!.visibility = View.GONE
                        }
                    } else if (rideInProgressBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                        mainActBottomSheetBehavior!!.isHideable = true
                        mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                    } else if (weDoNotProvideServiceHereBsBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                        mainActBottomSheetBehavior!!.isHideable = true
                        mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                    } else {
                        handler.postDelayed(
                            {
                                mainActBottomSheetBehavior!!.isHideable = false
                                mainActBottomSheetBehavior!!.state =
                                    BottomSheetBehavior.STATE_EXPANDED
                                settingsActivityOpener!!.visibility = View.VISIBLE
                                //   getDataWithCurrentUserId()
                                // upcomingRideLogic()

//                                // getLocationAccess()
                            },
                            40
                        )
                        internetnotEnabledUi!!.visibility = View.GONE
                    }
                } else {
                    mainActBottomSheetBehavior!!.isHideable = true
                    mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                    internetnotEnabledUi!!.visibility = View.VISIBLE
                    internetnotEnabledUi!!.setOnTouchListener { v, event -> true }
                }
            })
        } else {
            mainActBottomSheetBehavior!!.isHideable = true
            mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        }
        val callbackForClosingTheApp = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActBottomSheetBehavior!!.isHideable = true
                mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                finish()
                finishAffinity()
            }
        }
        this@MapActivity.onBackPressedDispatcher.addCallback(
            this@MapActivity,
            callbackForClosingTheApp
        )
        if (mainActBottomSheetBehavior != null) {
            var swipeLogic1: MotionLayout?
            var mainView: MaterialCardView?
            var secondView: MaterialCardView?
            var ongoingRideButton: LinearLayout?
            var pickupCardView: MaterialCardView? = null
            var dropToAirportCard: MaterialCardView? = null
            var dropToAirportCardLayout: LinearLayout? = null
            var pickupFromAirportLayout: LinearLayout? = null
            var dropIconBackground: ImageView? = null
            var pickupIconBackground: ImageView? = null
            var nextRideButton: RelativeLayout?
            var cancelRideButton: RelativeLayout?
            var goBackButton: RelativeLayout?
            mainActBottomSheetBehavior!!.setBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                @SuppressLint("RestrictedApi")
                override fun onStateChanged(bottomSheet: View, newState: Int) {

                    mainView = bottomSheet.findViewById(R.id.primaryCardView)
                    swipeLogic1 = bottomSheet.findViewById(R.id.lay_lock_main)
                    secondView = bottomSheet.findViewById(R.id.secondaryCardView)
                    ongoingRideButton = bottomSheet.findViewById(R.id.activeOngoingRide)
                    //callDriverButton = bottomSheet.findViewById(R.id.call_button_on_main_bottomsheet)
                    cancelRideButton = bottomSheet.findViewById(R.id.cancel_upcoming_ride_button)
                    nextRideButton = bottomSheet.findViewById(R.id.next_button_on_main_bs)
                    pickupCardView = bottomSheet.findViewById(R.id.pickup_from_airport_card)
                    dropToAirportCard = bottomSheet.findViewById(R.id.drop_to_airport_card)
                    dropToAirportCardLayout =
                        bottomSheet.findViewById(R.id.drop_to_airport_card_Layout)
                    pickupFromAirportLayout =
                        bottomSheet.findViewById(R.id.pickup_from_airport_card_layout)
                    dropIconBackground = bottomSheet.findViewById(R.id.drop_icon_image)
                    pickupIconBackground = bottomSheet.findViewById(R.id.pickup_icon_image)
                    goBackButton = bottomSheet.findViewById(R.id.go_back_button_on_main_bs)
                    getDataWithCurrentUserId()
                    handler.postDelayed({
                        if (ongoingTripIfUserDestroysTheAppList.size > 0) {
                            if(intent.getStringExtra("trip data for ongoing trip") == "coming directly to ongoing trip"){
                                ongoingRideButton!!.visibility = View.GONE
                                mainActBottomSheetBehavior!!.isHideable = true
                                mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                                init_ride_in_progress_bottomsheet()
                            }
                            else {
                                ongoingRideButton!!.visibility = View.VISIBLE
//                            thirdCardLayout!!.isEnabled = false
//                            thirdCardLayout!!.isClickable = false
                                thirdCardView!!.setOnTouchListener(object : View.OnTouchListener {
                                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                                        val snackBar = Snackbar.make(
                                            coordinatorLayout!!,
                                            "You have an Ongoing trip",
                                            Snackbar.LENGTH_INDEFINITE
                                        )
                                        snackBar.setActionTextColor(Color.CYAN)
                                        val snackbarView: View = snackBar.view
                                        val sbView: View = snackbarView
                                        snackBar.setTextColor(Color.WHITE)
                                        sbView.setBackgroundColor(Color.DKGRAY)
                                        snackBar.setAction("OK") { // Call your action method here
                                            snackBar.dismiss()
                                        }
                                        snackBar.show()
                                        return true
                                    }
                                })
                            }
                        } else if (ongoingTripIfUserDestroysTheAppList.size == 0) {
                            ongoingRideButton!!.visibility = View.GONE

                        }
                    }, 80)
//                    textView_result1!!.text = upcomingTripList.size.toString() + "value"
                    ongoingRideButton!!.setOnClickListener {
                        mainActBottomSheetBehavior!!.isHideable = true
                        mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                        init_ride_in_progress_bottomsheet()
                    }

                    val callbackForClosingTheApp = object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            mainActBottomSheetBehavior!!.isHideable = true
                            mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                            finish()
                            finishAffinity()
                        }
                    }
                    this@MapActivity.onBackPressedDispatcher.addCallback(
                        this@MapActivity,
                        callbackForClosingTheApp
                    )
//                    callDriverButton!!.setOnClickListener {
//                        val driverNumberToCallTo = "tel:" + "8208802742"
//                        var intent = Intent(Intent.ACTION_CALL)
//                        intent.setData(Uri.parse(driverNumberToCallTo))
//                        startActivity(intent)
//                    }
//                    val cancelUpcomingRideDialog = UpcomingRideCancelDialog(this@MapActivity)
//                    cancelRideButton!!.setOnClickListener {
//                        cancelUpcomingRideDialog.setCancelable(false)
//                        cancelUpcomingRideDialog.show()
//                        // hideSoftKeyboard(this@MapActivity)
//                        val cancel_trip_button =
//                            cancelUpcomingRideDialog.findViewById<AppCompatButton>(R.id.cancel_upcoming_ride_button_for_function)
//                        val cancel_dialog_button =
//                            cancelUpcomingRideDialog.findViewById<ImageButton>(R.id.cancel_upcoming_process)
//                        cancel_dialog_button.setOnClickListener { cancelUpcomingRideDialog.dismiss() }
//                        cancel_trip_button.setOnClickListener {
//                            try {
//                                updateDeleteDataRecordInDb()
//                                upcomingTripList.removeAt(rideClickedOnUpcomingView)
//                                //upcomingRideLogic()
//                                if (upcomingTripList.size == 1) {
//                                    nextRideButton!!.visibility = View.GONE
//                                    goBackButton!!.visibility = View.GONE
//                                    cancelRideButton!!.visibility = View.VISIBLE
//                                }
//                                if(upcomingTripList.isEmpty()){
//                                    upcomingTripList.removeAt(0)
//                                    secondView!!.visibility = View.GONE
//                                }
//                            }
//                            catch (e:Exception){e.printStackTrace()
//                            }
//                            handler.postDelayed({ cancelUpcomingRideDialog.dismiss() }, 500)
//                        }
//                    }
                    thirdCardView!!.setOnClickListener { val intent = Intent(this@MapActivity,RideHistoryActivity::class.java)
                        intent.putExtra("tab_value","0")
                        startActivity(intent)
                        finish()}
                    pickupCardView!!.setOnClickListener {

                        idCatcher = 1
                        doOnButtonClick(idCatcher)
                        handler.postDelayed({
                            //  init_from_to_airport_bottomsheet_layout(idCatcher)
                            handler.postDelayed({/*behavior1.state = BottomSheetBehavior.STATE_EXPANDED*/
                                launchLocationAutoCompleteActivity(dropOfRquestCode)
                            }, 230)
                            mainActBottomSheetBehavior!!.isHideable = true
                            mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                            mainActBottomSheetBehavior!!.shouldSkipSmoothAnimation()
                        }, 250)
                    }
                    dropToAirportCard!!.setOnClickListener {

                        idCatcher = 2
                        doOnButtonClick(idCatcher)
                        handler.postDelayed({
                            // init_from_to_airport_bottomsheet_layout(idCatcher)
                            handler.postDelayed({/*behavior1.state = BottomSheetBehavior.STATE_EXPANDED*/
                                launchLocationAutoCompleteActivity(pickupRequestCode)
                            }, 230)
                            mainActBottomSheetBehavior!!.isHideable = true
                            mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                            mainActBottomSheetBehavior!!.shouldSkipSmoothAnimation()
                        }, 250)
                    }
                }

                @SuppressLint("RestrictedApi")
                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }

                private fun doOnButtonClick(value: Int) {
                    if (value == 1) {

                        pickupCardView!!.setCardBackgroundColor(resources.getColor(R.color.white))
                        pickupFromAirportLayout!!.setBackgroundResource(R.drawable.view_with_green_border)
                        pickupIconBackground!!.setBackgroundResource(R.drawable.green_circle_view)
                        tripDetailsClass.tripType = "PickupFromAirport"
                        dropToAirportCard!!.setCardBackgroundColor(resources.getColor(R.color.veryLightGrey))
                        dropToAirportCardLayout!!.setBackgroundResource(R.color.veryLightGrey)
                        dropIconBackground!!.setBackgroundResource(R.drawable.grey_circle_view)
                    } else if (value == 2) {

                        dropToAirportCard!!.setCardBackgroundColor(resources.getColor(R.color.white))
                        dropToAirportCardLayout!!.setBackgroundResource(R.drawable.view_with_green_border)
                        dropIconBackground!!.setBackgroundResource(R.drawable.green_circle_view)
                        tripDetailsClass.tripType = "DropToAirport"
                        pickupCardView!!.setCardBackgroundColor(resources.getColor(R.color.veryLightGrey))
                        pickupFromAirportLayout!!.setBackgroundResource(R.color.veryLightGrey)
                        pickupIconBackground!!.setBackgroundResource(R.drawable.grey_circle_view)
                    }
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickupRequestCode || requestCode == dropOfRquestCode) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.d(TAG, "Place: " + place.name + " ," + place.id + ", " + place.latLng)
                    when (requestCode) {
                        dropOfRquestCode -> {
                            pickupValue = place.name
                            dropofvalue = airportTerminal2Text
                            pickupLatLng = place.latLng
                            pickupLatLngForMarker = pickupLatLng
                            val gcd = Geocoder(baseContext, Locale.getDefault())
                            val addresses: List<Address>?
                            addresses = gcd.getFromLocation(
                                place.latLng!!.latitude,
                                place.latLng!!.longitude,
                                1
                            )
                            if (addresses!!.isNotEmpty()) {
//                                textView_result1!!.text = addresses[0].locality
                                pickupLocalityValue = addresses[0].locality
                            }
                            dropLatLng = LatLng(19.0921, 72.8548)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                checkweatherAreaOfRequestIsInAreaOfService(
                                    pickupLocalityValue!!,
                                    "pickupfilled"
                                )
                            }
                        }
                        pickupRequestCode -> {
                            pickupValue = airportTerminal2Text
                            dropofvalue = place.name
                            dropLatLng = place.latLng
                            val gcd = Geocoder(baseContext, Locale.getDefault())
                            val addresses: List<Address>?
                            addresses = gcd.getFromLocation(
                                place.latLng!!.latitude,
                                place.latLng!!.longitude,
                                1
                            )
                            if (addresses!!.isNotEmpty()) {
//                                textView_result1!!.text = addresses[0].locality
                                dropLocalityValue = addresses[0].locality
                            }
                            pickupLatLng = LatLng(19.0921, 72.8548)
                            pickupLatLngForMarker = pickupLatLng
                            checkweatherAreaOfRequestIsInAreaOfService(
                                dropLocalityValue!!,
                                "dropfilled"
                            )
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status: Status = Autocomplete.getStatusFromIntent(data!!)
                    Log.d(TAG, status.statusMessage!!)
                }
                Activity.RESULT_CANCELED -> {
                    handler.postDelayed({
                        mainActBottomSheetBehavior!!.isHideable = false
                        mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    }, 500)
                    settingsActivityOpener!!.visibility = View.VISIBLE

                    val callbackToOpenMainBottomSheet = object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {

                        }
                    }
                    this@MapActivity.onBackPressedDispatcher.addCallback(
                        this@MapActivity,
                        callbackToOpenMainBottomSheet
                    )
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkweatherAreaOfRequestIsInAreaOfService(
        localityarea: String,
        pickupOrDropValue: String
    ) {
        if (localityarea == "Mumbai" || localityarea == "Thane" || localityarea == "Mira Bhayandar" || localityarea == "Andheri" || localityarea == "Girgaon") {

            handler.postDelayed({
                // init_persistent_bottomsheet()
                TimeAndDatePickerBottomSheetBehavior!!.isHideable = false
                TimeAndDatePickerBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    init_from_to_airport_values_layout()
                }
                fromToCardView!!.visibility = View.VISIBLE
                pickupTimeTextt!!.visibility = View.INVISIBLE
                endDestinationArrivalTime!!.visibility = View.INVISIBLE
            }, 300)
        } else {
            //init_persistent_bottomsheet()
            TimeAndDatePickerBottomSheetBehavior!!.isHideable = true
            TimeAndDatePickerBottomSheetBehavior!!.state =
                BottomSheetBehavior.STATE_HIDDEN
            weDonotProvideServiceHereBottomSheet(localityarea, pickupOrDropValue)
            weDoNotProvideServiceHereBsBehavior!!.isHideable = false
            weDoNotProvideServiceHereBsBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED

        }

    }

    private fun weDonotProvideServiceHereBottomSheet(
        localityarea: String,
        pickupOrDropValue: String
    ) {
        //  val persistantBottomSheet:View = coordinatorLayout!!.findViewById<LinearLayout>(R.id.we_dont_provide_service_here_layout)
        //  var behavoir =  BottomSheetBehavior.from(persistantBottomSheet)
        weDoNotProvideServiceHereBsBehavior!!.isHideable = true
        weDoNotProvideServiceHereBsBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        if (weDoNotProvideServiceHereBsBehavior != null) {
            var PlacewhereServiceNotProvide: TextView?
            var changeDropOrPickupLocationButton: Button?
            weDoNotProvideServiceHereBsBehavior!!.setBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    PlacewhereServiceNotProvide = bottomSheet.findViewById(R.id.place_heading)
                    changeDropOrPickupLocationButton =
                        bottomSheet.findViewById(R.id.change_drop_location)
                    PlacewhereServiceNotProvide!!.text = localityarea
                    when (pickupOrDropValue) {
                        "pickupfilled" -> {
                            changeDropOrPickupLocationButton!!.text = "Change Drop Location"
                            changeDropOrPickupLocationButton!!.setOnClickListener {
                                launchLocationAutoCompleteActivity(dropOfRquestCode)
                                weDoNotProvideServiceHereBsBehavior!!.isHideable = true
                                weDoNotProvideServiceHereBsBehavior!!.state =
                                    BottomSheetBehavior.STATE_HIDDEN
                            }
                            val callback = object : OnBackPressedCallback(true) {
                                override fun handleOnBackPressed() {
                                    launchLocationAutoCompleteActivity(dropOfRquestCode)
                                    weDoNotProvideServiceHereBsBehavior!!.isHideable = true
                                    weDoNotProvideServiceHereBsBehavior!!.state =
                                        BottomSheetBehavior.STATE_HIDDEN
                                }
                            }
                            this@MapActivity.onBackPressedDispatcher.addCallback(
                                this@MapActivity,
                                callback
                            )
                        }
                        "dropfilled" -> {
                            changeDropOrPickupLocationButton!!.text = "Change Pickup Location"
                            changeDropOrPickupLocationButton!!.setOnClickListener {
                                launchLocationAutoCompleteActivity(pickupRequestCode)
                                weDoNotProvideServiceHereBsBehavior!!.isHideable = true
                                weDoNotProvideServiceHereBsBehavior!!.state =
                                    BottomSheetBehavior.STATE_HIDDEN
                            }
                            val callback = object : OnBackPressedCallback(true) {
                                override fun handleOnBackPressed() {
                                    launchLocationAutoCompleteActivity(pickupRequestCode)
                                    weDoNotProvideServiceHereBsBehavior!!.isHideable = true
                                    weDoNotProvideServiceHereBsBehavior!!.state =
                                        BottomSheetBehavior.STATE_HIDDEN
                                }
                            }
                            this@MapActivity.onBackPressedDispatcher.addCallback(
                                this@MapActivity,
                                callback
                            )
                        }
                    }
                }

            })

        }
    }

    private fun init_ride_in_progress_bottomsheet() {
        rideInProgressBottomSheetBehavior!!.isHideable = false
        rideInProgressBottomSheetBehavior!!.isDraggable = false
        rideInProgressBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        if (rideInProgressBottomSheetBehavior != null) {
            var shareDetailsButton: RelativeLayout?
            var endRideButton: RelativeLayout?
            var rideEndTime: TextView?
            var driverNameView: TextView?
            var carNumberView: TextView?
            var carNameView: TextView?
            var fromLocationOngoing: TextInputLayout?
            var toLocationOngoing: TextInputLayout?
            var amountOfTheRideOngoing: TextView?
            var avgtimeOfTheRideOngoing: TextView
            var expandOngoingBs: ImageView?
            var collapseOngoingBs: ImageView?
            var total_amount_time_layoutOngoing: LinearLayout?
            var lineDividerOngoing: View?
            var lineDivider1Ongoing: View?
            var from_to_ongoing_layout: ConstraintLayout?
            var pickup_time_of_ride: TextView?
            var drop_of_time_of_ride: TextView?
            var rideStartTimeInDateFormat: Date? = null
            rideInProgressBottomSheetBehavior!!.setBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {

                }

                @SuppressLint("SimpleDateFormat", "SetTextI18n")
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    shareDetailsButton = bottomSheet.findViewById(R.id.share_details_button)
                    endRideButton = bottomSheet.findViewById(R.id.cancel_ride_button)
                    rideEndTime = bottomSheet.findViewById(R.id.ride_end_time)
                    driverNameView = bottomSheet.findViewById(R.id.drivers_name)
                    carNumberView = bottomSheet.findViewById(R.id.car_number)
                    carNameView = bottomSheet.findViewById(R.id.car_name)
                    pickup_time_of_ride = bottomSheet.findViewById(R.id.pickup_time_on_Ongoing)
                    drop_of_time_of_ride = bottomSheet.findViewById(R.id.drop_time_On_Ongoing)
                    fromLocationOngoing =
                        bottomSheet.findViewById(R.id.pickUpTextView_mainBsOngoing)
                    toLocationOngoing = bottomSheet.findViewById(R.id.dropTextView_mainBsOngoing)
                    amountOfTheRideOngoing = bottomSheet.findViewById(R.id.price_ongoing)
                    avgtimeOfTheRideOngoing = bottomSheet.findViewById(R.id.avg_time_text_ongoing)
                    expandOngoingBs = bottomSheet.findViewById(R.id.expand_bs_button)
                    collapseOngoingBs = bottomSheet.findViewById(R.id.collapse_bs_button)
                    total_amount_time_layoutOngoing =
                        bottomSheet.findViewById(R.id.price_time_view_ongoing)
                    lineDividerOngoing = bottomSheet.findViewById(R.id.line_divider_ongoing)
                    lineDivider1Ongoing = bottomSheet.findViewById(R.id.line_divider1_ongoing)
                    from_to_ongoing_layout =
                        bottomSheet.findViewById(R.id.from_to_text_layout_ongoing)
                    val ongoingRideUi =
                        bottomSheet.findViewById<ConstraintLayout>(R.id.ongoing_ride_ui)
                    if (ongoingTripIfUserDestroysTheAppList.size == 0) {

                        val endOnGoingRideDialog = EndRideDialog(this@MapActivity)
                        val getRideTime = calculationUtilClass.getDateFromEpoch(ongoingTrip.endTime)
                        val getRideStartTime =
                            calculationUtilClass.getDateFromEpoch(ongoingTrip.startTime)
                        carNumberView!!.text = ongoingTrip.carId
                        driverNameView!!.text = ongoingTrip.driverName
                        carNameView!!.text = ongoingTrip.carName
                        fromLocationOngoing!!.editText!!.setText(ongoingTrip.startLocationAddress)
                        toLocationOngoing!!.editText!!.setText(ongoingTrip.endLocationAddress)
                        amountOfTheRideOngoing!!.text = ongoingTrip.price
                        avgtimeOfTheRideOngoing.text = ongoingTrip.timeTakenForRide + " mins"
                        val format = SimpleDateFormat("E, dd MMM hh:mm aa")
                        try {
                            val date: Date? = format.parse(getRideTime!!)
                            val startDate: Date? = format.parse(getRideStartTime!!)
                            rideEndTimeInDateFormat = date
                            rideStartTimeInDateFormat = startDate
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        val getHourTime = SimpleDateFormat("hh", Locale.getDefault()).format(
                            rideEndTimeInDateFormat!!
                        )
                        val getMinuteTime = SimpleDateFormat("mm", Locale.getDefault()).format(
                            rideEndTimeInDateFormat!!
                        )
                        val getAmPm = SimpleDateFormat("aa", Locale.getDefault()).format(
                            rideEndTimeInDateFormat!!
                        )

                        val getStartHourTime = SimpleDateFormat("hh", Locale.getDefault()).format(
                            rideStartTimeInDateFormat!!
                        )
                        val getStartMinuteTime = SimpleDateFormat("mm", Locale.getDefault()).format(
                            rideStartTimeInDateFormat!!
                        )
                        val getStartAmPm = SimpleDateFormat("aa", Locale.getDefault()).format(
                            rideStartTimeInDateFormat!!
                        )
                        rideEndTime!!.text = "$getHourTime:$getMinuteTime $getAmPm"
                        pickup_time_of_ride!!.text =
                            "$getStartHourTime:$getStartMinuteTime $getStartAmPm"
                        drop_of_time_of_ride!!.text = "$getHourTime:$getMinuteTime $getAmPm"
                        expandOngoingBs!!.setOnClickListener {
                            expandOngoingBs!!.visibility = View.GONE
                            collapseOngoingBs!!.visibility = View.VISIBLE
                            from_to_ongoing_layout!!.visibility = View.VISIBLE
                            total_amount_time_layoutOngoing!!.visibility = View.VISIBLE
                            lineDividerOngoing!!.visibility = View.VISIBLE
                            lineDivider1Ongoing!!.visibility = View.VISIBLE
                        }
                        collapseOngoingBs!!.setOnClickListener {
                            expandOngoingBs!!.visibility = View.VISIBLE
                            collapseOngoingBs!!.visibility = View.GONE
                            from_to_ongoing_layout!!.visibility = View.GONE
                            total_amount_time_layoutOngoing!!.visibility = View.GONE
                            lineDividerOngoing!!.visibility = View.GONE
                            lineDivider1Ongoing!!.visibility = View.GONE
                        }
//                    val callback = object : OnBackPressedCallback(true) {
//                        override fun handleOnBackPressed() {
////                            var ongoingRideButtonOnMsinBs = mainActBottomSheet!!.findViewById<LinearLayout>(R.id.activeOngoingRide)
////                            ongoingRideButtonOnMsinBs.visibility = View.VISIBLE
//                            rideInProgressBottomSheetBehavior!!.isHideable = true
//                            rideInProgressBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
//                            mainActBottomSheetBehavior!!.isHideable = false
//                            mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
//                        }
//                    }
//                    this@MapActivity.onBackPressedDispatcher.addCallback(this@MapActivity,callback)
                        endRideButton!!.setOnClickListener {
                            endOnGoingRideDialog.setCancelable(false)
                            endOnGoingRideDialog.show()
                            val end_trip_button =
                                endOnGoingRideDialog.findViewById<AppCompatButton>(R.id.end_current_ongoing_ride)
                            val cancel_end_dialog_button =
                                endOnGoingRideDialog.findViewById<ImageButton>(R.id.cancel_ongoing_process)
                            cancel_end_dialog_button.setOnClickListener { endOnGoingRideDialog.dismiss() }
                            end_trip_button.setOnClickListener {
                                endOnGoingRideDialog.dismiss()
                                ongoingRideUi.visibility = View.GONE
                                tripDetailsClass.statuso = "Completed"
                                updateRideStatus()
                                if (ongoingTrip.paymentStatus == "" || ongoingTrip.paymentStatus == "notPaid") {
//                            rideCompletedBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                                    val intent = Intent(
                                        this@MapActivity,
                                        RideCompletedWithoutPayment::class.java
                                    )
                                    intent.putExtra("startLatitude", ongoingTrip.startLocationLat)
                                    intent.putExtra("startLongitude", ongoingTrip.startLocationLng)
                                    intent.putExtra("endLatitude", ongoingTrip.endLocationLat)
                                    intent.putExtra("endLongitude", ongoingTrip.endLocationLng)
                                    intent.putExtra("StartLocation", ongoingTrip.startLocationAddress)
                                    intent.putExtra("endLocation", ongoingTrip.endLocationAddress)
                                    intent.putExtra("carNumber", ongoingTrip.carId)
                                    intent.putExtra("RideFare", ongoingTrip.price)
                                    intent.putExtra("RideDistance", ongoingTrip.distance)
                                    intent.putExtra("RideTime", ongoingTrip.timeTakenForRide)
                                    intent.putExtra("RidePrice", ongoingTrip.price)
                                    intent.putExtra("RideStartTime", ongoingTrip.startTime)
                                    intent.putExtra("userId", ongoingTrip.userId)
                                    intent.putExtra("startTime", ongoingTrip.startTime)
                                    intent.putExtra("tripId", ongoingTrip.tripId)
                                    intent.putExtra("carbonEmissionFunction", ongoingTrip.carbonEmissionValue)
//                                    intent.putExtra("carID",ongoingTrip.carId)
                                    intent.putExtra("driverContact", ongoingTrip.driverContact)
                                    intent.putExtra("driverName", ongoingTrip.driverName)
                                    intent.putExtra("userPhoneNumber", currentUserphoneNumber)
                                    intent.putExtra("userEmail", currentUserEmailId)
                                    intent.putExtra("userName", currentUserName)
                                    intent.putExtra("rideEndTimeInDateFormat", rideEndTime!!.text.toString())
                                    startActivity(intent)
                                    finish()
                                } else if (ongoingTrip.paymentStatus == "Paid") {
//                            rideCompletedBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                                    val intent = Intent(
                                        this@MapActivity,
                                        PaymentSuccessfullActivityMain::class.java
                                    )
                                    intent.putExtra("startLatitude", ongoingTrip.startLocationLat)
                                    intent.putExtra("startLongitude", ongoingTrip.startLocationLng)
                                    intent.putExtra("endLatitude", ongoingTrip.endLocationLat)
                                    intent.putExtra("endLongitude", ongoingTrip.endLocationLng)
                                    intent.putExtra("RideDistance", ongoingTrip.distance)
                                    intent.putExtra("RideTime", ongoingTrip.timeTakenForRide)
                                    intent.putExtra("RidePrice", ongoingTrip.price)
                                    intent.putExtra("driverName", ongoingTrip.driverName)
                                    intent.putExtra("driverContact", ongoingTrip.driverContact)
                                    intent.putExtra("RideStartTime", ongoingTrip.startTime)
                                    intent.putExtra("userId", ongoingTrip.userId)
                                    intent.putExtra("tripId", ongoingTrip.tripId)
                                    intent.putExtra("driveContact", ongoingTrip.driverContact)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                        shareDetailsButton!!.setOnClickListener {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                                val shareMessage =
                                    "Below is my ride details\n" + "Trip Type : ${ongoingTrip.tripType} \n" + "Trip Date and Time : ${
                                        calculationUtilClass.getDateFromEpoch(ongoingTrip.startTime)
                                    } \n" + "Start Location : ${ongoingTrip.startLocationAddress} \n" + "End Location : ${ongoingTrip.endLocationAddress} \n" + "Driver Name : ${ongoingTrip.driverName} \n" + "Driver Phone Number : ${ongoingTrip.driverContact} \n " + "Car number : ${ongoingTrip.carId} \n" + "Drop-off by ${getHourTime}:${getMinuteTime} $getAmPm"
                                putExtra(Intent.EXTRA_TEXT, shareMessage)
                            }
                            startActivity(
                                Intent.createChooser(
                                    shareIntent,
                                    "Share ride details with"
                                )
                            )
                        }
                    }
                    if (ongoingTripIfUserDestroysTheAppList.size > 0) {

                        val endOnGoingRideDialog = EndRideDialog(this@MapActivity)
                        val getRideTime = calculationUtilClass.getDateFromEpoch(
                            ongoingTripIfUserDestroysTheAppList[0].endTime
                        )
                        val getRideStartTime = calculationUtilClass.getDateFromEpoch(
                            ongoingTripIfUserDestroysTheAppList[0].startTime
                        )
                        if(TextUtils.isDigitsOnly(ongoingTripIfUserDestroysTheAppList[0].driverContact)){
                            driverNameView!!.text = ongoingTripIfUserDestroysTheAppList[0].driverName
                        }else{
                            driverNameView!!.text = ongoingTripIfUserDestroysTheAppList[0].driverName
                        }
                        carNumberView!!.text = ongoingTripIfUserDestroysTheAppList[0].carId
                        carNameView!!.text = ongoingTripIfUserDestroysTheAppList[0].carName
                        fromLocationOngoing!!.editText!!.setText(ongoingTripIfUserDestroysTheAppList[0].startLocationAddress)
                        toLocationOngoing!!.editText!!.setText(ongoingTripIfUserDestroysTheAppList[0].endLocationAddress)
                        amountOfTheRideOngoing!!.text = ongoingTripIfUserDestroysTheAppList[0].price
                        avgtimeOfTheRideOngoing.text =
                            ongoingTripIfUserDestroysTheAppList[0].timeTakenForRide + " mins"
                        val format = SimpleDateFormat("E, dd MMM hh:mm aa")
                        try {
                            val date: Date? = format.parse(getRideTime!!)
                            val startDate: Date? = format.parse(getRideStartTime!!)
                            rideEndTimeInDateFormat = date
                            rideStartTimeInDateFormat = startDate
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        val getHourTime = SimpleDateFormat("hh", Locale.getDefault()).format(
                            rideEndTimeInDateFormat!!
                        )
                        val getMinuteTime = SimpleDateFormat("mm", Locale.getDefault()).format(
                            rideEndTimeInDateFormat!!
                        )
                        val getAmPm = SimpleDateFormat("aa", Locale.getDefault()).format(
                            rideEndTimeInDateFormat!!
                        )

                        val getStartHourTime = SimpleDateFormat("hh", Locale.getDefault()).format(
                            rideStartTimeInDateFormat!!
                        )
                        val getStartMinuteTime = SimpleDateFormat("mm", Locale.getDefault()).format(
                            rideStartTimeInDateFormat!!
                        )
                        val getStartAmPm = SimpleDateFormat("aa", Locale.getDefault()).format(
                            rideStartTimeInDateFormat!!
                        )
                        rideEndTime!!.text = "$getHourTime:$getMinuteTime $getAmPm"
                        pickup_time_of_ride!!.text =
                            "$getStartHourTime:$getStartMinuteTime $getStartAmPm"
                        drop_of_time_of_ride!!.text = "$getHourTime:$getMinuteTime $getAmPm"
                        expandOngoingBs!!.setOnClickListener {
                            expandOngoingBs!!.visibility = View.GONE
                            collapseOngoingBs!!.visibility = View.VISIBLE
                            from_to_ongoing_layout!!.visibility = View.VISIBLE
                            total_amount_time_layoutOngoing!!.visibility = View.VISIBLE
                            lineDividerOngoing!!.visibility = View.VISIBLE
                            lineDivider1Ongoing!!.visibility = View.VISIBLE
                        }
                        collapseOngoingBs!!.setOnClickListener {
                            expandOngoingBs!!.visibility = View.VISIBLE
                            collapseOngoingBs!!.visibility = View.GONE
                            from_to_ongoing_layout!!.visibility = View.GONE
                            total_amount_time_layoutOngoing!!.visibility = View.GONE
                            lineDividerOngoing!!.visibility = View.GONE
                            lineDivider1Ongoing!!.visibility = View.GONE
                        }
//
                        endRideButton!!.setOnClickListener {
                            endOnGoingRideDialog.setCancelable(false)
                            endOnGoingRideDialog.show()
                            val end_trip_button =
                                endOnGoingRideDialog.findViewById<AppCompatButton>(R.id.end_current_ongoing_ride)
                            val cancel_end_dialog_button =
                                endOnGoingRideDialog.findViewById<ImageButton>(R.id.cancel_ongoing_process)
                            cancel_end_dialog_button.setOnClickListener { endOnGoingRideDialog.dismiss() }
                            end_trip_button.setOnClickListener {
                                endOnGoingRideDialog.dismiss()
                                ongoingRideUi.visibility = View.GONE
                                ongoingTripIfUserDestroysTheAppList[0].statuso = "Completed"
                                updateRideStatusForOngoingIfUserDestroysTheApp()
                                if (ongoingTripIfUserDestroysTheAppList[0].paymentStatus == "" || ongoingTripIfUserDestroysTheAppList[0].paymentStatus == "notPaid") {
//                            rideCompletedBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                                    val intent = Intent(
                                        this@MapActivity,
                                        RideCompletedWithoutPayment::class.java
                                    )
                                    intent.putExtra("startLatitude", ongoingTripIfUserDestroysTheAppList[0].startLocationLat)
                                    intent.putExtra("startLongitude", ongoingTripIfUserDestroysTheAppList[0].startLocationLng)
                                    intent.putExtra("endLatitude", ongoingTripIfUserDestroysTheAppList[0].endLocationLat)
                                    intent.putExtra("endLongitude", ongoingTripIfUserDestroysTheAppList[0].endLocationLng)
                                    intent.putExtra("StartLocation", ongoingTripIfUserDestroysTheAppList[0].startLocationAddress)
                                    intent.putExtra("endLocation", ongoingTripIfUserDestroysTheAppList[0].endLocationAddress)
                                    intent.putExtra("carNumber", ongoingTripIfUserDestroysTheAppList[0].carId)
                                    intent.putExtra("RideFare", ongoingTripIfUserDestroysTheAppList[0].price)
                                    intent.putExtra("RideDistance", ongoingTripIfUserDestroysTheAppList[0].distance)
                                    intent.putExtra("RideTime", ongoingTripIfUserDestroysTheAppList[0].timeTakenForRide)
                                    intent.putExtra("RidePrice", ongoingTripIfUserDestroysTheAppList[0].price)
                                    intent.putExtra("userPhoneNumber", currentUserphoneNumber)
                                    intent.putExtra("userEmail", currentUserEmailId)
                                    intent.putExtra("userName", currentUserName)
                                    intent.putExtra(
                                        "RideStartThime",
                                        ongoingTripIfUserDestroysTheAppList[0].startTime
                                    )
                                    intent.putExtra(
                                        "userId",
                                        ongoingTripIfUserDestroysTheAppList[0].userId
                                    )
                                    intent.putExtra(
                                        "startTime",
                                        ongoingTripIfUserDestroysTheAppList[0].startTime
                                    )
                                    intent.putExtra(
                                        "tripId",
                                        ongoingTripIfUserDestroysTheAppList[0].tripId
                                    )
                                    intent.putExtra(
                                        "rideEndTimeInDateFormat",
                                        rideEndTime!!.text.toString()
                                    )
                                    intent.putExtra(
                                        "carbonEmissionFunction",
                                        ongoingTripIfUserDestroysTheAppList[0].carbonEmissionValue
                                    )
                                    intent.putExtra(
                                        "driverContact",
                                        ongoingTripIfUserDestroysTheAppList[0].driverContact
                                    )
                                    intent.putExtra(
                                        "driverName",
                                        ongoingTripIfUserDestroysTheAppList[0].driverName
                                    )
                                    startActivity(intent)
                                    finish()
                                } else if (ongoingTripIfUserDestroysTheAppList[0].paymentStatus == "Paid") {
//                            rideCompletedBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                                    val intent = Intent(
                                        this@MapActivity,
                                        PaymentSuccessfullActivityMain::class.java
                                    )
                                    intent.putExtra(
                                        "startLatitude",
                                        ongoingTripIfUserDestroysTheAppList[0].startLocationLat
                                    )
                                    intent.putExtra(
                                        "startLongitude",
                                        ongoingTripIfUserDestroysTheAppList[0].startLocationLng
                                    )
                                    intent.putExtra(
                                        "endLatitude",
                                        ongoingTripIfUserDestroysTheAppList[0].endLocationLat
                                    )
                                    intent.putExtra(
                                        "endLongitude",
                                        ongoingTripIfUserDestroysTheAppList[0].endLocationLng
                                    )
                                    intent.putExtra(
                                        "RideDistance",
                                        ongoingTripIfUserDestroysTheAppList[0].distance
                                    )
                                    intent.putExtra(
                                        "RideTime",
                                        ongoingTripIfUserDestroysTheAppList[0].timeTakenForRide
                                    )
                                    intent.putExtra(
                                        "RidePrice",
                                        ongoingTripIfUserDestroysTheAppList[0].price
                                    )
                                    intent.putExtra(
                                        "driverName",
                                        ongoingTripIfUserDestroysTheAppList[0].driverContact
                                    )
                                    intent.putExtra(
                                        "RideStartTime",
                                        ongoingTripIfUserDestroysTheAppList[0].startTime
                                    )
                                    intent.putExtra(
                                        "userId",
                                        ongoingTripIfUserDestroysTheAppList[0].userId
                                    )
                                    intent.putExtra(
                                        "tripId",
                                        ongoingTripIfUserDestroysTheAppList[0].tripId
                                    )
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                        shareDetailsButton!!.setOnClickListener {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                                val shareMessage =
                                    "Below is my ride details\n" + "Trip Type : ${ongoingTripIfUserDestroysTheAppList[0].tripType} \n" + "Trip Date and Time : ${
                                        calculationUtilClass.getDateFromEpoch(
                                            ongoingTripIfUserDestroysTheAppList[0].startTime
                                        )
                                    } \n" + "Start Location : ${ongoingTripIfUserDestroysTheAppList[0].startLocationAddress} \n" + "End Location : ${ongoingTripIfUserDestroysTheAppList[0].endLocationAddress} \n" + "Driver Name : ${ongoingTripIfUserDestroysTheAppList[0].driverName} \n" + "Driver Phone Number : ${ongoingTripIfUserDestroysTheAppList[0].driverContact} \n " + "Driver car number : ${ongoingTripIfUserDestroysTheAppList[0].carId} \n" + "Drop-off by ${getHourTime}:${getMinuteTime} $getAmPm"
                                putExtra(Intent.EXTRA_TEXT, shareMessage)
                            }
                            startActivity(
                                Intent.createChooser(
                                    shareIntent,
                                    "Share ride details with"
                                )
                            )
                        }

                    }
                }

                private fun updateRideStatusForOngoingIfUserDestroysTheApp() {
                    val tripStatus = ongoingTripIfUserDestroysTheAppList[0].statuso.trim()
                    val db = FirebaseFirestore.getInstance()
                    //  val tripId = db.collection("tripDetails").document().id
                    val trips = hashMapOf("status" to tripStatus)
                    val query = db.collection("tripDetails")
//                        .whereEqualTo("userId",userId)
                        .whereEqualTo("tripId", ongoingTripIfUserDestroysTheAppList[0].tripId)
                        //.whereEqualTo("status", "Initiated")
                        .get()
                    query.addOnSuccessListener { result ->
                        for (document in result) {
                            db.collection("tripDetails").document(document.id)
                                .set(trips, SetOptions.merge())

                        }
                    }

                }

                private fun updateRideStatus() {
                    val tripStatus = tripDetailsClass.statuso.trim()
                    val db = FirebaseFirestore.getInstance()
                    //  val tripId = db.collection("tripDetails").document().id
                    val trips = hashMapOf("status" to tripStatus)
                    val query = db.collection("tripDetails")
//                        .whereEqualTo("userId",userId)
                        .whereEqualTo("tripId", ongoingTrip.tripId)
                        //.whereEqualTo("status", "Initiated")
                        .get()
                    query.addOnSuccessListener { result ->
                        for (document in result) {
                            db.collection("tripDetails").document(document.id)
                                .set(trips, SetOptions.merge())

                        }
                    }

                }
            })
        }
    }

    private fun init_book_ride_details() {
        // val persistantBottomSheet : View = coordinatorLayout!!.findViewById(R.id.ride_details_bottomsheet)
        // val behavior = BottomSheetBehavior.from(persistantBottomSheet)
//        var openOnGoingRide = findViewById<Button>(R.id.open_ride_details_bottomsheet)
        rideDetailsBottomSheetBehavior!!.isHideable = true
        rideDetailsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        if (rideDetailsBottomSheetBehavior != null) {
            var monthNumber: String
            var day: String
            var year: String
            var rideDate: TextView?
            var durationText: TextView?
            var totalPriceOfTheRide: TextView?
            var payAfterRideButton: RelativeLayout?
            var payNowButton: RelativeLayout?
            var finaldistanceOfRide: String?
            var durationInTrafficeInseconds: Double
            var carbonEmissionSavedValue: TextView?
            rideDetailsBottomSheetBehavior!!.setBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                @RequiresApi(Build.VERSION_CODES.M)
                @SuppressLint("SetTextI18n")
                override fun onStateChanged(bottomSheet: View, newState: Int) {

                    durationText = bottomSheet.findViewById(R.id.estimated_time_text)
                    rideDate = bottomSheet.findViewById(R.id.ride_date)
                    carbonEmissionSavedValue = bottomSheet.findViewById(R.id.carbonEmissionSavedValue)
                    val monthNameOfTheRide = DateUtils.getFullMonthName(selectedMonthForRide)
                    val rideDateFromArrayList = arrayList[arrayListPositionClicked]
                    monthNumber = SimpleDateFormat(
                        "MM",
                        Locale.getDefault()
                    ).format(rideDateFromArrayList)// 06
                    day = SimpleDateFormat("dd", Locale.getDefault()).format(rideDateFromArrayList)
                    year =
                        SimpleDateFormat("yyyy", Locale.getDefault()).format(rideDateFromArrayList)
                    totalPriceOfTheRide =
                        bottomSheet.findViewById(R.id.total_price_of_the_ride_cost)
                    rideDate!!.text = "$day $monthNameOfTheRide $year"
                    durationInTrafficeInseconds = durationInSeconds!!.toDouble()
                    val durationInTrafficConversionFromSecondsToMin =
                        durationInTrafficeInseconds / 60
                    val finalDistanceOfRide = Math.ceil(durationInTrafficConversionFromSecondsToMin)
                    finaldistanceOfRide = finalDistanceOfRide.toString()
                    durationText!!.text = "${finalDistanceOfRide.toInt().toString()} mins"
                    carbonEmissionSavedValue!!.text =
                        calculationUtilClass.carbonEmissionCalculator(distValue) + " g"
                    // totalPriceOfTheRide!!.text = finalfaree
                    payAfterRideButton = bottomSheet.findViewById(R.id.pay_after_ride_button)
                    payNowButton = bottomSheet.findViewById(R.id.pay_now_button)
                    payNowButton!!.setOnClickListener {
                        if (totalPriceOfTheRide!!.text == "") {

                        } else if (totalPriceOfTheRide!!.text != "") {
                            val selectedDateForRideInString =
                                "${day}-${monthNumber}-${year} ${selectedHourForRide}:${selectedMinuteForRide}"
                            val selectedDateForRideInEpoch =
                                calculationUtilClass.convertDateToEpochConverter(
                                    selectedDateForRideInString
                                )
                            tripDetailsClass.statuso = "Initiated"
                            tripDetailsClass.paymentStatus = "notPaid"
                            tripDetailsClass.startTime = selectedDateForRideInEpoch.toString()
                            tripDetailsClass.startLocationAddress = dropofvalue.toString()
                            tripDetailsClass.endLocationAddress = pickupValue.toString()
                            tripDetailsClass.startLocationLat = pickupLatLng!!.latitude.toString()
                            tripDetailsClass.startLocationLng = pickupLatLng!!.longitude.toString()
                            tripDetailsClass.endLocationLat = dropLatLng!!.latitude.toString()
                            tripDetailsClass.endLocationLng = dropLatLng!!.longitude.toString()
                            tripDetailsClass.timeTakenForRide = finaldistanceOfRide!!.toString()
                            tripDetailsClass.OTP = ""
                            tripDetailsClass.distance = distValue.toString() + "Kms"
                            tripDetailsClass.carbonEmissionValue =
                                calculationUtilClass.carbonEmissionCalculator(distValue)
                            val rideTimeInMilliSeconds =
                                tripDetailsClass.startTime.toLong() + (durationInTrafficeInseconds * 1000).toLong()
                            tripDetailsClass.endTime = rideTimeInMilliSeconds.toString()
                            tripDetailsClass.price = finalFareOfRide!!.toString()
                            tripDetailsClass.paymentMethod = "PaidCash"
                            updateRecord()
                            db.collection("tripDetails")
                                .whereEqualTo("userId", userId)
                                .whereEqualTo("status", "Initiated")
                                .get()
                                .addOnSuccessListener { document ->
                                    for (documentt in document) {
                                        currentTripId = documentt.id
                                    }

                                    val intent = Intent(this@MapActivity, RazorPayment::class.java)
                                    val docRef = db.collection("users").document(userId)
                                    docRef.get()
                                        .addOnSuccessListener { document ->
                                            if (document != null) {
                                                currentUserName = document.getString("displayName").toString()
                                                currentUserEmailId = document.getString("email").toString()

                                                val paymentData = PaymentData("INR", finalFareOfRide!!, "Ride Fare", currentTripId, userId, currentUserName.toString(), currentUserphoneNumber.toString(), currentUserEmailId.toString()
                                                )
                                                intent.putExtra("paymentData", paymentData)
                                                intent.putExtra("userId", userId)
                                                intent.putExtra("tripId", currentTripId)
                                                intent.putExtra("rideStatusF", "Initiated")
                                                startActivity(intent)
                                            }
                                        }
                                }
                        }
                    }
                    payAfterRideButton!!.setOnClickListener {
                        if (totalPriceOfTheRide!!.text == "") {

                        } else if (totalPriceOfTheRide!!.text != "") {
                            tripDetailsClass.paymentStatus = "notPaid"
                            rideDetailsBottomSheetBehavior!!.isHideable = true
                            rideDetailsBottomSheetBehavior!!.state =
                                BottomSheetBehavior.STATE_HIDDEN
                            val selectedDateForRideInString =
                                "${day}-${monthNumber}-${year} ${selectedHourForRide}:${selectedMinuteForRide}"
                            val selectedDateForRideInEpoch =
                                calculationUtilClass.convertDateToEpochConverter(
                                    selectedDateForRideInString
                                )
                            tripDetailsClass.statuso = "Schedule"
                            tripDetailsClass.startTime = selectedDateForRideInEpoch.toString()
                            tripDetailsClass.OTP = ""
                            tripDetailsClass.startLocationAddress = dropofvalue.toString()
                            tripDetailsClass.endLocationAddress = pickupValue.toString()
                            tripDetailsClass.startLocationLat = pickupLatLng!!.latitude.toString()
                            tripDetailsClass.startLocationLng = pickupLatLng!!.longitude.toString()
                            tripDetailsClass.endLocationLat = dropLatLng!!.latitude.toString()
                            tripDetailsClass.endLocationLng = dropLatLng!!.longitude.toString()
                            tripDetailsClass.timeTakenForRide = finaldistanceOfRide!!.toString()
                            tripDetailsClass.distance = distValue.toString() + "Kms"
                            tripDetailsClass.paymentMethod = "PaidCash"
                            val newOTP = calculationUtilClass.getOTP(4)
                            tripDetailsClass.OTP = newOTP
                            tripDetailsClass.carbonEmissionValue =
                                calculationUtilClass.carbonEmissionCalculator(distValue)
                            tripDetailsClass.price = finalFareOfRide!!
                            val rideTimeInMilliSeconds =
                                tripDetailsClass.startTime.toLong() + (durationInTrafficeInseconds * 1000).toLong()
                            tripDetailsClass.endTime = rideTimeInMilliSeconds.toString()
                            // val persistantBottomSheet1 : View = coordinatorLayout!!.findViewById(R.id.ride_scheduled_bottomsheet1)
                            val pickupEditIcon =
                                findViewById<ImageButton>(R.id.pickup_location_edit_icon)
                            val dropOfEditIcon =
                                findViewById<ImageButton>(R.id.drop_to_location_edit_icon)
                            updateRecord()
                            if (pickupEditIcon.visibility == View.VISIBLE) {
                                pickupEditIcon.visibility = View.GONE
                            }
                            if (dropOfEditIcon.visibility == View.VISIBLE) {
                                dropOfEditIcon.visibility = View.GONE
                            }
                            rideScheduledSuccessfullyBottomSheetBehavior!!.isHideable = false
                            rideScheduledSuccessfullyBottomSheetBehavior!!.state =
                                BottomSheetBehavior.STATE_EXPANDED
                        }
                    }
                    val callback = object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            rideDetailsBottomSheetBehavior!!.isHideable = true
                            rideDetailsBottomSheetBehavior!!.state =
                                BottomSheetBehavior.STATE_HIDDEN
                            TimeAndDatePickerBottomSheetBehavior!!.isHideable = false
                            TimeAndDatePickerBottomSheetBehavior!!.state =
                                BottomSheetBehavior.STATE_EXPANDED
                        }
                    }
                    this@MapActivity.onBackPressedDispatcher.addCallback(this@MapActivity, callback)
                }


                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }

            })
        }
    }

    private fun init_payment_successful() {
        //val persistantBottomSheet : View = coordinatorLayout!!.findViewById(R.id.ride_scheduled_bottomsheet1)
        // val behavior = BottomSheetBehavior.from(persistantBottomSheet)
        val intentAnswer = intent
        val extraVal = intentAnswer.getStringExtra("paymentStatus")
        rideScheduledSuccessfullyBottomSheetBehavior!!.isHideable = true
        rideScheduledSuccessfullyBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN

        if (rideScheduledSuccessfullyBottomSheetBehavior != null) {

            var lottieAnim: LottieAnimationView?
            var got_it_button: AppCompatButton?
            rideScheduledSuccessfullyBottomSheetBehavior!!.setBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {

                }

                @RequiresApi(Build.VERSION_CODES.M)
                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    lottieAnim = bottomSheet.findViewById(R.id.animationView_ride_booked_successfull1)
                    got_it_button = bottomSheet.findViewById(R.id.got_it_button)
                    lottieAnim!!.playAnimation()
                    lottieAnim!!.setMinAndMaxProgress(0.0f, 0.8f)
                    got_it_button!!.setOnClickListener {
                        upcomingTripList.clear()
                        setBlankData()
                        //  handler.postDelayed({getDataWithCurrentUserId()},70)
                        mMap.clear()
                        polylineAnimator.cancel()
                        //  upcomingRideLogic()
                        rideScheduledSuccessfullyBottomSheetBehavior!!.isHideable = true
                        rideScheduledSuccessfullyBottomSheetBehavior!!.state =
                            BottomSheetBehavior.STATE_HIDDEN
                        mainActBottomSheetBehavior!!.isHideable = false
                        fromToCardView!!.visibility = View.GONE
                        mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                        if (settingsActivityOpener!!.visibility == View.GONE) {
                            settingsActivityOpener!!.visibility = View.VISIBLE
                        }

                    }
                    val callback = object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            upcomingTripList.clear()
                            setBlankData()
                            getDataWithCurrentUserId()
                            //  upcomingRideLogic()
                            rideScheduledSuccessfullyBottomSheetBehavior!!.isHideable = true
                            rideScheduledSuccessfullyBottomSheetBehavior!!.state =
                                BottomSheetBehavior.STATE_HIDDEN
                            mainActBottomSheetBehavior!!.isHideable = false
                            fromToCardView!!.visibility = View.GONE
                            mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }
                    this@MapActivity.onBackPressedDispatcher.addCallback(this@MapActivity, callback)

                }
            })
            if (extraVal == null || extraVal == "") {
                tripDetailsClass.paymentStatus = "notPaid"

            } else if (extraVal == "Paid") {
                // tripDetailsClass.paymentStatus = "Paid"
                rideScheduledSuccessfullyBottomSheetBehavior!!.isHideable = false
                rideScheduledSuccessfullyBottomSheetBehavior!!.state =
                    BottomSheetBehavior.STATE_EXPANDED
                val successAnim =
                    rideScheduledSuccessfullyBottomSheet!!.findViewById<LottieAnimationView>(R.id.animationView_ride_booked_successfull1)
                successAnim!!.playAnimation()
                successAnim.setMinAndMaxProgress(0.0f, 0.8f)
                val gotItButton =
                    rideScheduledSuccessfullyBottomSheet!!.findViewById<AppCompatButton>(R.id.got_it_button)
                gotItButton.setOnClickListener {
                    rideScheduledSuccessfullyBottomSheetBehavior!!.isHideable = true
                    rideScheduledSuccessfullyBottomSheetBehavior!!.state =
                        BottomSheetBehavior.STATE_HIDDEN
                    upcomingTripList.clear()
                    setBlankData()
                    //    handler.postDelayed({getDataWithCurrentUserId()},70)
                    mMap.clear()
                    polylineAnimator.cancel()
                    //updateRecordForPayment()
                    mainActBottomSheetBehavior!!.isHideable = false
                    mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    if (settingsActivityOpener!!.visibility == View.GONE) {
                        settingsActivityOpener!!.visibility = View.VISIBLE
                    }
                    fromToCardView!!.visibility = View.GONE
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (tripDetailsClass.paymentStatus == "notPaid") {
            fromToCardView!!.visibility = View.GONE
        }
    }

    override fun onRestart() {
        super.onRestart()
        val pickupTextView =
            fromToCardView!!.findViewById<TextInputLayout>(R.id.pickUpTextView_main)
        val dropTextView = fromToCardView!!.findViewById<TextInputLayout>(R.id.dropTextView_main)
        if (pickupTextView.editText!!.text == null || pickupTextView.editText!!.text.toString() == "" || dropTextView.editText!!.text == null || dropTextView.editText!!.text.toString() == "") {
            fromToCardView!!.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        val prefs: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val editPrefs = prefs.edit()
        editPrefs.putString("pickupValue", pickupValue)
        editPrefs.putString("dropOffValue", dropofvalue)
        editPrefs.putString("pickupTime", pickupTimeTextt!!.text.toString())
        editPrefs.putString("dropTime", endDestinationArrivalTime!!.text.toString())
        editPrefs.putString("rideCompleted", ongoingTrip.statuso)

        editPrefs.apply()
    }

    override fun onResume() {
        super.onResume()
        val FromToView =
            coordinatorLayout!!.findViewById<ConstraintLayout>(R.id.pickup_drop_layout_seperate)
        val pickTime: TextView = FromToView.findViewById(R.id.pickup_time)
        val dropTime: TextView = FromToView.findViewById(R.id.drop_time)
        //  init_main_act_bottomsheet()
        val rideStatus = "k"
        if (onresumeCounter == 0) {
            val prefsResult: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            pickupValue1 = prefsResult.getString("dropOffValue", "")
            dropOffValue1 = prefsResult.getString("pickupValue", "")

            val pickupTextInputLayout: TextInputLayout =
                FromToView.findViewById(R.id.pickUpTextView_main)
            val dropTextInputLayout: TextInputLayout =
                FromToView.findViewById(R.id.dropTextView_main)
            val pickupEditIcon: ImageButton =
                FromToView.findViewById(R.id.pickup_location_edit_icon)
            val dropoffEditIcon: ImageButton =
                FromToView.findViewById(R.id.drop_to_location_edit_icon)
            if (pickupEditIcon.visibility == View.VISIBLE) {
                pickupEditIcon.visibility = View.GONE
            }
            if (dropoffEditIcon.visibility == View.VISIBLE) {
                dropoffEditIcon.visibility = View.GONE
            }
            val pickupTimee = prefsResult.getString("pickupTime", "234")
            val dropTimee = prefsResult.getString("dropTime", "234")
            pickTime.text = pickupTimee
            dropTime.text = dropTimee
            pickupTextInputLayout.editText!!.setText(pickupValue1)
            dropTextInputLayout.editText!!.setText(dropOffValue1)
            pickupTextInputLayout.isClickable = false
            pickupTextInputLayout.isEnabled = false
            dropTextInputLayout.isClickable = false
            dropTextInputLayout.isEnabled = false
            //    rideStatus = prefsResult.getString("rideCompleted","kmm").toString()
            onresumeCounter++
        } else if (onresumeCounter > 0) {
            networkConnection!!.observe(
                this@MapActivity,
                androidx.lifecycle.Observer { isConnected ->
                    if (isConnected) {
                        internetnotEnabledUi!!.visibility = View.GONE
                        if (fromToCardView!!.visibility == View.GONE) {
                            if (TimeAndDatePickerBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED || rideScheduledSuccessfullyBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED || rideDetailsBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                                fromToCardView!!.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        internetnotEnabledUi!!.visibility = View.VISIBLE
                        internetnotEnabledUi!!.setOnTouchListener { v, event -> true }
                    }
                })

        }
        val intentListener = intent
        val paymentStatusCatcher = intentListener.getStringExtra("paymentStatus")
        val catch = paymentStatusCatcher.toString()
        if (catch == "Paid" && rideScheduledSuccessfullyBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {

            // val paymentSuccessAnim = paymentSuccessfullBS.findViewById<LottieAnimationView>(R.id.animationView_ride_booked_successfull1)
            fromToCardView!!.visibility = View.VISIBLE
            pickTime.visibility = View.VISIBLE
            dropTime.visibility = View.VISIBLE
            settingsActivityOpener!!.visibility = View.GONE
        } else if (catch == "Paid" && rideStatus == "Completed") {
            val intent = Intent(this, PaymentSuccessfullActivityMain::class.java)
            startActivity(intent)

        }
        if (mainActBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED || rideInProgressBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED || weDoNotProvideServiceHereBsBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            fromToCardView!!.visibility = View.GONE
            if (mainActBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED || weDoNotProvideServiceHereBsBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                if (settingsActivityOpener!!.visibility == View.GONE) {
                    settingsActivityOpener!!.visibility = View.VISIBLE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setDouble(min: String?) {
        try {
            val parser = JsonParser()
            val obj: Any = parser.parse(min!!)
            val jsonobj = obj as JsonObject
            val dist = jsonobj["rows"] as JsonArray
            val obj2 = dist[0] as JsonObject
            val disting = obj2["elements"] as JsonArray
            val obj3 = disting[0] as JsonObject
            val obj4 = obj3["distance"] as JsonObject
            val obj5 = obj3["duration"] as JsonObject
            val obj6 = obj3["duration_in_traffic"] as JsonObject
            println(obj4["text"])
            println(obj5["text"])
            distanceFromSourceToDestination = obj4["value"].toString()
            durationInTraffic = obj6["text"].toString()
//            textView_result1!!.text = obj6["text"].toString()
            durationInSeconds = obj6["value"].toString()
        } catch (e: Exception) {
        }
    }

    private fun init_persistent_bottomsheet() {
        TimeAndDatePickerBottomSheetBehavior!!.isDraggable = false
        TimeAndDatePickerBottomSheetBehavior!!.isHideable = true
        TimeAndDatePickerBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        if (TimeAndDatePickerBottomSheetBehavior != null) {
            var confirmDetailsButton: Button?
            TimeAndDatePickerBottomSheetBehavior!!.setBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}

                @RequiresApi(Build.VERSION_CODES.M)
                @SuppressLint("SetTextI18n", "SimpleDateFormat", "ClickableViewAccessibility")
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    calendarRecyclerView = findViewById(R.id.calendar_recycler_view)
                    val sdf = SimpleDateFormat("EEEE dd-MMM-yyyy")
                    for (i in 0..6) {
                        calendar = GregorianCalendar()
                        calendar.add(Calendar.DATE, i)
                        val day: String = sdf.format(calendar.time)
                        val formatter4 = SimpleDateFormat("EEEE dd-MMM-yyyy")
                        val date = formatter4.parse(day)
                        arrayList.add(date!!)
                    }
                    val layoutManager =
                        LinearLayoutManager(this@MapActivity, LinearLayoutManager.HORIZONTAL, false)
                    calendarRecyclerView!!.layoutManager = layoutManager
                    val calendarAdapter = CalendarAdapter(this@MapActivity, arrayList, currentDate)
                    calendarRecyclerView!!.adapter = calendarAdapter
                    calendarAdapter.setOnItemClickListener(object :
                        CalendarAdapter.OnItemClickListener {
                        override fun onItemClick() {}
                    })
                    setUpCalendar()
                    mainActBottomSheetBehavior!!.isHideable = true
                    mainActBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                    mapFragment!!.getMapAsync {
                        mMap = it
                        mMap.addMarker(MarkerOptions().position(pickupLatLng!!).title(pickupValue.toString()))
                        mMap.addMarker(
                            MarkerOptions().position(dropLatLng!!).title(dropofvalue.toString())
                        )
                        val urll = getDirectionURL(pickupLatLng!!, dropLatLng!!, apiKey!!)
                        getDistance()
                        GetDirection(urll).execute()
                        if (urll != null) {
                            mMap.clear()
                            polylineAnimator.cancel()
                        }
                        mMap.addMarker(
                            MarkerOptions().position(pickupLatLng!!).title(pickupValue.toString()).icon(bitmapDescriptorFromVector(this@MapActivity,R.drawable.pickup_location_24dp))
                        )
                        mMap.addMarker(
                            MarkerOptions().position(dropLatLng!!).title(dropofvalue.toString()).icon(bitmapDescriptorFromVector(this@MapActivity,R.drawable.location_on_black))
                        )
                    }
                    val timePicker1 = bottomSheet.findViewById<TimePicker>(R.id.timePicker1)
                    confirmDetailsButton = bottomSheet.findViewById(R.id.confirm_details_button)
                    confirmDetailsButton!!.setOnClickListener { /*if (behavior1.state == BottomSheetBehavior.STATE_COLLAPSED) {*/
                        pickupTimeTextt!!.visibility = View.VISIBLE
                        endDestinationArrivalTime!!.visibility = View.VISIBLE
                        val valueInInt = (distanceFromSourceToDestination!!.toDouble() / 1000)
                        distValue = valueInInt
                        functions = Firebase.functions("asia-south1")
                        addNumbers(distValue)
                        selectedYearForRide = selectedYear.toString()
                        selectedMonthForRide = selectedMonth.toString()
                        selectedDayForRide = selectedDay.toString()
                        selectedHourForRide = timePicker1.hour.toString()
                        selectedMinuteForRide = timePicker1.minute.toString()
                        if (timePicker1.hour < 12) {
                            selectedAmOrPmForRide = "AM"
                        } else if (timePicker1.hour <= 12) {
                            selectedAmOrPmForRide = "PM"
                        }
//                        if(selectedDayForRide != null && selectedMonthForRide != null && selectedYearForRide != null && selectedHourForRide != null && selectedAmOrPmForRide != null && selectedMinuteForRide != null){
                        val selectedHourForRideInInt = selectedHourForRide!!.toInt()
                        if (selectedDayForRide!!.toInt() in 1..9) {
                            selectedDayForRide = "0$selectedDayForRide"
                        }
                        if (selectedHourForRideInInt in 0..9) {
                            selectedHourForRide = "0$selectedHourForRide"
                        }
                        if (selectedMinuteForRide!!.toInt() in 0..9) {
                            selectedMinuteForRide = "0$selectedMinuteForRide"
                        }
                        if (selectedHourForRideInInt >= 12) {
                            val timeIn12hr = selectedHourForRideInInt % 12
                            if (timeIn12hr == 0) {
                                pickupTimeTextt!!.text = "12:$selectedMinuteForRide PM"
                            } else if (timeIn12hr in 1..9) {
                                pickupTimeTextt!!.text =
                                    "0$timeIn12hr:${selectedMinuteForRide} PM"
                            } else {
                                pickupTimeTextt!!.text =
                                    "$timeIn12hr:${selectedMinuteForRide} PM"
                            }
                        } else if (selectedHourForRideInInt < 12) {
                            pickupTimeTextt!!.text =
                                "$selectedHourForRide:${selectedMinuteForRide} AM"
                        }
                        val selectedTimeForRide = "$selectedHourForRide:$selectedMinuteForRide"
                        val minsToAdd = (durationInSeconds!!.toInt() / 60) + 1
                        val date = Date()
                        date.time =
                            ((selectedTimeForRide.split(":")[0].toInt() * 60 + selectedTimeForRide.split(
                                ":"
                            )[1]
                                .toInt() + date.timezoneOffset) * 60000).toLong()
                        date.time = date.time + minsToAdd * 60000

                        var endHourOfRide = date.hours.toString()
                        var endMinuteOfRide = date.minutes.toString()
                        if (date.minutes in 0..9) {
                            endMinuteOfRide = "0$endMinuteOfRide"
                        }
                        if (date.hours in 0..9) {
                            endHourOfRide = "0" + date.hours
                        }
                        if (date.hours >= 12) {
                            val timeIn12HrFormat = date.hours % 12
                            if (timeIn12HrFormat == 0) {
                                endDestinationArrivalTime!!.text = "12:$endMinuteOfRide PM"
                            } else if (timeIn12HrFormat in 1..9) {
                                endDestinationArrivalTime!!.text =
                                    "0$timeIn12HrFormat:$endMinuteOfRide PM"
                            } else {
                                endDestinationArrivalTime!!.text =
                                    "$timeIn12HrFormat:$endMinuteOfRide PM"
                            }
                        } else if (date.hours < 12) {
                            endDestinationArrivalTime!!.text =
                                "$endHourOfRide:$endMinuteOfRide AM"
                        }

                        val rideDateFromArrayList = arrayList[arrayListPositionClickedd]
                        val monthNumberr = SimpleDateFormat(
                            "MM",
                            Locale.getDefault()
                        ).format(rideDateFromArrayList)// 06
                        val dayy = SimpleDateFormat(
                            "dd",
                            Locale.getDefault()
                        ).format(rideDateFromArrayList)
                        val yearr = SimpleDateFormat("yyyy", Locale.getDefault()).format(
                            rideDateFromArrayList
                        )
                        val selectedDateForRideInString =
                            "${dayy}-${monthNumberr}-${yearr} ${selectedHourForRide}:${selectedMinuteForRide}"
                        val selectedDateForRideInEpochh =
                            calculationUtilClass.convertDateToEpochConverter(
                                selectedDateForRideInString
                            )
                        startTimeForDirectionUrl = selectedDateForRideInEpochh.toString()

                        // var selectedDateForRideInEpochh = "1659605004000"   for testing
                        var existsForendTime = false
                        var exists = false
                        for (i in 0 until upcomingTripList.size){
                            if(selectedDateForRideInEpochh > upcomingTripList[i].startTime.toLong()  && selectedDateForRideInEpochh < (upcomingTripList[i].endTime.toLong()+900000)){
                                existsForendTime = true
                            }
                        }
                        for (i in 0 until upcomingTripList.size) {
                            if (upcomingTripList[i].startTime == selectedDateForRideInEpochh.toString()) {
                                exists = true
                            }
                        }
                        if (exists) {

                            val alreadyRideBookedDialog = AlreadyRideBookedDialog(this@MapActivity)
                            //   var mainbsbehavior = BottomSheetBehavior.from(ma)
                            alreadyRideBookedDialog.setCancelable(false)
                            alreadyRideBookedDialog.show()
                            val gotItButtonOnAlreadyRideBooked =
                                alreadyRideBookedDialog.findViewById<AppCompatButton>(R.id.gotItButton_onalreadyRideBooked)
                            gotItButtonOnAlreadyRideBooked.setOnClickListener {
                                alreadyRideBookedDialog.dismiss()
                            }
                        } else {
                            if (existsForendTime) {
                                val alreadyRideBookedDialog =
                                    AlreadyRideBookedDialog(this@MapActivity)
                                //   var mainbsbehavior = BottomSheetBehavior.from(ma)
                                alreadyRideBookedDialog.setCancelable(false)
                                alreadyRideBookedDialog.show()
                                val gotItButtonOnAlreadyRideBooked =
                                    alreadyRideBookedDialog.findViewById<AppCompatButton>(R.id.gotItButton_onalreadyRideBooked)
                                gotItButtonOnAlreadyRideBooked.setOnClickListener {
                                    alreadyRideBookedDialog.dismiss()
                                }
                            } else if(!existsForendTime){
                                TimeAndDatePickerBottomSheetBehavior!!.isHideable = true
                                TimeAndDatePickerBottomSheetBehavior!!.state =
                                    BottomSheetBehavior.STATE_HIDDEN
                                handler.postDelayed({
                                    rideDetailsBottomSheetBehavior!!.isHideable = false
                                    rideDetailsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                                }, 600)
                            }
                        }
                    }
                }

                private fun setUpCalendar() {
                    setCurrentTimeOnView()
                    calendarRecyclerView!!.addOnItemTouchListener(
                        RecyclerItemClickListener(
                            this@MapActivity,
                            object : RecyclerItemClickListener.OnItemClickListener {
                                override fun onItemClick(v: View?, position: Int) {
                                    if (position == 0) {
                                        setCurrentTimeOnView()
                                        arrayListPositionClicked = 0
                                        arrayListPositionClickedd = 0
                                    } else {
                                        arrayListPositionClicked = position
                                        arrayListPositionClickedd = position
                                        timePicker =
                                            findViewById<View>(R.id.timePicker1) as TimePicker
                                        timePicker!!.descendantFocusability =
                                            TimePicker.FOCUS_BLOCK_DESCENDANTS
                                        val c = Calendar.getInstance()
                                        hour = c[Calendar.HOUR_OF_DAY] + 1
                                        minute = c[Calendar.MINUTE]
                                        timePicker!!.currentHour = hour
                                        timePicker!!.currentMinute = minute
                                        timePicker!!.setOnTimeChangedListener(object : TimePicker.OnTimeChangedListener {
                                            override fun onTimeChanged(
                                                view: TimePicker?,
                                                hourOfDay: Int,
                                                minutee: Int
                                            ) {
                                                val datetime = Calendar.getInstance()
                                                datetime[Calendar.HOUR_OF_DAY] = hourOfDay
                                                datetime[Calendar.MINUTE] = minutee

                                            }
                                        })
                                    }
                                }
                            })
                    )
                }

                fun setCurrentTimeOnView() {
                    // textViewTime = findViewById<View>(R.id.txtTime) as TextView
                    timePicker = findViewById<View>(R.id.timePicker1) as TimePicker

                    timePicker!!.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS
                    val c = Calendar.getInstance()
                    hour = c[Calendar.HOUR_OF_DAY] + 1
                    minute = c[Calendar.MINUTE]
                    timePicker!!.currentHour = hour
                    timePicker!!.currentMinute = minute
                    timePicker!!.setOnTimeChangedListener { _, hourOfDay, minutes ->
                        val datetime = Calendar.getInstance()
                        datetime[Calendar.HOUR_OF_DAY] = hourOfDay
                        datetime[Calendar.MINUTE] = minutes
                        if (datetime.timeInMillis >= (c.timeInMillis + 3600000)) {
                            //it's after current
                        } else {
                            //it's before current'

                            timePicker!!.currentHour = hour
                            timePicker!!.currentMinute = minute
                        }
                    }
                }
            })
        }
    }

    override fun setJsonBody(jsonResponse: String) {
        TODO("Not yet implemented")
    }

    override fun showPath(latLngList: List<LatLng>) {
        val builder = LatLngBounds.Builder()
        for (latLng in latLngList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        val cameraPosition = CameraPosition.builder()
            .target(bounds.center)
            .zoom(12f)
            .bearing(90f)
            .tilt(60f)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        val polylineOptions = PolylineOptions()
        polylineOptions.color(applicationContext.resources.getColor(R.color.teal_700))
        polylineOptions.width(12f)
        polylineOptions.addAll(latLngList)
        greyPolyLine = mMap.addPolyline(polylineOptions)


        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.width(12f)
        blackPolylineOptions.color(applicationContext.resources.getColor(R.color.teal_200))
        blackPolyline = mMap.addPolyline(blackPolylineOptions)

        originMarker = addOriginDestinationMarkerAndGet(latLngList[0])
        originMarker?.setAnchor(0.5f, 0.5f)
        destinationMarker = addOriginDestinationMarkerAndGet(latLngList[latLngList.size - 1])
        destinationMarker?.setAnchor(0.5f, 0.5f)

        val polylineAnimator = AnimationUtils.polyLineAnimator()
        polylineAnimator.addUpdateListener { valueAnimator ->
            val percentValue = (valueAnimator.animatedValue as Int)
            val index = (greyPolyLine?.points!!.size * (percentValue / 100.0f)).toInt()
            blackPolyline?.points = greyPolyLine?.points!!.subList(0, index)
        }
        polylineAnimator.start()
    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getDestinationBitmap())
        return mMap.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))!!
    }

    private fun getDestinationBitmap(): Bitmap {
        val height = 30
        val width = 30
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.TRANSPARENT
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    @SuppressLint("LongLogTag")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val myLocation = LatLng(19.097403, 72.874245)
        //val bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_airplanemode_active_24)
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getCarBitmap(this))
        mMap.addMarker(
            MarkerOptions().position(myLocation).icon(bitmapDescriptor)
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
    }

    private fun getCarBitmap(context: Context): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.airportloc)
        return Bitmap.createScaledBitmap(bitmap, 350, 150, false)
    }
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    private fun shineStart(){
        var animation = TranslateAnimation(0F, (thirdCardView!!.width+shineEffectForMBS!!.width).toFloat(),0F, 0F)
        animation.duration = 1500
        animation.fillAfter = false
        animation.interpolator = AccelerateDecelerateInterpolator()
        shineEffectForMBS!!.startAnimation(animation)
    }
}

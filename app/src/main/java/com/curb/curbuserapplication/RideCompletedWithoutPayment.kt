package com.curb.curbuserapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RideCompletedWithoutPayment : AppCompatActivity(), OnMapReadyCallback,GeoTask.Geo {
    private var mapFragment : SupportMapFragment? = null
    private lateinit var mMap: GoogleMap
    var pickupLatLng : LatLng? = null
    var dropLatLng : LatLng? = null
    var startLat : String? = null
    var startLng : String? = null
    var endLat : String? = null
    var endLng : String? = null
    var apiKey :String? = null
    var startLocationaddress : String? = null
    var endLocationAddress:String? = null
    var carNumber:String? = null
    var rideFare:String? = null
    var startLocationInLayout:TextInputLayout? = null
    var endLocationInLayout:TextInputLayout? = null
    var carNumberInLayout:TextView? =  null
    var rideFareInLayout:TextView? =  null
    var payInCashAfterRide:RelativeLayout? = null
    var payNowAfterRide:RelativeLayout? = null
    var rideTime:String? = null
    var rideDistance:String? = null
    var userId:String?= null
    var db = FirebaseFirestore.getInstance()
    var rideStartTime:String? = null
    var tripId:String? = null
    var rideEndTimeOnRideCompletedView : TextView? = null
    var carbonEmissionSavedInView : TextView? = null
    var driverNameInView : TextView? = null
    var carbonEmissionSaved : String? = null
    var driverName : String? = null
    var DistanceCoveredText : TextView? = null
    var driverContact : String? = null
    var currentUserName : String? = null
    var currentUserEmail  : String? = null
    var currentUserPhoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_completed_without_payment)
        ViewUtils.enableTransparentStatusBar(window)
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        apiKey = value.toString()
        startLocationInLayout = findViewById(R.id.pickUpTextView_ride_completed)
        endLocationInLayout = findViewById(R.id.dropTextView_ride_completed)
        carNumberInLayout = findViewById(R.id.car_number_in_layout)
        DistanceCoveredText = findViewById(R.id.distance_covered_text)
        rideFareInLayout = findViewById(R.id.total_price_of_the_ride_cost_after_ride)
        payInCashAfterRide = findViewById(R.id.pay_in_cash_after_ride)
        payNowAfterRide = findViewById(R.id.pay_now_button_after_ride)
        driverNameInView = findViewById(R.id.drivers_name)
        carbonEmissionSavedInView =
            findViewById(R.id.carbonEmissionSavedValue_rideCompletedWithoutPayment)
        rideEndTimeOnRideCompletedView = findViewById(R.id.ride_end_time_on_ride_completed)
        val intent = intent
        startLat = intent.getStringExtra("startLatitude")
        startLng = intent.getStringExtra("startLongitude")
        endLat = intent.getStringExtra("endLatitude")
        endLng = intent.getStringExtra("endLongitude")
        rideStartTime = intent.getStringExtra("startTime")
        driverName = intent.getStringExtra("driverName")
        driverContact = intent.getStringExtra("driverContact")
        carbonEmissionSaved = intent.getStringExtra("carbonEmissionFunction")
        startLocationaddress = intent.getStringExtra("StartLocation")
        endLocationAddress = intent.getStringExtra("endLocation")
        carNumber = intent.getStringExtra("carNumber")
        rideFare = intent.getStringExtra("RideFare")
        rideTime = intent.getStringExtra("RideTime")
        rideDistance = intent.getStringExtra("RideDistance")
//        currentUserName = intent.getStringExtra("userName")
//        currentUserEmail = intent.getStringExtra("userEmail")
//        currentUserPhoneNumber = intent.getStringExtra("userPhoneNumber")
        userId = intent.getStringExtra("userId")
        tripId = intent.getStringExtra("tripId")
        var rideEndTimeInString = intent.getStringExtra("rideEndTimeInDateFormat")

        rideEndTimeOnRideCompletedView!!.text = rideEndTimeInString.toString()

        pickupLatLng = LatLng(startLat!!.toDouble(), startLng!!.toDouble())
        dropLatLng = LatLng(endLat!!.toDouble(), endLng!!.toDouble())
        startLocationInLayout!!.editText!!.setText(startLocationaddress)
        endLocationInLayout!!.editText!!.setText(endLocationAddress)
        carNumberInLayout!!.text = carNumber.toString()
        DistanceCoveredText!!.text = rideDistance.toString()
        carbonEmissionSavedInView!!.text = carbonEmissionSaved.toString() + " grams"
        //  driverNameInView!!.text = driverContact.toString()
        if (TextUtils.isDigitsOnly(driverContact)) {
            driverNameInView!!.text = driverName.toString()
        } else {
            driverNameInView!!.text = driverContact.toString()
        }
//        carbonEmissionSavedLayout!!.setRenderEffect(RenderEffect.createBlurEffect(30f,30f,Shader.TileMode.CLAMP))
        rideFareInLayout!!.text = rideFare.toString()
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)
        mapFragment!!.getMapAsync {
            mMap = it
            mMap.addMarker(MarkerOptions().position(pickupLatLng!!))!!.setIcon(bitmapDescriptorFromVector(this,R.drawable.pickup_location_24dp))
            mMap.addMarker(MarkerOptions().position(dropLatLng!!))!!.setIcon(bitmapDescriptorFromVector(this,R.drawable.location_on_black))
            val urll = getDirectionURL(pickupLatLng!!, dropLatLng!!, apiKey!!)
            //getDistance()
            GetDirection(urll).execute()
        }
        payInCashAfterRide!!.setOnClickListener {
            val intent = Intent(this, PaymentSuccessfullActivityMain::class.java)
            intent.putExtra("RideTime", rideTime.toString())
            intent.putExtra("RideDistance", rideDistance.toString())
            intent.putExtra("RidePrice", rideFare.toString())
            intent.putExtra("driverName", driverName.toString())
            intent.putExtra("driverContact", driverContact.toString())
            intent.putExtra("startLatitude", startLat.toString())
            intent.putExtra("startLongitude", startLng.toString())
            intent.putExtra("endLatitude", endLat.toString())
            intent.putExtra("endLongitude", endLng.toString())
            intent.putExtra("userId", userId.toString())
            intent.putExtra("tripId", tripId.toString())
            startActivity(intent)
        }
        payNowAfterRide!!.setOnClickListener {
            val intent = Intent(this, RazorPayment::class.java)
            val docRef =
                db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        currentUserName = document.getString("displayName").toString()
                        currentUserEmail = document.getString("email").toString()

                        val paymentData =
                            PaymentData(
                                "INR",
                                rideFare!!,
                                "Ride Fare",
                                tripId.toString(),
                                userId.toString(),
                                currentUserName.toString(),
                                currentUserPhoneNumber.toString(),
                                currentUserEmail.toString()
                            )
                        intent.putExtra("paymentData", paymentData)
                        intent.putExtra("userId", userId)
                        intent.putExtra("tripId", tripId.toString())
                        intent.putExtra("rideStatusF", "Completed")
                        intent.putExtra("RideTime", rideTime.toString())
                        intent.putExtra("RideDistance", rideDistance.toString())
                        intent.putExtra("RidePrice", rideFare.toString())
                        intent.putExtra("startLatitude", startLat.toString())
                        intent.putExtra("startLongitude", startLng.toString())
                        intent.putExtra("endLatitude", endLat.toString())
                        intent.putExtra("endLongitude", endLng.toString())
                        intent.putExtra("driverContact", driverContact.toString())
                        intent.putExtra("driverName", driverNameInView!!.text.toString())
                        startActivity(intent)
                    }
                }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun setDouble(min: String?) {
        TODO("Not yet implemented")
    }

    override fun setJsonBody(jsonResponse: String) {
        TODO("Not yet implemented")
    }

    override fun showPath(latLngList: List<LatLng>) {
        val builder = LatLngBounds.Builder()
        var greyPolyLine: Polyline? = null
        var blackPolyline: Polyline? = null
        var destinationMarker: Marker? = null
        var originMarker: Marker? = null
        for (latLng in latLngList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        val cameraPosition = CameraPosition.builder()
            .target(bounds.center)
            .zoom(13f)
            //.bearing(90f)
            .tilt(60f)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        val polylineOptions = PolylineOptions()
        polylineOptions.color(applicationContext.resources.getColor(R.color.teal_700))
        polylineOptions.width(10f)
        polylineOptions.addAll(latLngList)
        greyPolyLine = mMap.addPolyline(polylineOptions)

        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.width(10f)
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
    private fun getDirectionURL(origin: LatLng, dest: LatLng, secret: String) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }
    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data,MapData::class.java)
                val path =  ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }
        override fun onPostExecute(result: List<List<LatLng>>) {
            val colorPrimary = ContextCompat.getColor(applicationContext, R.color.teal_700)
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.geodesic(true)
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
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }
    fun getDestinationBitmap(): Bitmap {
        val height = 20
        val width = 20
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }
    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getDestinationBitmap())
        return mMap.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))!!
    }
    private fun animateCamera(latlng: LatLng?){
        val cameraPosition = CameraPosition.builder().target(latlng!!).zoom(17f).bearing(90f).tilt(30f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
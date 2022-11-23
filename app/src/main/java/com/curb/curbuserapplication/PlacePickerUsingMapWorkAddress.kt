package com.curb.curbuserapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.provider.Settings
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException
import java.lang.IndexOutOfBoundsException
import java.util.*
import java.util.jar.Manifest

class PlacePickerUsingMapWorkAddress : AppCompatActivity(),LocationListener,OnMapReadyCallback,GoogleMap.OnCameraMoveListener,GoogleMap.OnCameraMoveStartedListener,GoogleMap.OnCameraIdleListener {

    var getCurrentAddressAfterMarkerMove: TextView? = null
    private var mMap : GoogleMap? = null
    lateinit var mapView : MapView
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    private var onCameraIdleAddress :List<Address>? = null
    private var currentLocationF :List<Address>? = null
    private var currentLocationLatLng : LatLng? = null
    private val DEFAULT_ZOOM = 18f
    private var backButtonOnChooseWorkAddress : ImageView? = null
    private var getLocationAddressButton : AppCompatButton?= null
    private var addWorkAddressButton : AppCompatButton? = null
    private var enableLocationButton : Button? = null
    private var enabkeLocationText : TextView? = null
    private var bottomLayout : ConstraintLayout? = null
    private var mainLayoutWorkAddress : ConstraintLayout? = null
    private var userId = ""
    private var fusedClientLocationProvider: FusedLocationProviderClient? = null
    private var networkConnectionForHomeAddress : IsInternetAvailable? = null
    private var enableLocationUI : View ? = null

    override fun onMapReady(googleMap: GoogleMap) {
        mapView.onResume()
        mMap = googleMap
        askGalleryPermissionLocation()
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return
        }
        mMap!!.isMyLocationEnabled = true
        mMap!!.setOnCameraIdleListener(this)
        mMap!!.setOnCameraMoveStartedListener(this)
        mMap!!.setOnCameraIdleListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_picker_using_map_work_address)
        ViewUtils.enableTransparentStatusBar(window)
        mapView = findViewById(R.id.map11work)
        getCurrentAddressAfterMarkerMove = findViewById(R.id.location_choose_place_work)
        backButtonOnChooseWorkAddress = findViewById(R.id.back_button_on_choose_work_address)
        addWorkAddressButton = findViewById(R.id.add_work_address_button)
        getLocationAddressButton = findViewById(R.id.get_location_address_button_work)
        enableLocationUI = findViewById(R.id.location_not_enable_ui_on_select_workAddress)
        enableLocationButton = enableLocationUI!!.findViewById(R.id.Enable_location_button)
        mainLayoutWorkAddress = findViewById(R.id.main_layout_work_address)
        bottomLayout = findViewById(R.id.bottom_card_layout_work)
        enabkeLocationText = enableLocationUI!!.findViewById(R.id.enable_location_text_body)
        networkConnectionForHomeAddress = IsInternetAvailable(applicationContext)
        // useCurrentLocationButton = findViewById(R.id.use_current_location_button)
        askGalleryPermissionLocation()
        userId = intent.getStringExtra("userId").toString()
        backButtonOnChooseWorkAddress!!.setOnClickListener { finish() }
        addWorkAddressButton!!.setOnClickListener {
            networkConnectionForHomeAddress!!.observe(
                this,
                androidx.lifecycle.Observer { isConnected ->
                    if (isConnected) {
                        val workAddress = getCurrentAddressAfterMarkerMove!!.text.toString()
                        val db = FirebaseFirestore.getInstance()
                        val trips = hashMapOf("workLocation" to workAddress)
                        db.collection("users")
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    //  val tripId = db.collection("tripDetails").document().id

                                    db.collection("users").document(document.id)
                                        .set(trips, SetOptions.merge())
                                    Toast.makeText(
                                        applicationContext,
                                        "Record Updated for work Address",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    } else {
                        val snackBar = Snackbar.make(mainLayoutWorkAddress!!, "Check your internet connection to save your home Address", Snackbar.LENGTH_INDEFINITE)
                        snackBar.setActionTextColor(Color.CYAN)
                        val snackbarView: View = snackBar.view
                        val sbView: View = snackbarView
                        snackBar.setTextColor(Color.WHITE)
                        sbView.setBackgroundColor(Color.DKGRAY);
                        snackBar.setAction("OK") { // Call your action method here
                            snackBar.dismiss()
                        }
                        snackBar.show()

                    }
                })
        }
        var mapViewBundle : Bundle? = null
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        this.onBackPressedDispatcher.addCallback(this,callback)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        askGalleryPermissionLocation()
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }
    private fun askGalleryPermissionLocation(){
        Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION)
            .withListener(object : MultiplePermissionsListener{
                override fun onPermissionsChecked(multiplePermissionReport: MultiplePermissionsReport?) {
                    if(multiplePermissionReport!!.areAllPermissionsGranted()){
                        getCurrentLocation()
                        getLocationAddressButton!!.visibility = View.VISIBLE
                        enableLocationUI!!.visibility = View.GONE
                        bottomLayout!!.visibility = View.VISIBLE
                        getLocationAddressButton!!.setOnClickListener {
                            var addresses : List<Address>? = null
                            val geoCoder = Geocoder(this@PlacePickerUsingMapWorkAddress, Locale.getDefault())
                            try{
                                addresses = geoCoder.getFromLocation(mMap!!.cameraPosition.target.latitude,mMap!!.cameraPosition.target.longitude,1)
                                setAddress(addresses!![0])
                                onCameraIdleAddress = addresses

                            }catch (e : IndexOutOfBoundsException){
                                e.printStackTrace()
                            }catch (e : IOException){
                                e.printStackTrace()
                            }
                        }
                    }
                    if(multiplePermissionReport.isAnyPermissionPermanentlyDenied){
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(list: MutableList<PermissionRequest>?, permissionToken: PermissionToken?) {
                    permissionToken!!.continuePermissionRequest()
                    enableLocationUI!!.visibility = View.VISIBLE
                    bottomLayout!!.visibility = View.GONE
                    enableLocationUI!!.setOnTouchListener { v, event -> true }
                    enabkeLocationText!!.text = "Enable location service to save your Work Address"
                    enableLocationButton!!.setOnClickListener { askGalleryPermissionLocation() }
                }

            }).withErrorListener { Toast.makeText(this,"Some issue occurred",Toast.LENGTH_LONG).show() }
            .onSameThread().check()
    }
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedClientLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        try{
            val location = fusedClientLocationProvider!!.lastLocation
            location.addOnCompleteListener(object : OnCompleteListener<Location>{
                override fun onComplete(p0: Task<Location>) {
                    if (p0.isSuccessful) {
                        val currentLocation = p0.result as Location?
                        if (currentLocation != null) {
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), DEFAULT_ZOOM))
                            val geoCoder = Geocoder(this@PlacePickerUsingMapWorkAddress, Locale.getDefault())
                            var addresses: List<Address>? = null
                            try {
                                addresses = geoCoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
                                currentLocationF = addresses
                                currentLocationLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        }
                    } else {
                        askGalleryPermissionLocation()
                        Toast.makeText(
                            this@PlacePickerUsingMapWorkAddress,
                            "Current location not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
        }catch (se:Exception){
            Log.e("TAG","Security Exception")
        }
    }
    @SuppressLint("PrivateResource")
    private fun showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        val builder = AlertDialog.Builder(this, R.style.MyDialogStyle)

        // below line is the title for our alert dialog.
        builder.setTitle("Need Permissions")
        builder.setCancelable(false)
        // below line is our message for our dialog
        builder.setMessage("The app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel()
            // below is the intent from which we are redirecting our user.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 101)
        }
        // below line is used to display our dialog
        builder.show()
    }
    override fun onLocationChanged(location: Location) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        setAddress(addresses!![0])

        getCurrentAddressAfterMarkerMove!!.addTextChangedListener  (object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getLocationAddressButton!!.visibility = View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        //useCurrentLocationButton()
    }
    @SuppressLint("SetTextI18n")
    private fun setAddress(address: Address) {
        if (address != null) {
            if (address.getAddressLine(0) != null) {
                getCurrentAddressAfterMarkerMove!!.text = address.getAddressLine(0)
            }
            if (address.getAddressLine(1) != null) {
                getCurrentAddressAfterMarkerMove!!.text =
                    getCurrentAddressAfterMarkerMove!!.text.toString() + address.getAddressLine(1)
            }
//            useCurrentLocationButton()

        }
    }
    override fun onCameraMove() {
    }
    override fun onCameraMoveStarted(p0: Int) {
    }
    override fun onCameraIdle() {
        var addresses : List<Address>? = null
        val geoCoder = Geocoder(this, Locale.getDefault())
        try{
            addresses = geoCoder.getFromLocation(mMap!!.cameraPosition.target.latitude,mMap!!.cameraPosition.target.longitude,1)
            setAddress(addresses!![0])
            onCameraIdleAddress = addresses
            getCurrentAddressAfterMarkerMove!!.addTextChangedListener  (object :TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    getLocationAddressButton!!.visibility = View.GONE
                }
                override fun afterTextChanged(s: Editable?) {}
            })

        }catch (e : IndexOutOfBoundsException){
            e.printStackTrace()
        }catch (e : IOException){
            e.printStackTrace()
        }
    }


}
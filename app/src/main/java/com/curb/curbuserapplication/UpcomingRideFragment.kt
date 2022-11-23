package com.curb.curbuserapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class UpcomingRideFragment : Fragment() {
    var recyclerViewForCompleted : RecyclerView? = null
    var noCompletedRide : LinearLayout? = null
    var db = FirebaseFirestore.getInstance()
    private var tripDetailsList11 = ArrayList<TripDetailsClass>()
    val calculationUtilClass = CalculationUtilsClass()
    private var tripDetailsListRideHistory = ArrayList<TripDetailsClass>()
    var userForRideHistory : String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_upcoming_ride, container, false)
        recyclerViewForCompleted = view.findViewById(R.id.recycler_view_ride_history_upcoming)
        noCompletedRide = view.findViewById(R.id.no_ride_history_text_for_upcoming)
        userForRideHistory = FirebaseAuth.getInstance().currentUser!!.uid
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tripDetailsListRideHistory.clear()
        getDataForOngoingTrip()
    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser){
            tripDetailsListRideHistory.clear()
            getDataForOngoingTrip()
        }
    }

    private fun getDataForOngoingTrip() {
        db.collection("tripDetails")
            .whereEqualTo("userId", userForRideHistory)
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
                    val currentTineInMillis = calculationUtilClass.currentDateToEpochCoverter()
                    if (tripDetail1.startTime != "null") {
                        if (tripDetail1.startTime != "") {
                            if (tripDetail1.startTime.toLong() > currentTineInMillis!! && tripDetail1.statuso == "Schedule" || tripDetail1.startTime.toLong() > currentTineInMillis && tripDetail1.statuso == "Assign" || tripDetail1.startTime.toLong() > currentTineInMillis && tripDetail1.statuso == "Arrived") {
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
                            }
                        }
                    }
                }
                if(tripDetailsListRideHistory.isEmpty() || tripDetailsListRideHistory.size == 0){
                    noCompletedRide!!.visibility = View.VISIBLE


                }else if(tripDetailsListRideHistory.isNotEmpty() || tripDetailsListRideHistory.size > 0) {
                    noCompletedRide!!.visibility = View.GONE

                    tripDetailsListRideHistory.sort()
                    val adapter = AdapterForUpcomingRideHistory(tripDetailsListRideHistory, requireContext())
                    recyclerViewForCompleted!!.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    recyclerViewForCompleted!!.adapter = adapter
                }
                // textView_result2!!.text = tripDetailsList[0].toString()
            }
            .addOnFailureListener { exception ->

            }
    }
}
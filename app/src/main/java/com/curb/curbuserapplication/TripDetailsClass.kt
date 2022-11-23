package com.curb.curbuserapplication

import java.util.*

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class TripDetailsClass(
    var OTP :String,
    var PaymentId :String,
    val carId:String,
    var distance :String,
    val driverId :String,
    var endTime :String,
    var price :String,
    var ridersRating :String,
    var startTime:String,
    var startLocationAddress :String,
    var endLocationAddress :String,
    var statuso :String,
    val tripId :String,
    var tripType :String,
    val userId:String,
    var startLocationLat:String,
    var startLocationLng:String,
    var endLocationLat:String,
    var endLocationLng:String,
    var paymentStatus: String,
    var timeTakenForRide : String,
    var driverName : String,
    var driverContact : String,
    var carbonEmissionValue : String,
    var carName : String,
    var orderId:String,
    var paymentMethod:String

):Comparable<TripDetailsClass?> {
    constructor() : this("","","","","","","","","","","","","","","","","","","","","","","","","","","")
    override fun compareTo(other: TripDetailsClass?): Int {
        return startTime.compareTo(other!!.startTime)
    }

}
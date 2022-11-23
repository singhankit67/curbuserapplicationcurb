package com.curb.curbuserapplication

import android.annotation.SuppressLint
import java.math.RoundingMode
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class CalculationUtilsClass {

    //region variables
    var userOTP:String? = null
    var s = ""
    var ranNo = 0
    //endregion

    //region functionalities
    /**
     *  Takes input in double and returns the total co2 saved during the ride in terms of string
     */
    fun carbonEmissionCalculator(km: Double): String {
        //1 km of tigor ev saves 35g of co2 per km
        val co2EmissionSaved = km * 35
        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.UP
        return df.format(co2EmissionSaved).toString()
    }

    /**
     *  Takes start time of the upcoming ride and checks weather the current time
     *  is 15 minutes prior to the upcoming ride time and returns a boolean value.
     */
    fun check15minutesBeforeTheRide(timeOfrideInMillis:String):Boolean{
        val fifteenMinutesBeforeRideTime = timeOfrideInMillis.toLong() - 900000
        val currentTime = System.currentTimeMillis()
        if (currentTime >= fifteenMinutesBeforeRideTime && currentTime <= timeOfrideInMillis.toLong()){
            return true
        }
        return false
    }

    /**
     * takes the date in string format and converts it to milliseconds in long format
     */
    @SuppressLint("SimpleDateFormat")
    fun convertDateToEpochConverter(dateString : String): Long {
        val df = SimpleDateFormat("dd-M-yyyy h:mm")
        val date = df.parse(dateString)
        return date!!.time
    }

    /**
     * Takes the current date and converts it to epoch
     */
    fun currentDateToEpochCoverter(): Long? {
        @SuppressLint("SimpleDateFormat")
        val df = SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz")
        val dateInString = df.format(Calendar.getInstance().time)
        val date = df.parse(dateInString)
        val epoch = date?.time
        return epoch
    }

    /**
     * Takes the given date in epoch format and converts it to date
     */
    @SuppressLint("SimpleDateFormat")
    fun getDateFromEpoch(epoch: String): String? {
        val date = Date(epoch.toLong())
        val format: DateFormat = SimpleDateFormat("E, dd MMM hh:mm aa")
        format.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        return format.format(date)
    }

    /**
     * Everytime this function is called it generates a random otp
     */
    fun getOTP(len: Int): String {
        // Use for loop to iterate 4 times and generate random OTP
        for (i in 0 until len) {
            // Generate random digit within 0-9
            ranNo = Random().nextInt(9)
            s += ranNo.toString()
        }
        // Return the generated OTP
        return s
    }
    //endregion
}
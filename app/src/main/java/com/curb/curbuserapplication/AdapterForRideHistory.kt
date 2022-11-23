package com.curb.curbuserapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ColorSpace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterForRideHistory(val modelList : ArrayList<TripDetailsClass>, val context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.ride_history_single_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(modelList[position])
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(model: TripDetailsClass): Unit {
            var rideEndTimeInDateFormatForStart : Date? = null
            var rideEndTimeInDateFormatForEnd : Date? = null
            var pickupAddressrideHistory = itemView.findViewById<TextInputLayout>(R.id.pickUpTextView_main_ride_history)
            var dropOffAddressrideHistory = itemView.findViewById<TextInputLayout>(R.id.dropTextView_main_ride_history)
            var totalAmountOfTheRideHistory = itemView.findViewById<TextView>(R.id.price_ongoing_ride_history)
            var driverDetailsLayout = itemView.findViewById<LinearLayout>(R.id.driver_details)
            var driverNameRideHistory = itemView.findViewById<TextView>(R.id.drivers_name_ride_history)
            var pickupTimeRide  = itemView.findViewById<TextView>(R.id.pickup_time_for_cancelled)
            var dropOffTimeRide = itemView.findViewById<TextView>(R.id.dropoff_time_for_cancelled)
            var driverRatingByCustomerRideHistory = itemView.findViewById<TextView>(R.id.rider_rating_ride_history)
            var rideStartTimeInRideHistory = itemView.findViewById<TextView>(R.id.trip_time_in_history)
            var driverNameRatingLayout = itemView.findViewById<LinearLayout>(R.id.driver_name_rating_layout)
            var rideStatusVieww = itemView.findViewById<TextView>(R.id.completed_view_ui)
            var calculationUtilClass = CalculationUtilsClass()
            var getRideTimeForStart = calculationUtilClass.getDateFromEpoch(model.startTime)
            var getRideTimeForEnd = calculationUtilClass.getDateFromEpoch(model.endTime)
            driverDetailsLayout.visibility = View.VISIBLE
            pickupAddressrideHistory.editText!!.setText(model.startLocationAddress)
            dropOffAddressrideHistory.editText!!.setText(model.endLocationAddress)
            totalAmountOfTheRideHistory.text = model.price
            driverNameRideHistory.text = model.driverContact
            driverRatingByCustomerRideHistory.text = model.ridersRating
            var startTimeInHist  = calculationUtilClass.getDateFromEpoch(model.startTime).toString()
            rideStartTimeInRideHistory.text = startTimeInHist

            if(model.statuso == "Cancelled"){
                driverNameRatingLayout.visibility = View.GONE
                rideStatusVieww.setBackgroundResource(R.drawable.circular_cancel_button_red)
                rideStatusVieww.text = "CANCELLED"
                rideStatusVieww.setTextColor(context.resources.getColor(R.color.cancelButtonColor))
            }
//            if(model.statuso == "Schedule"|| model.statuso == "Arrived" || model.statuso == "Assigned"){
//                rideStatusVieww.setBackgroundResource(R.drawable.otp_field_ui)
//                rideStatusVieww.text = "UPCOMING"
//                rideStatusVieww.setTextColor(context.resources.getColor(R.color.lightGrey))
//            }

        }
    }
}
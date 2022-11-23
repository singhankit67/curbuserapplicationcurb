package com.curb.curbuserapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class AdapterForUpcomingRideHistory(val modelList : ArrayList<TripDetailsClass>, val context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mListener: AdapterForUpcomingRideHistory.OnItemClickListener? = null
    var permissionUtlis = PermissionUtils
    var ongoingTrip = TripDetailsClass()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.upcoming_ride_single_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(modelList[position])
    }

    override fun getItemCount(): Int {
        return modelList.size
    }
    interface OnItemClickListener {
        fun onItemClick(/*position: Int*/)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        var startRideButton: TextView? = null
        var whiteShine_effect : ImageView ?= null
        fun bind(model: TripDetailsClass): Unit {
            var pickupAddressrideHistory = itemView.findViewById<TextInputLayout>(R.id.pickUpTextView_main_ride_history)
            var dropOffAddressrideHistory = itemView.findViewById<TextInputLayout>(R.id.dropTextView_main_ride_history)
            var totalAmountOfTheRideHistory = itemView.findViewById<TextView>(R.id.price_ongoing_ride_history)
            var driverDetailsLayout = itemView.findViewById<LinearLayout>(R.id.driver_details)
            var driverNameRideHistory = itemView.findViewById<TextView>(R.id.drivers_name_ride_history)
            var rideStartTimeInRideHistory = itemView.findViewById<TextView>(R.id.trip_time_in_history)
            var car_number_inAdapter = itemView.findViewById<TextView>(R.id.car_numberr)
            var cancel_upcoming_ridebutton = itemView.findViewById<TextView>(R.id.cancel_upcoming_view_ui)
            var calculationUtilClass = CalculationUtilsClass()
            var callDriverButton = itemView.findViewById<TextView>(R.id.call_button_on_upcoming_ride)
            startRideButton = itemView.findViewById(R.id.start_ride_button_upcoming)
            whiteShine_effect = itemView.findViewById<ImageView>(R.id.shine_effect_in_main_upcoming)
            var upcomingViewUI = itemView.findViewById<TextView>(R.id.upcomingg_view_ui)
            driverDetailsLayout.visibility = View.VISIBLE
            pickupAddressrideHistory.editText!!.setText(model.startLocationAddress)
            dropOffAddressrideHistory.editText!!.setText(model.endLocationAddress)
            val cancelUpcomingRideDialogForRideHistory = UpcomingRideCancelDialog(context)
            val contactUsDialog = ContactUsDialog(context)
            val otpVerificationDialog = OtpVerificationDialog(context)
            val driverContactToCall = model.driverName
            callDriverButton.setOnClickListener {
                if (permissionUtlis.isPermissionGranted(context)) {
                    //   var mainbsbehavior = BottomSheetBehavior.from(ma)
                    contactUsDialog.setCancelable(false)
                    contactUsDialog.show()
                    val cancelButton =
                        contactUsDialog.findViewById<AppCompatButton>(R.id.cancel_button_incontactus_dialog)
                    val phoneNumberToCall =
                        contactUsDialog.findViewById<TextView>(R.id.contact_no_toCall)
                    phoneNumberToCall.text = driverContactToCall
                    cancelButton.setOnClickListener {
                        contactUsDialog.dismiss()
                    }
                    phoneNumberToCall.setOnClickListener {
                        try {
                            val driverNumberToCallTo = "tel:$driverContactToCall"
                            val intent = Intent(Intent.ACTION_CALL)
                            intent.data = Uri.parse(driverNumberToCallTo)
                            context.startActivity(intent)
                        } catch (e: Exception) {

                        }
                    }
                } else if (!permissionUtlis.isPermissionGranted(context)) {

                    val snackBar = Snackbar.make(itemView,
                        "Curb needs permission to access your phone To make calls ",
                        Snackbar.LENGTH_INDEFINITE
                    )
                    snackBar.setActionTextColor(Color.WHITE)
                    val snackbarView: View = snackBar.view
                    val sbView: View = snackbarView
                    snackBar.setTextColor(Color.WHITE)
                    sbView.setBackgroundColor(Color.DKGRAY)
                    snackBar.setAction("OK") { // Call your action method here
                        snackBar.dismiss()
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.CALL_PHONE),
                            1
                        )
                    }
                    snackBar.show()
                }
            }
            cancel_upcoming_ridebutton.setOnClickListener {
                cancelUpcomingRideDialogForRideHistory.setCancelable(false)
                cancelUpcomingRideDialogForRideHistory.show()
                val cancel_trip_button = cancelUpcomingRideDialogForRideHistory.findViewById<AppCompatButton>(R.id.cancel_upcoming_ride_button_for_function)
                val cancel_dialog_button = cancelUpcomingRideDialogForRideHistory.findViewById<ImageButton>(R.id.cancel_upcoming_process)
                cancel_dialog_button.setOnClickListener { cancelUpcomingRideDialogForRideHistory.dismiss() }
                cancel_trip_button.setOnClickListener {
                    try {
                        updateDeleteDataRecordInDb()
                        modelList.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                        notifyItemRangeChanged(adapterPosition,modelList.size)
                        notifyDataSetChanged()
                    }
                    catch (e:Exception){e.printStackTrace()
                    }
                    Handler().postDelayed({ cancelUpcomingRideDialogForRideHistory.dismiss() }, 200)
                }
            }
            //   var mainbsbehavior = BottomSheetBehavior.from(ma)
            startRideButton!!.setOnClickListener {
                otpVerificationDialog.setCancelable(false)
                otpVerificationDialog.show()
                val bundle = Bundle()
                bundle.putString("OtpForRide", model.OTP)
                val cancelButton = otpVerificationDialog.findViewById<ImageButton>(R.id.cancel_otp_process)
                cancelButton.setOnClickListener {
                    otpVerificationDialog.dismiss()
                }
                otpVerificationDialog.setDialogResult(object :
                    OtpVerificationDialog.OnMyDialogResult {
                    override fun finish(result: String?) {
                        Handler().postDelayed({
//                            mainActBottomSheetBehavior!!.isHideable = true
//                            mainActBottomSheetBehavior!!.state =
//                                BottomSheetBehavior.STATE_HIDDEN
                        }, 100)
                        ongoingTrip = modelList[0]
                        ongoingTrip.statuso = "Ongoing"
                        val db = FirebaseFirestore.getInstance()
                        val trips = hashMapOf("status" to ongoingTrip.statuso)
                        val query = db.collection("tripDetails")
                            .whereEqualTo("userId", ongoingTrip.userId)
                            .whereEqualTo("tripId", ongoingTrip.tripId)
                            .get()
                        query.addOnSuccessListener { result ->
                            for (document in result) {
                                db.collection("tripDetails")
                                    .document(document.id)
                                    .set(trips, SetOptions.merge())
                            }
                        }
                        Handler().postDelayed(
                            { val intent = Intent(context,MapActivity::class.java)
                                intent.putExtra("trip data for ongoing trip","coming directly to ongoing trip")
                                context.startActivity(intent)},
                            200
                        )

                    }
                })
            }
            if(model.statuso == "Arrived"){
                cancel_upcoming_ridebutton.visibility = View.GONE
                upcomingViewUI.visibility = View.GONE
                startRideButton!!.visibility = View.VISIBLE
                whiteShine_effect!!.visibility = View.VISIBLE
                callDriverButton.visibility = View.VISIBLE
                rideStartTimeInRideHistory.visibility = View.GONE
                var scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
                for(i in 1..8) {
                    scheduledExecutorService.scheduleAtFixedRate({ (context as Activity).runOnUiThread { shineStart() } }, 1, 3, TimeUnit.SECONDS) }
            }
            else if(model.statuso != "Arrived"){
                cancel_upcoming_ridebutton.visibility = View.VISIBLE
                upcomingViewUI.visibility = View.VISIBLE
                startRideButton!!.visibility = View.GONE
                whiteShine_effect!!.visibility = View.GONE
                callDriverButton.visibility = View.GONE
                rideStartTimeInRideHistory.visibility = View.VISIBLE
            }
            totalAmountOfTheRideHistory.text = model.price
            driverNameRideHistory.text = model.driverContact
            car_number_inAdapter.text = model.carId
            var startTimeInHist  = calculationUtilClass.getDateFromEpoch(model.startTime).toString()
            rideStartTimeInRideHistory.text = startTimeInHist

        }
        private fun updateDeleteDataRecordInDb() {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val db = FirebaseFirestore.getInstance()
            val tripIdOfTheFieldToBeDeleted = modelList[adapterPosition].tripId
            val tripss = hashMapOf("status" to "Cancelled")
            val collection = db.collection("tripDetails")
                .whereEqualTo("userId", userId)
                .whereEqualTo("tripId", tripIdOfTheFieldToBeDeleted)
                // .whereEqualTo("status","Schedule")
                .get()
            //.document(tripIdOfTheFieldToBeDeleted)
            //.delete()
            collection.addOnSuccessListener { result ->
                for (document in result) {
                    //  val tripId = db.collection("tripDetails").document().id
                    db.collection("tripDetails").document(document.id).set(tripss, SetOptions.merge())
                }
            }


        }

        private fun shineStart(){
            var animation = TranslateAnimation(0F, (startRideButton!!.width+whiteShine_effect!!.width).toFloat(),0F, 0F)
            animation.duration = 1500
            animation.fillAfter = false
            animation.interpolator = AccelerateDecelerateInterpolator()
            whiteShine_effect!!.startAnimation(animation)
        }
    }

}
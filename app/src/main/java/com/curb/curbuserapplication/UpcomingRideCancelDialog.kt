package com.curb.curbuserapplication

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton

class UpcomingRideCancelDialog (context:Context): Dialog(context){
    var cancelUpcomingRideButtonForFun : AppCompatButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        window!!.setBackgroundDrawable(ColorDrawable(context.resources.getColor(android.R.color.transparent)))
        setContentView(R.layout.cancel_upcoming_ride_layout)
        cancelUpcomingRideButtonForFun = findViewById(R.id.cancel_upcoming_ride_button_for_function)
    }

}

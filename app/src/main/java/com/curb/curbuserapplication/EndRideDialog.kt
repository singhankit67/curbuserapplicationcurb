package com.curb.curbuserapplication

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import com.airbnb.lottie.LottieAnimationView

class EndRideDialog (context:Context): Dialog(context){
    var endRideAnimation : LottieAnimationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        window!!.setBackgroundDrawable(ColorDrawable(context.resources.getColor(android.R.color.transparent)))
        setContentView(R.layout.end_ride_layout)
        endRideAnimation = findViewById(R.id.end_ride_animation)
        endRideAnimation!!.setMinAndMaxProgress(0.3f,0.7f)
        endRideAnimation!!.playAnimation()

    }

}

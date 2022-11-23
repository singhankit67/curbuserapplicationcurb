package com.curb.curbuserapplication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.startActivity

class AlreadyRideBookedDialog(context: Context) : Dialog(context) {

    var gotItButton : AppCompatButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawable(ColorDrawable(context.resources.getColor(android.R.color.transparent)))
        setContentView(R.layout.already_ride_booked_bottomsheet)

        gotItButton = findViewById(R.id.cancel_button_incontactus_dialog)
    }
}
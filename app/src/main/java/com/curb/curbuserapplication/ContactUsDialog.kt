package com.curb.curbuserapplication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.startActivity

class ContactUsDialog(context: Context) : Dialog(context) {

    var cancelbutton : AppCompatButton? = null
    var phoneNumberToCall : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawable(ColorDrawable(context.resources.getColor(android.R.color.transparent)))
        setContentView(R.layout.contactusdialog)

        cancelbutton = findViewById(R.id.cancel_button_incontactus_dialog)
        phoneNumberToCall = findViewById(R.id.contact_no_toCall)

        phoneNumberToCall!!.setOnClickListener {
            val driverNumberToCallTo = "tel:" + "9820798181"
            var intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse(driverNumberToCallTo)
            context.startActivity(intent) }
    }
}
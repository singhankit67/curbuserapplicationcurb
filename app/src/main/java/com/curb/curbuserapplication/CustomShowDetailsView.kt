package com.curb.curbuserapplication

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

class CustomShowDetailsView(
    context: Context,
    attrs: AttributeSet
) : RelativeLayout(context, attrs){

    private var displayDetailsUI : RelativeLayout? = null
    var editDataButton : ImageButton? = null
    var userDetailText : TextView? = null
    var verifyPhonenumberLogo : ImageView? = null

    init {
        inflate(context, R.layout.custom_show_details_view,this)

        val customAttributStyle = context.obtainStyledAttributes(attrs,R.styleable.CustomShowDetailsView,0,0)
        userDetailText = findViewById<TextView>(R.id.account_holder_name)
        val userDetailHeading = findViewById<TextView>(R.id.content_heading_tag)
        editDataButton = findViewById<ImageButton>(R.id.editicon)
        displayDetailsUI = findViewById(R.id.display_details_ui)

        try{
            userDetailText!!.text = customAttributStyle.getText(R.styleable.CustomShowDetailsView_accountHolderDetail)
            userDetailHeading.text = customAttributStyle.getText(R.styleable.CustomShowDetailsView_detailHeading)
            verifyPhonenumberLogo = findViewById(R.id.phone_number_verified)
        }
        finally {
            customAttributStyle.recycle()
        }
    }
}
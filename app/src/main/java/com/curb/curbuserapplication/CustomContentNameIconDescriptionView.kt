package com.curb.curbuserapplication

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

class CustomContentNameIconDescriptionView(
    context:Context,
    attrs:AttributeSet
) : RelativeLayout(context, attrs) {
    var nextButton : ImageButton? = null
    var upcomingRideLocater : TextView? = null
    init {
        inflate(context,R.layout.custom_content_name_icon_description_view,this)

        val customAttributeStyle = context.obtainStyledAttributes(attrs,R.styleable.CustomContentNameIconDescriptionView,0,0)

        val contentImage = findViewById<ImageView>(R.id.Content_Image)
        val contentHeading = findViewById<TextView>(R.id.content_heading)
        nextButton = findViewById<ImageButton>(R.id.go_to_next_view_button)

        try{
            contentImage.setImageDrawable(customAttributeStyle.getDrawable(R.styleable.CustomContentNameIconDescriptionView_contentIcon))
            contentHeading.text = customAttributeStyle.getText(R.styleable.CustomContentNameIconDescriptionView_contentHeading)
            upcomingRideLocater = findViewById(R.id.upcoming_text)
        }
        finally {
            customAttributeStyle.recycle() //free/clearing all the data associated with corresponding res
        }
    }
}
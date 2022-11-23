package com.curb.curbuserapplication

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

class CustomContentNameIconView(
    context:Context,
    attrs:AttributeSet
): RelativeLayout(context, attrs) {
    var nextButton : ImageButton? = null
    var lineDivider : View? = null
    var locationVerifier : TextView? = null

    init {
        inflate(context,R.layout.custom_content_icon_name_view,this)
        val customAttributeStyle = context.obtainStyledAttributes(attrs,R.styleable.CustomContentNameIconView,0,0)
        val contentImage = findViewById<ImageView>(R.id.content_Image)
        val contentName = findViewById<TextView>(R.id.content_name)

        try{

            contentImage.setImageDrawable(customAttributeStyle.getDrawable(R.styleable.CustomContentNameIconView_content_Icon))
            contentName.text = customAttributeStyle.getText(R.styleable.CustomContentNameIconView_content_Heading)
            nextButton = findViewById(R.id.go_to_next_view_button1)
            lineDivider = findViewById(R.id.lineDivider1)
            locationVerifier = findViewById(R.id.location_verifier)

        }
        finally {
            customAttributeStyle.recycle()
        }
    }
}
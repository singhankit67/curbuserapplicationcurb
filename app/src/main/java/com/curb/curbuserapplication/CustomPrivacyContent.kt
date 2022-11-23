package com.curb.curbuserapplication

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class CustomPrivacyContent(context: Context,
                           attrs: AttributeSet
) : ConstraintLayout(context, attrs){
    init {
        inflate(context, R.layout.privacy_policy_single_view,this)

        val customAttributStyle = context.obtainStyledAttributes(attrs,R.styleable.CustomPrivacyContent,0,0)
        val content_heading = findViewById<TextView>(R.id.content_heading_for_privacy)
        val content_text = findViewById<TextView>(R.id.content_text_for_privacy_policy)

        try{
            content_heading!!.text = customAttributStyle.getText(R.styleable.CustomPrivacyContent_heading_text)
            content_text.text = customAttributStyle.getText(R.styleable.CustomPrivacyContent_content_text)
        }
        finally {
            customAttributStyle.recycle()
        }
    }
}
package com.curb.curbuserapplication

import android.content.Context

import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class CustomEditDetailsView(
    context:Context,
    attrs:AttributeSet
) :TextInputLayout(context, attrs){
    var editDetialsUi : TextInputEditText? = null

    init {
        inflate(context, R.layout.custom_edit_details_view, this)
        val customAttributeStyle = context.obtainStyledAttributes(attrs,R.styleable.CustomEditDetailsView,0,R.style.CustomBoxWithoutOutline)
        editDetialsUi = findViewById<TextInputEditText>(R.id.edit_details_ui)
        try {
            editDetialsUi!!.setHintTextColor(
                customAttributeStyle.getColor(
                    R.styleable.CustomEditDetailsView_hintTextColor,
                    0
                )
            )
            editDetialsUi!!.hint = customAttributeStyle.getText(R.styleable.CustomEditDetailsView_editHintText)
        }
        finally {
            customAttributeStyle.recycle()
        }
    }
}
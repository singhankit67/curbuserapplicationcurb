package com.curb.curbuserapplication

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CustomTextInputLayout(context:Context,
                            attrs:AttributeSet
):ConstraintLayout(context, attrs) {
    var editText : TextInputEditText? = null
    init {
        inflate(context, R.layout.custom_text_input_layout, this)
        val customAttributeStyle = context.obtainStyledAttributes(attrs, R.styleable.CustomTextInputLayout,0,0)
        val textInputLayout = findViewById<TextInputLayout>(R.id.customTextInputLayout)
        editText = findViewById(R.id.inputEditText)

        try{
            textInputLayout.helperText = customAttributeStyle.getString(R.styleable.CustomTextInputLayout_helperText)
            textInputLayout.error = customAttributeStyle.getString(R.styleable.CustomTextInputLayout_errorTexttext)
            textInputLayout.hint = customAttributeStyle.getString(R.styleable.CustomTextInputLayout_hintText)
        }
        finally {
            customAttributeStyle.recycle()
        }
    }
}
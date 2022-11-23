package com.curb.curbuserapplication

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class CustomPersistantBottomSheet(
    context:Context,
    attrs:AttributeSet
): ConstraintLayout(context, attrs) {

    var closeBottomSheetButton :ImageButton? = null

    init {
        inflate(context,R.layout.custom_bottom_sheet,this)
        val customAttributeStyle = context.obtainStyledAttributes(attrs,R.styleable.CustomBottomSheet,0,0)
        val customBottomSheetHeaderText = findViewById<TextView>(R.id.bottom_sheet_header_text)
        closeBottomSheetButton = findViewById<ImageButton>(R.id.bottom_sheet_close_button)
        val customBottomSheetUi = findViewById<ConstraintLayout>(R.id.custom_persistant_bottom_sheet)

        try{
            customBottomSheetHeaderText.text = customAttributeStyle.getText(R.styleable.CustomBottomSheet_bottomsheetheadertext)
            closeBottomSheetButton!!.setOnClickListener {
                customBottomSheetUi.visibility = View.GONE
            }
        }
        finally {
            customAttributeStyle.recycle()
        }
    }
}
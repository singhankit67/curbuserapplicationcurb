package com.curb.curbuserapplication

import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class RideOptionsBottomsheetActivity : AppCompatActivity() {
    var numPickerMin : NumberPicker? = null
    var numPickerHr : NumberPicker? = null
    var numPickerAmPm : NumberPicker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_options_bottomsheet)

//        numPickerMin = findViewById(R.id.numPickerM)
//        numPickerHr = findViewById(R.id.numPickerH)
//        numPickerAmPm = findViewById(R.id.numPickerAm)
//        numPickerMin!!.minValue = 0
//        numPickerMin!!.maxValue = 59
//        numPickerHr!!.minValue = 1
//        numPickerHr!!.maxValue = 12
//
//        val str = arrayOf<String>("AM", "PM")
//        numPickerAmPm!!.displayedValues = str


    }

}
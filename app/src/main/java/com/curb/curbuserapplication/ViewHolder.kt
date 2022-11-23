package com.curb.curbuserapplication

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(
    itemView: View,
    val listener: CalendarAdapter.OnItemClickListener):
    RecyclerView.ViewHolder(itemView) {

    var txtDay: TextView? = null
    var txtDayInWeek: TextView? =null
    var linearlayout: LinearLayout? =null
    var monthName : TextView?= null

    init {
        txtDay = itemView.findViewById(R.id.txt_date)
        txtDayInWeek = itemView.findViewById(R.id.txt_day)
        linearlayout = itemView.findViewById(R.id.calendar_linear_layout)
        monthName = itemView.findViewById(R.id.month_name)
    }
}
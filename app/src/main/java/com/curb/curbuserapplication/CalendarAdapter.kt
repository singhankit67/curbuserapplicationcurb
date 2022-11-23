package com.curb.curbuserapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList

class CalendarAdapter (private val context: Context,
                       private val thatDay:ArrayList<Date>,
                       private val currentDate:Calendar):RecyclerView.Adapter<ViewHolder>() {
    private var mListener: OnItemClickListener? = null
    // private lateinit var onClickInterface : onClickInterface
    var clickedPos = -1
    var selectedItem  =0
    // This is true only the first time you load the calendar.
    private var selectCurrentDate = true
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val selectedDay = null
    //    when {
//        changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
//       else -> currentDay
//    }
    private val selectedMonth = null
    //        when {
//            changeMonth != null -> changeMonth[Calendar.MONTH]
//          else -> currentMonth
//        }
    private val selectedYear = null
    //        when {
//            changeMonth != null -> changeMonth[Calendar.YEAR]
//             else -> currentYear
//        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(
            inflater.inflate(
                R.layout.custom_calender_day,
                parent,
                false
            ), mListener!!
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cal = Calendar.getInstance()
        cal.time = thatDay[position]

        val displayMonth = cal[Calendar.MONTH]
        val displayYear= cal[Calendar.YEAR]
        val displayDay = cal[Calendar.DAY_OF_MONTH]
        try {
            val dayOfweek = cal.get(Calendar.DAY_OF_WEEK)
            holder.txtDayInWeek!!.text = getWeekDayToString(dayOfweek)
        }
        catch (ex:ParseException) {
            Log.v("Exception", ex.localizedMessage!!)
        }
        holder.txtDay!!.text = cal[Calendar.DAY_OF_MONTH].toString()
        val actualMonth = cal[Calendar.MONTH] + 1
        holder.monthName!!.text = getCalenderMonthtoString(actualMonth.toString())
        /**
         * Invoke OnClickListener and make the item selected.
         */
        holder.linearlayout!!.setOnClickListener {
            selectedItem = position
            selectCurrentDate = false
            notifyDataSetChanged()}
        holder.txtDay!!.setTextColor(Color.parseColor("#535559"))
        holder.txtDayInWeek!!.setTextColor(Color.parseColor("#535559"))
        holder.monthName!!.setTextColor(Color.parseColor("#535559"))
        holder.linearlayout!!.setBackgroundResource(R.drawable.sunselected_calendar_item)
        holder.linearlayout!!.isEnabled = true
        if(selectedItem == position){
            holder.txtDay!!.setTextColor(Color.parseColor("#0cba70"))
            holder.txtDayInWeek!!.setTextColor(Color.parseColor("#0cba70"))
            holder.monthName!!.setTextColor(Color.parseColor("#0cba70"))
            holder.linearlayout!!.setBackgroundResource(R.drawable.selected_calendar_item)
            holder.linearlayout!!.isEnabled = false
            clickedPos = holder.adapterPosition
        }
    }

    override fun getItemCount(): Int {
        return 7
    }
    private fun getWeekDayToString(weekDayInInt : Int): String{
        if (weekDayInInt == 1){
            return "SUN"
        }
        if (weekDayInInt == 2){
            return "MON"
        }
        if (weekDayInInt == 3){
            return "TUE"
        }
        if (weekDayInInt == 4){
            return "WED"
        }
        if (weekDayInInt == 5){
            return "THU"
        }
        if (weekDayInInt == 6){
            return "FRI"
        }
        if (weekDayInInt == 7){
            return "SAT"
        }
        else{
            return "Invalid"
        }

    }

    private fun getCalenderMonthtoString(month: String): String {
        if (month == "1"){
            return "JAN"
        }
        if (month == "2"){
            return "FEB"
        }
        if (month == "3"){
            return "MAR"
        }
        if (month == "4"){
            return "APR"
        }
        if (month == "5"){
            return "MAY"
        }
        if (month == "6"){
            return "JUN"
        }
        if (month == "7"){
            return "JUL"
        }
        if (month == "8"){
            return "AUG"
        }
        if (month == "9"){
            return "SEP"
        }
        if (month == "10"){
            return "OCT"
        }
        if (month == "11"){
            return "NOV"
        }
        if (month == "12"){
            return "DEC"
        }
        else{
            return "INVALID"
        }

    }
    interface OnItemClickListener {
        fun onItemClick(/*position: Int*/)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }
    /**
     * This make the item selected.
     */
    private fun makeItemSelected(holder: ViewHolder) {
        holder.txtDay!!.setTextColor(Color.parseColor("#0cba70"))
        holder.txtDayInWeek!!.setTextColor(Color.parseColor("#0cba70"))
        holder.monthName!!.setTextColor(Color.parseColor("#0cba70"))
        holder.linearlayout!!.setBackgroundResource(R.drawable.selected_calendar_item)
        holder.linearlayout!!.isEnabled = false
    }

    /**
     * This make the item default.
     */
    private fun makeItemDefault(holder: ViewHolder) {
        holder.txtDay!!.setTextColor(Color.parseColor("#535559"))
        holder.txtDayInWeek!!.setTextColor(Color.parseColor("#535559"))
        holder.monthName!!.setTextColor(Color.parseColor("#535559"))
        holder.linearlayout!!.setBackgroundResource(R.drawable.sunselected_calendar_item)
        holder.linearlayout!!.isEnabled = true
    }
}
package com.curb.curbuserapplication

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun getDayName(date: Date): String =
        SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
    fun getMonthNumber(monthnumber: String): String {
        if(monthnumber == "January"){
            return "01"
        }
        if(monthnumber == "February"){
            return "02"
        }
        if(monthnumber == "March"){
            return "03"
        }
        if(monthnumber == "April"){
            return "04"
        }
        if(monthnumber == "May"){
            return "05"
        }
        if(monthnumber == "June"){
            return "06"
        }
        if(monthnumber == "July"){
            return "07"
        }
        if(monthnumber == "August"){
            return "08"
        }
        if(monthnumber == "September"){
            return "09"
        }
        if(monthnumber == "October"){
            return "10"
        }
        if(monthnumber == "November"){
            return "11"
        }
        if(monthnumber == "December"){
            return "12"
        }
        else{
            return "INVALID"
        }

    }

    fun getMonthName(monthnumber: String): String {
        if(monthnumber == "0"){
            return "Jan"
        }
        if(monthnumber == "1"){
            return "Feb"
        }
        if(monthnumber == "2"){
            return "Mar"
        }
        if(monthnumber == "3"){
            return "Apr"
        }
        if(monthnumber == "4"){
            return "May"
        }
        if(monthnumber == "5"){
            return "Jun"
        }
        if(monthnumber == "6"){
            return "Jul"
        }
        if(monthnumber == "7"){
            return "Aug"
        }
        if(monthnumber == "8"){
            return "Sep"
        }
        if(monthnumber == "9"){
            return "Oct"
        }
        if(monthnumber == "10"){
            return "Nov"
        }
        if(monthnumber == "11"){
            return "Dec"
        }
        else{
            return "INVALID"
        }

    }

    fun dayOfTheWeektoString(dayOfWeek: Int): String? {
        if (dayOfWeek == 1){
            return "SUN"
        }
        if (dayOfWeek == 2){
            return "MON"
        }
        if (dayOfWeek == 3){
            return "TUE"
        }
        if (dayOfWeek == 4){
            return "WED"
        }
        if (dayOfWeek == 5){
            return "THU"
        }
        if (dayOfWeek == 6){
            return "FRI"
        }
        if (dayOfWeek == 7){
            return "SAT"
        }
        else{
            return "Invalid"
        }

    }

    fun getFullMonthName(selectedMonthForRide: String?): Any {
        if(selectedMonthForRide == "0"){
            return "January"
        }
        if(selectedMonthForRide == "1"){
            return "February"
        }
        if(selectedMonthForRide == "2"){
            return "March"
        }
        if(selectedMonthForRide == "3"){
            return "April"
        }
        if(selectedMonthForRide == "4"){
            return "May"
        }
        if(selectedMonthForRide == "5"){
            return "June"
        }
        if(selectedMonthForRide == "6"){
            return "July"
        }
        if(selectedMonthForRide == "7"){
            return "August"
        }
        if(selectedMonthForRide == "8"){
            return "September"
        }
        if(selectedMonthForRide == "9"){
            return "October"
        }
        if(selectedMonthForRide == "10"){
            return "November"
        }
        if(selectedMonthForRide == "11"){
            return "December"
        }
        else{
            return "INVALID"
        }

    }
}
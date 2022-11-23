package com.curb.curbuserapplication.data.model

data class LoggedInUser(
    val userId: String,
    val displayName: String,
    val phoneNumber: String,
    val Email:String,
    val HomeLocation:String,
    val WorkLocation:String
)
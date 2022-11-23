package com.curb.curbuserapplication

class UserClass(
    var userId:String,
    var phoneNumber:String,
    var displayName:String,
    var emailId:String,
    var homeAddress:String,
    var workAddress:String
) {
    constructor() : this("","","","","","")
}
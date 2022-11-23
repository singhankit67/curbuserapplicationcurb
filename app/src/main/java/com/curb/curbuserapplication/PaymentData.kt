package com.curb.curbuserapplication

import java.io.Serializable


data class PaymentData(
    val userId: String ,
    val displayName: String ,
    val phoneNumber: String ,
    val Email: String? ,
    val currency: String ,
    val amountInPaisa: Double ,
    val description: String,
    val tripId: String
): Serializable {
    constructor( currency: String , amountInRupees: String , description: String, tripId: String, userId: String, userName : String, userPhoneNumber: String, userEmailId : String ):
            this(
                userId = userId,
                displayName = userName,
                phoneNumber = userPhoneNumber,
                Email = userEmailId,
                currency = currency,
                amountInPaisa = convertAmountFromRupeesToPaisa(amountInRupees),
                description = description,
                tripId = tripId
            )

    companion object {
        private fun convertAmountFromRupeesToPaisa(amount: String): Double {
            return amount.toDouble().times(100)
        }
    }
}
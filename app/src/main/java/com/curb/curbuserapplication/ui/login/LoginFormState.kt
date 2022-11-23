package com.curb.curbuserapplication.ui.login

data class LoginFormState(
    val nameError: Int? = null,
    val phoneNumberError: Int? = null,
    val isDataValid: Boolean = false
)
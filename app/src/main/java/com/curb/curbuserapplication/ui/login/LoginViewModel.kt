package com.curb.curbuserapplication.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.curb.curbuserapplication.R
import com.curb.curbuserapplication.data.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.curb.curbuserapplication.data.Result

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(name: String, phoneNumber: String,Email:String,workLocation:String,HomeLocation:String) {
        // can be launched in a separate asynchronous job
        runBlocking(Dispatchers.IO) {
            launch {

                when (val result = loginRepository.login(name, phoneNumber,Email,workLocation,HomeLocation)) {
                    is Result.NewSuccess -> {
                        loginRepository.createNewUser(name, phoneNumber)
                        _loginResult.postValue(LoginResult(success = LoggedInUserView(displayName = (result.data.displayName))))
                    }
                    is Result.Success -> {
                        _loginResult.postValue(LoginResult(success = LoggedInUserView(displayName = (result.data.displayName))))
                    }
                    else -> {
                        _loginResult.postValue(LoginResult(error = R.string.login_failed))
                    }
                }
            }
        }
    }

    fun loginDataChanged(name: String, phoneNumber: String) {
        if (!isUserNameValid(name)) {
            _loginForm.value = LoginFormState(nameError = R.string.invalid_name)
        } else if (!isPhoneNumberValid(phoneNumber)) {
            _loginForm.value = LoginFormState(phoneNumberError = R.string.invalid_phone_number)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder name validation check
    private fun isUserNameValid(name: String): Boolean {
        return if (name.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(name).matches()
        } else {
            name.isNotBlank()
        }
    }

    // A placeholder name validation check
    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return if (phoneNumber.length == 10) {
            Patterns.PHONE.matcher(phoneNumber).matches()
        } else {
            phoneNumber.isNotBlank()
        }
    }

    // A placeholder phoneNumber validation check
    private fun isPasswordValid(phoneNumber: String): Boolean {
        return phoneNumber.length > 5
    }
}
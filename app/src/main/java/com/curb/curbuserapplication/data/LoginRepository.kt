package com.curb.curbuserapplication.data

import com.curb.curbuserapplication.data.model.LoggedInUser


class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun login(name: String, phoneNumber: String,Email:String,HomeLocation:String,WorkLocation:String): Result<LoggedInUser> {
        // handle login
        val result: Result<LoggedInUser> = dataSource.login(name, phoneNumber,Email, HomeLocation, WorkLocation)

        when (result) {
            is Result.Success,  -> setLoggedInUser(result.data)
            is Result.NewSuccess,  -> setLoggedInUser(result.data)
            else -> {}
        }

        return result
    }

    suspend fun createNewUser(name: String, phoneNumber: String): LoggedInUser {
        // handle signUp
        return dataSource.createNewUser(name, phoneNumber)
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // TODO: If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}
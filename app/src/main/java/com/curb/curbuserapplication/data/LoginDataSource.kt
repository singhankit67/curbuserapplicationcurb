package com.curb.curbuserapplication.data


import com.curb.curbuserapplication.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private val mAuth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()

    suspend fun login(name: String, phoneNumber: String,Email:String,HomeLocation:String,WorkLocation:String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication

            val userCollection = mFirestore.collection("users")
            // TODO: make constants for DB fields

            val documentsQuery = userCollection
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .await()
            val documents = documentsQuery.documents
            if (documents.isEmpty()) {
//                val loggedInUser = createNewUser(name, phoneNumber)
                val user = LoggedInUser(java.util.UUID.randomUUID().toString(), name, phoneNumber,"","","")
                return Result.NewSuccess(user)
            } else if (documents.size == 1){
                val document = documents[0]
                // TODO: return the appropriate name
                val user = LoggedInUser(document.id, document.data?.get("name")?.toString() ?: name, phoneNumber,Email, HomeLocation, WorkLocation)
                return Result.Success(user)
            } else {
                // TODO: should not have multiple users with the same phone number, throw an error saying phone number already in use
            }
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), name, phoneNumber,"","","")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            throw e
        }
    }

    suspend fun createNewUser(userName: String, phoneNumber: String): LoggedInUser {
        val userCollection = mFirestore.collection("users")
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val user = LoggedInUser(uid.toString(), userName, phoneNumber,"","","")
        userCollection
            .document(uid.toString()).set(user)
            .await()
        return user
    }

    fun logout() {
        // TODO: revoke authentication
    }
}
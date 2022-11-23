package com.curb.curbuserapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {

    //Setup firebase
    val mStorage = FirebaseStorage.getInstance()
    val mAuth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mCreateUserButton: Button = findViewById(R.id.createUser)
        val userNameTextBox: EditText = findViewById(R.id.fullname)
        val phoneNumberTextBox: EditText = findViewById(R.id.number)
        mCreateUserButton.setOnClickListener {
            // TODO: validate text in user input
//            createNewUser(userNameTextBox.text.toString(), phoneNumberTextBox.text.toString())
        }
    }

    private fun validateEntries() {
        //TODO: Validate R..id.name
        //TODO: Validate R..id.phoneNumber
    }
}
package com.curb.curbuserapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.curb.curbuserapplication.ui.login.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SignoutBottomsheet: BottomSheetDialogFragment() {
    lateinit var cancelButton : AppCompatButton
    lateinit var signOutButton : AppCompatButton

    companion object {

        const val TAG = "CustomBottomSheetDialogFragment"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottomsheetfragment, container, false)
        cancelButton = view.findViewById(R.id.cancel_button)
        signOutButton = view.findViewById(R.id.sign_out_button)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.isCancelable = false
        cancelButton.setOnClickListener { this.dismiss() }
        signOutButton.setOnClickListener { val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)}


    }
}
package com.curb.curbuserapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.curb.curbuserapplication.databinding.ActivityPrivacySettingsBinding


class PrivacySettings : AppCompatActivity() {
    private lateinit var binding : ActivityPrivacySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewUtils.enableTransparentStatusBar(window)
        binding.goBackButton.setOnClickListener {
            //onBackPressed()
            finish()
        }

        binding.IUnderstandButton.setOnClickListener { finish() }
    }
}
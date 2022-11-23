package com.curb.curbuserapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class RideHistoryActivity : AppCompatActivity() {
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var db = FirebaseFirestore.getInstance()
    var tabValueRecivere :String? = null
    private var backButtonForRideHistory : ImageButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_history)
        ViewUtils.enableTransparentStatusBar(window)
        backButtonForRideHistory = findViewById(R.id.goBackButton_ride_history)
        tabValueRecivere = intent.getStringExtra("tab_value")
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        backButtonForRideHistory!!.setOnClickListener {
            if (tabValueRecivere == "0") {
                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra("ride_history_back", "back from ride history")
                startActivity(intent)
                finish()
            }
            else if(tabValueRecivere != "0" || tabValueRecivere.isNullOrEmpty()){
                val intent = Intent(this,SettingsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (tabValueRecivere == "0") {
                    val intent = Intent(this@RideHistoryActivity, MapActivity::class.java)
                    intent.putExtra("ride_history_back", "back from ride history")
                    startActivity(intent)
                    finish()
                }
                else if(tabValueRecivere != "0" || tabValueRecivere.isNullOrEmpty()){
                    val intent = Intent(this@RideHistoryActivity,SettingsActivity::class.java)
                    intent.putExtra("normal back hit","normal back pressed")
                    startActivity(intent)
                    finish()
                }
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
        tabLayout!!.addTab(tabLayout!!.newTab().setText("UPCOMING"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("COMPLETED"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("CANCELLED"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = TabsdAdapter(this, supportFragmentManager, tabLayout!!.tabCount)
        viewPager!!.adapter = adapter
        viewPager!!.offscreenPageLimit = 3
        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) { viewPager!!.currentItem = tab.position} })
    }
}
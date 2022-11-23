package com.curb.curbuserapplication

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabsdAdapter (private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                //  val homeFragment: HomeFragment = HomeFragment()
                UpcomingRideFragment()
            }
            1 -> {
                CompletedRideFragment()
            }
            2 ->{
                CancelledRideFragment()
            }
            // else -> null
            else -> return   CancelledRideFragment()
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return 3
    }
}
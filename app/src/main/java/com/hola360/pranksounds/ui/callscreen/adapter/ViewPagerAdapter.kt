package com.hola360.pranksounds.ui.callscreen.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hola360.pranksounds.ui.callscreen.mycaller.MyCallerFragment
import com.hola360.pranksounds.ui.callscreen.trendcaller.TrendCallerFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0->{
                TrendCallerFragment()
            }
            1->{
                MyCallerFragment()
            }
            else->{
                TrendCallerFragment()
            }

        }
    }


}
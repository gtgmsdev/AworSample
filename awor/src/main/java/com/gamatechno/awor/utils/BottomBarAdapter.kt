package com.gamatechno.awor.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class BottomBarAdapter(fm: FragmentManager) : SmartFragmentStatePagerAdapter(fm,
    BEHAVIOR_SET_USER_VISIBLE_HINT) {
    private val fragments = ArrayList<Fragment>()

    fun addFragments(fragment: Fragment) {
        fragments.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}
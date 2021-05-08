package com.example.ping.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ping.fragments.inboxFragment
import com.example.ping.fragments.peopleFragment


class ScreenSlideAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when(position) {
        0-> inboxFragment()
        else -> peopleFragment()
    }




}

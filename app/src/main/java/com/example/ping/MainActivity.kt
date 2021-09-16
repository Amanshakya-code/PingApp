package com.example.ping

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.ping.adapter.FragmentViewPagerAdapter
import com.example.ping.adapter.ScreenSlideAdapter
import com.example.ping.fragments.UserProfile
import com.example.ping.fragments.inboxFragment
import com.example.ping.fragments.peopleFragment
import com.example.ping.models.User
import com.fxn.OnBubbleClickListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    private var mCurrentId:String?=""
    val database by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mCurrentId = FirebaseAuth.getInstance().uid!!

        val chatfragment = inboxFragment()
        val peoplefragment = peopleFragment()
        val profileView = UserProfile()

        val viewPagerAdapter = FragmentViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(chatfragment)
        viewPagerAdapter.addFragment(peoplefragment)
        viewPagerAdapter.addFragment(profileView)
        //Setting Adapter To Viewpager
        fragmentViewPager.adapter = viewPagerAdapter
        fragmentViewPager.setOffscreenPageLimit(2);
        //Setting default Fragment
        //Setting Viewpager To TabBar
        bottomTabBar.setupBubbleTabBar(viewPager = fragmentViewPager)
        fragmentViewPager.currentItem = 0
        bottomTabBar.setSelected(0,false)
        bottomTabBar.isFocusableInTouchMode = true
        bottomTabBar.touchscreenBlocksFocus = true
        bottomTabBar.addBubbleListener(object : OnBubbleClickListener {
            override fun onBubbleClick(id: Int) {
                when (id) {
                    R.id.chattingFragment -> fragmentViewPager.currentItem = 0
                    R.id.peopleFragment -> fragmentViewPager.currentItem = 1
                    R.id.userprofile -> fragmentViewPager.currentItem = 2
                }
            }

        })

    }


    private fun onlinestatus(status:String){
        val reference  = database.collection("users").document(mCurrentId!!)
        var hashMap: HashMap<String, Any> = HashMap<String, Any>()
        hashMap.put("onlineStatus",status)
        reference.update(hashMap)

    }

    override fun onStart() {
        super.onStart()
        onlinestatus("online")
    }

    override fun onPause() {
        super.onPause()
        onlinestatus("offline")
    }
    fun viewpagerspecific()
    {
        fragmentViewPager.setCurrentItem(1)
    }


}



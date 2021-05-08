package com.example.ping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.ping.adapter.ScreenSlideAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setup the toolbar
        setSupportActionBar(toolbar)
        viewPager.adapter = ScreenSlideAdapter(this)
        TabLayoutMediator(tabs,viewPager,TabLayoutMediator.TabConfigurationStrategy{ tab: TabLayout.Tab, position: Int ->
            when(position){
                0->tab.text = "Chats"
                1->tab.text = "People"
            }
        }).attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = menuInflater
        inflater.inflate(R.menu.first_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.porfile-> {
                startActivity(Intent(this,profileview::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
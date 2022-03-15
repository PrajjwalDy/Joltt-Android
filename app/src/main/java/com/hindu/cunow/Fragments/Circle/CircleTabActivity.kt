package com.hindu.cunow.Fragments.Circle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hindu.cunow.Adapter.CircleTabsAdapter
import com.hindu.cunow.R

class CircleTabActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_tab)

        val tabLayout = findViewById<TabLayout>(R.id.circle_tabs_layout)
        val viewPager2 = findViewById<ViewPager2>(R.id.circle_viewPager)

        val adapter = CircleTabsAdapter(supportFragmentManager,lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout,viewPager2){tab,position->
            when(position){
                0-> {
                    tab.text = "Joined"
                }
                1-> {
                    tab.text = "Explore Circles"
                }
                2 ->{
                    tab.text = "My Circles"
                }
            }

        }.attach()

    }
}
package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hindu.cunow.Adapter.AboutMeTabsAdapter
import com.hindu.cunow.Adapter.CircleTabsAdapter
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_about_me_tabs.*

class AboutMeTabsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_me_tabs)

        val tabLayout = findViewById<TabLayout>(R.id.aboutMe_tabs_layout)
        val viewPager2 = findViewById<ViewPager2>(R.id.aboutMe_viewPager)

        val adapter = AboutMeTabsAdapter(supportFragmentManager,lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout,viewPager2){tab,position->
            when(position){
                0-> {
                    tab.text = "About Me"
                }
                1-> {
                    tab.text = "My Posts"
                }
                2 ->{
                    tab.text = "My Saved"

                }
            }


        }.attach()

        fun setSupportActionBar(userNameAboutMe: TextView?) {
            TODO("Not yet implemented")

        }
 fun setupActionBar(){
         setSupportActionBar(userNameAboutMe)

         val actionBar = supportActionBar
         if(actionBar != null){
             actionBar.setDisplayHomeAsUpEnabled(true)
             //actionBar.setHomeAsUpIndicator(R.drawable.eye)
         }

     }
    }
}
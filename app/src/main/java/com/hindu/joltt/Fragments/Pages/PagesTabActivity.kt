package com.hindu.joltt.Fragments.Pages

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hindu.cunow.R
import com.hindu.joltt.Activity.CreatePageActivity
import com.hindu.joltt.Adapter.PageTabAdapter

class PagesTabActivity : AppCompatActivity() {

    private lateinit var pagesBack:ImageView
    private lateinit var pageTxt:TextView

    private lateinit var CP_img:ImageView
    private lateinit var CP_TV:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages_tab)

        pageTxt = findViewById(R.id.pageTxt)
        pagesBack = findViewById(R.id.pagesBack)

        CP_img = findViewById(R.id.CP_img)
        CP_TV = findViewById(R.id.CP_TV)

        val tabLayout = findViewById<TabLayout>(R.id.pages_tabs_layout)
        val viewPager2 = findViewById<ViewPager2>(R.id.pages_viewPager)

        val adapter = PageTabAdapter(supportFragmentManager,lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout,viewPager2){tab,position->

            when(position){
                0->{
                    tab.text = "Explore Pages"
                }
                1->{
                    tab.text = "My Pages"
                }
            }

            pagesBack.setOnClickListener {
                finish()
            }
            pageTxt.setOnClickListener {
                finish()
            }

        }.attach()

        CP_img.setOnClickListener {
            val intent = Intent(this, CreatePageActivity::class.java)
            startActivity(intent)
        }

        CP_TV.setOnClickListener {
            val intent = Intent(this, CreatePageActivity::class.java)
            startActivity(intent)

            finish()

        }


    }
}
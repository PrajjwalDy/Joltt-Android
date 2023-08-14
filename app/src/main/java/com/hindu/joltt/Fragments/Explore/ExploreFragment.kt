package com.hindu.joltt.Fragments.Explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hindu.cunow.R
import com.hindu.joltt.Adapter.ExploreTabAdapter

class ExploreFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View= inflater.inflate(R.layout.explore_fragment, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = view.findViewById(R.id.explore_tabs_layout)
        viewPager = view.findViewById(R.id.explore_viewPager)

        val adapter = ExploreTabAdapter(childFragmentManager,lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout,viewPager){tab,position->

            when(position){
                0-> {
                    tab.text = "Home"
                }
                1->{
                    tab.text = "Trending"
                }
                2->{
                    tab.text = "Internships"
                }
                3->{
                    tab.text = "Hackathons"
                }
            }

        }.attach()

    }




}
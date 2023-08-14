package com.hindu.joltt.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hindu.joltt.Fragments.ExploreTabs.HomeTab
import com.hindu.joltt.Fragments.Hackathons.HackathonsFragment
import com.hindu.joltt.Fragments.HashTag.HasTagFragment
import com.hindu.joltt.Fragments.Internship.InternshipsFragment

class ExploreTabAdapter(fm:FragmentManager,lifecycle:Lifecycle):FragmentStateAdapter(fm,lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                HomeTab()
            }
            1->{
                HasTagFragment()
            }
            2->{
                InternshipsFragment()
            }
            3->{
                HackathonsFragment()
            }

            else->{
                HomeTab()
            }

        }
    }


}
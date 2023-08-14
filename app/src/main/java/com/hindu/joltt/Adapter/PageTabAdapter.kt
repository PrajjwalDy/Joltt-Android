package com.hindu.joltt.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hindu.joltt.Fragments.Pages.ExplorePages.ExplorePagesFragment
import com.hindu.joltt.Fragments.Pages.MyPages.MyPagesFragment

class PageTabAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                ExplorePagesFragment()
            }
            1->{
                MyPagesFragment()
            }else->{
                ExplorePagesFragment()
            }
        }
    }

}
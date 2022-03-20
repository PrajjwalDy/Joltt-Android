package com.hindu.cunow.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hindu.cunow.Fragments.AboutMe.MyDetails.MyDetailsFragment
import com.hindu.cunow.Fragments.AboutMe.MySaved.MySavedFragment
import com.hindu.cunow.Fragments.AboutMe.Posts.MyPostsFragemt

class AboutMeTabsAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
       return  when(position){
           0->{
               MyDetailsFragment()
           }
           1->{
               MyPostsFragemt()
           }
           2->{
               MySavedFragment()
           }
           else->{
               MyDetailsFragment()
           }
       }
    }


}
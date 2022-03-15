package com.hindu.cunow.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hindu.cunow.Fragments.Circle.ExploreCircles.ExploreCirclesFragments
import com.hindu.cunow.Fragments.Circle.JoinedCircles.JoinedCirclesFragment
import com.hindu.cunow.Fragments.Circle.MyCircles.MyCirclesFragment

class CircleTabsAdapter(fragmentManager: FragmentManager, lifecycle:Lifecycle):FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
       return when(position){
           0-> {
               JoinedCirclesFragment()
           }
           1->{
               ExploreCirclesFragments()
           }
           2->{
               MyCirclesFragment()
           }
           else->{
              JoinedCirclesFragment()
           }
       }
    }
}
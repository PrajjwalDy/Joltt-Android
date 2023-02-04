package com.hindu.cunow.Adapter

import android.provider.Contacts.People
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hindu.cunow.Fragments.Academics.AcademicsFragment
import com.hindu.cunow.Fragments.Event.EventFragment
import com.hindu.cunow.Fragments.ExploreTabs.HomeTab
import com.hindu.cunow.Fragments.Hackathons.HackathonsFragment
import com.hindu.cunow.Fragments.HashTag.HasTagFragment
import com.hindu.cunow.Fragments.Internship.InternshipsFragment
import com.hindu.cunow.Fragments.People.PeopleFragment
import com.hindu.cunow.Fragments.PublicPost.PublicPostFragement

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
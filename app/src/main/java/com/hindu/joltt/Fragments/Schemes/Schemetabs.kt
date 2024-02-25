package com.hindu.joltt.Fragments.Schemes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hindu.joltt.Fragments.Schemes.GovtJobs.Admission
import com.hindu.joltt.Fragments.Schemes.GovtJobs.GovtJobs
import com.hindu.joltt.Fragments.Schemes.GovtJobs.Result

class Schemetabs(fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm,lifecycle)  {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                GovtJobs()
            }
            1->{
                Result()
            }
            2->{
                Admission()
            }

            else->{
                GovtJobs()
            }
        }
    }
}
package com.hindu.cunow.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hindu.cunow.Fragments.Projects.AllProjects.AllProjectsFragment
import com.hindu.cunow.Fragments.Projects.MyApplications.MyApplicationsFragment
import com.hindu.cunow.Fragments.Projects.MyProjects.MyProjectsFragment

class ProjectsTabAdapter(fm: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fm,lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                AllProjectsFragment()
            }
            1->{
                MyProjectsFragment()
            }
            2->{
                MyApplicationsFragment()
            }

            else->{
                AllProjectsFragment()
            }

        }
    }

}
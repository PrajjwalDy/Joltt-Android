package com.hindu.cunow.Fragments.Projects

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hindu.cunow.Activity.AddProjectActivity
import com.hindu.cunow.Adapter.ExploreTabAdapter
import com.hindu.cunow.Adapter.ProjectsTabAdapter
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.fragment_projects.view.*

class ProjectsFragment : Fragment() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.fragment_projects, container, false)

        root.addProject_button.setOnClickListener {
            startActivity(Intent(context,AddProjectActivity::class.java))
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = view.findViewById(R.id.projects_tabs_layout)
        viewPager = view.findViewById(R.id.projects_viewPager)

        val adapter = ProjectsTabAdapter(childFragmentManager,lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout,viewPager){tab,position->

            when(position){
                0-> {
                    tab.text = "Projects"
                }
                1->{
                    tab.text = "My Projects"
                }
                2->{
                    tab.text = "My Applications"
                }
            }

        }.attach()
    }
}
package com.hindu.cunow.Fragments.Explore

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.FeedbackActivity
import com.hindu.cunow.Adapter.AboutMeTabsAdapter
import com.hindu.cunow.Adapter.ExploreTabAdapter
import com.hindu.cunow.Adapter.UserAdapter
import com.hindu.cunow.Fragments.Circle.CircleTabActivity
import com.hindu.cunow.Fragments.Pages.PagesTabActivity
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.explore_fragment.view.*
import kotlinx.android.synthetic.main.tag_item_layout.view.*
import kotlin.collections.ArrayList

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
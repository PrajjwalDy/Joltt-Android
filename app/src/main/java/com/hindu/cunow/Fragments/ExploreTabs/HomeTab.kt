package com.hindu.cunow.Fragments.ExploreTabs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.hindu.cunow.Activity.FeedbackActivity
import com.hindu.cunow.Fragments.Circle.CircleTabActivity
import com.hindu.cunow.Fragments.Pages.PagesTabActivity
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.fragment_home_tab.view.*

class HomeTab : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root:View = inflater.inflate(R.layout.fragment_home_tab, container, false)


        root.ll_confessionRoom.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_dashboard_to_confessionRoomFragment)
        }

        root.circle_ll.setOnClickListener {
            val intent = Intent(context, CircleTabActivity::class.java)
            startActivity(intent)
        }
        root.feedback.setOnClickListener {
            val intent = Intent(context, FeedbackActivity::class.java)
            startActivity(intent)
        }

        root.publicPost.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_dashboard_to_publicPostFragement)
        }

        root.explore_people.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_dashboard_to_peopleFragment)
        }

        root.academicsNow.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_dashboard_to_academicsFragment)
        }

        root.trending.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_dashboard_to_hackathonsFragment)
        }

        root.internships.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_dashboard_to_internshipsFragment)
        }

        root.pages.setOnClickListener {
            val intent = Intent(context, PagesTabActivity::class.java)
            startActivity(intent)
        }

        return root
    }

}
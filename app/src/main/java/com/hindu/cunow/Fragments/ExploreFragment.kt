package com.hindu.cunow.Fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.explore_fragment.view.*

class ExploreFragment : Fragment() {

    companion object {
        fun newInstance() = ExploreFragment()
    }

    private lateinit var viewModel: ExploreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View= inflater.inflate(R.layout.explore_fragment, container, false)


        root.ll_confessionRoom.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_dashboard_to_confessionRoomFragment)
        }

        return root
    }
}
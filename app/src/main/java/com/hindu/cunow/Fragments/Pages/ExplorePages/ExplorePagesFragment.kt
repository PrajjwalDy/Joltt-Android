package com.hindu.cunow.Fragments.Pages.ExplorePages

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hindu.cunow.R

class ExplorePagesFragment : Fragment() {

    companion object {
        fun newInstance() = ExplorePagesFragment()
    }

    private lateinit var viewModel: ExplorePagesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.explore_pages_fragment, container, false)
    }


}
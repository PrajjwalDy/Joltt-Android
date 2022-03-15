package com.hindu.cunow.Fragments.Circle.JoinedCircles

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hindu.cunow.R

class JoinedCirclesFragment : Fragment() {

    companion object {
        fun newInstance() = JoinedCirclesFragment()
    }

    private lateinit var viewModel: JoinedCirclesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.joined_circles_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(JoinedCirclesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
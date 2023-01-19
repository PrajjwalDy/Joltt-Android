package com.hindu.cunow.Fragments.Hackathons

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hindu.cunow.R

class HackathonsFragment : Fragment() {

    companion object {
        fun newInstance() = HackathonsFragment()
    }

    private lateinit var viewModel: HackathonsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hackathons, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HackathonsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
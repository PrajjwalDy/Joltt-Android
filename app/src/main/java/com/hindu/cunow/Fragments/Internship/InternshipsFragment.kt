package com.hindu.cunow.Fragments.Internship

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hindu.cunow.R

class InternshipsFragment : Fragment() {

    companion object {
        fun newInstance() = InternshipsFragment()
    }

    private lateinit var viewModel: InternshipsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_internships, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InternshipsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
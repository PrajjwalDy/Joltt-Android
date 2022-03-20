package com.hindu.cunow.Fragments.AboutMe.MySaved

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hindu.cunow.R

class MySavedFragment : Fragment() {

    companion object {
        fun newInstance() = MySavedFragment()
    }

    private lateinit var viewModel: MySavedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_saved_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MySavedViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
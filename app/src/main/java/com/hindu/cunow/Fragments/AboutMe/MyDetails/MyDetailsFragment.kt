package com.hindu.cunow.Fragments.AboutMe.MyDetails

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hindu.cunow.R

class MyDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = MyDetailsFragment()
    }

    private lateinit var viewModel: MyDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
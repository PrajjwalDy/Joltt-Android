package com.hindu.cunow.Fragments.Pages.MyPages

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hindu.cunow.R

class MyPagesFragment : Fragment() {

    companion object {
        fun newInstance() = MyPagesFragment()
    }

    private lateinit var viewModel: MyPagesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_pages_fragment, container, false)
    }


}
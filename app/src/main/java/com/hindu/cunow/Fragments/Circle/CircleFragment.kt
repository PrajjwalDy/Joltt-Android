package com.hindu.cunow.Fragments.Circle

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hindu.cunow.Activity.CreateCircle
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_create_circle.view.*
import kotlinx.android.synthetic.main.circle_fragment.view.*

class CircleFragment : Fragment() {

    companion object {
        fun newInstance() = CircleFragment()
    }

    private lateinit var viewModel: CircleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.circle_fragment, container, false)


        root.create_circle.setOnClickListener {
            startActivity(Intent(context,CreateCircle::class.java))
        }
        return root
    }


}
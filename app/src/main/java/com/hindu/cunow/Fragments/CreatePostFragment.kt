package com.hindu.cunow.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hindu.cunow.R

class CreatePostFragment : Fragment() {


    private lateinit var viewModel: CreatePostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.create_post_fragment, container, false)

        /*val intent = Intent(context,VibesActivity::class.java)
        startActivity(intent)*/

        return root
    }


}
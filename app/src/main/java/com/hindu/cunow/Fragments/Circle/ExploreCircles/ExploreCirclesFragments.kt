package com.hindu.cunow.Fragments.Circle.ExploreCircles

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Activity.CreateCircle
import com.hindu.cunow.Adapter.CircleAdapter
import com.hindu.cunow.R
import com.hindu.cunow.databinding.ExploreCirclesFragmentsFragmentBinding
import kotlinx.android.synthetic.main.explore_circles_fragments_fragment.view.*

class ExploreCirclesFragments : Fragment() {

    var recyclerView: RecyclerView? = null
    private var circleAdapter: CircleAdapter? = null

    private var _binding: ExploreCirclesFragmentsFragmentBinding? =null

    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ExploreCirclesFragments()
    }

    private lateinit var viewModel: ExploreCirclesFragmentsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ExploreCirclesFragmentsViewModel::class.java)
        _binding = ExploreCirclesFragmentsFragmentBinding.inflate(inflater,container,false)
        val root:View = binding.root
        initView(root)

        viewModel.circleViewModel!!.observe(viewLifecycleOwner, Observer {
            circleAdapter = context?.let { it1-> CircleAdapter(it1,it) }
            recyclerView!!.adapter = circleAdapter
            circleAdapter!!.notifyDataSetChanged()
        })


        root.create_circle.setOnClickListener {
            startActivity(Intent(context, CreateCircle::class.java))
        }
        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.circle_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context,3)
        recyclerView!!.layoutManager = linearLayoutManager

    }

}
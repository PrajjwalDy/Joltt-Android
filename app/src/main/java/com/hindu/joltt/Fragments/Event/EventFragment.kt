package com.hindu.joltt.Fragments.Event

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentEventBinding
import com.hindu.joltt.Activity.AddEventActivity
import com.hindu.joltt.Adapter.EventAdapter
import kotlinx.android.synthetic.main.fragment_event.view.addEvent_button
import kotlinx.android.synthetic.main.fragment_event.view.eventBack
import kotlinx.android.synthetic.main.fragment_event.view.eventTxt

class EventFragment : Fragment() {
    var recyclerView:RecyclerView? = null
    private var eventAdapter: EventAdapter? = null
    private lateinit var viewModel: EventViewModel
    private var _binding:FragmentEventBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        _binding = FragmentEventBinding.inflate(inflater,container,false)
        val root:View = binding.root
        viewModel.eventModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            eventAdapter = context?.let { it1-> EventAdapter(it1,it) }
            recyclerView!!.adapter = eventAdapter
            eventAdapter!!.notifyDataSetChanged()
        })

        root.addEvent_button.setOnClickListener {
            startActivity(Intent(context, AddEventActivity::class.java))
        }
        root.eventBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_eventFragment_to_navigation_dashboard)
        }
        root.eventTxt.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_eventFragment_to_navigation_dashboard)
        }
        return root
    }
    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.event_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManger = LinearLayoutManager(context)
        linearLayoutManger.reverseLayout = true
        linearLayoutManger.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManger
    }
}
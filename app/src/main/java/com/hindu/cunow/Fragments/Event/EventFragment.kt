package com.hindu.cunow.Fragments.Event

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Activity.AddEventActivity
import com.hindu.cunow.Activity.FeedbackActivity
import com.hindu.cunow.Adapter.EventAdapter
import com.hindu.cunow.Fragments.Internship.InternshipAdapter
import com.hindu.cunow.Fragments.Internship.InternshipsViewModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentEventBinding
import com.hindu.cunow.databinding.FragmentInternshipsBinding
import kotlinx.android.synthetic.main.fragment_event.view.*

class EventFragment : Fragment() {
    var recyclerView:RecyclerView? = null
    private var eventAdapter:EventAdapter? = null
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
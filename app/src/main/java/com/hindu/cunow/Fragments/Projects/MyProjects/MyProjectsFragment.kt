package com.hindu.cunow.Fragments.Projects.MyProjects

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Adapter.ProjectAdapter
import com.hindu.cunow.Fragments.Projects.AllProjects.AllProjectsViewModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentAllProjectsBinding
import com.hindu.cunow.databinding.FragmentMyProjectsBinding

class MyProjectsFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private var projectAdapter: ProjectAdapter? = null
    private lateinit var viewModel: MyProjectsViewModel
    private var _binding: FragmentMyProjectsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(MyProjectsViewModel::class.java)
        _binding = FragmentMyProjectsBinding.inflate(inflater,container,false)
        val root:View = binding.root

        viewModel.projectModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            projectAdapter = context?.let { it1-> ProjectAdapter(it1,it) }
            recyclerView!!.adapter = projectAdapter
            projectAdapter!!.notifyDataSetChanged()
        })

        return root
    }

    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.myProjects_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManger = LinearLayoutManager(context)
        linearLayoutManger.reverseLayout = true
        linearLayoutManger.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManger
    }
}
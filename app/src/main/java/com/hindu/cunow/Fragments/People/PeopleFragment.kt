
package com.hindu.cunow.Fragments.People

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
import com.hindu.cunow.Adapter.ExploreUserAdapter
import com.hindu.cunow.Adapter.PostAdapter
import com.hindu.cunow.R
import com.hindu.cunow.databinding.ExplorePeopleLayoutBinding
import com.hindu.cunow.databinding.FragmentHomeBinding
import com.hindu.cunow.databinding.PeopleFragmentBinding
import kotlinx.android.synthetic.main.people_fragment.view.*

class PeopleFragment : Fragment() {

    var recyclerView:RecyclerView? = null
    private var exploreUserAdapter:ExploreUserAdapter? = null
    private var _binding:PeopleFragmentBinding? =null
    private val binding get() = _binding!!

    private lateinit var viewModel: PeopleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(PeopleViewModel::class.java)

        _binding = PeopleFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.userModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            exploreUserAdapter = context?.let { it1-> ExploreUserAdapter(it1,it) }
            recyclerView!!.adapter = exploreUserAdapter
            exploreUserAdapter!!.notifyDataSetChanged()

        })

        root.exploreBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_peopleFragment_to_navigation_dashboard)
        }
        root.exploreTxt.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_peopleFragment_to_navigation_dashboard)
        }

        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.explorePeople_rv) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }

}
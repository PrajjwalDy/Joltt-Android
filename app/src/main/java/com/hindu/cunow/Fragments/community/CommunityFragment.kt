
package com.hindu.cunow.Fragments.community

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.Adapter.CommunityAdapter
import com.hindu.cunow.Adapter.HackathonAdapter
import com.hindu.cunow.Fragments.Hackathons.HackathonsViewModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentCommunityBinding
import com.hindu.cunow.databinding.FragmentHackathonsBinding
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.add_community_post_dialog.view.*
import kotlinx.android.synthetic.main.fragment_community.com_editText
import kotlinx.android.synthetic.main.fragment_community.com_post
import kotlinx.android.synthetic.main.fragment_community.create_query_CV
import kotlinx.android.synthetic.main.fragment_community.view.*

class CommunityFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private lateinit var viewModel: CommunityViewModel
    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private var communityAdapter: CommunityAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(CommunityViewModel::class.java)
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.communityModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            communityAdapter = context?.let { it1 -> CommunityAdapter(it1, it) }
            recyclerView!!.adapter = communityAdapter
            communityAdapter!!.notifyDataSetChanged()
        })

        root.com_post.setOnClickListener {
            addCommunityPost(root)
        }

        root.add_button_commnunity.setOnClickListener {
            root.create_query_CV.visibility = View.VISIBLE
        }


        root.close_ccp.setOnClickListener {
            root.create_query_CV.visibility = View.GONE
        }

        root.communityBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_communityFragment_to_navigation_dashboard3)
        }
        root.communityTxt.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_communityFragment_to_navigation_dashboard3)
        }

        return root
    }


    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.community_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }

    private fun addCommunityPost(root: View) {
        if (com_editText.text.isEmpty()) {
            Snackbar.make(root, "please write something..", Snackbar.LENGTH_SHORT).show()
        } else {
            val dataRef = FirebaseDatabase.getInstance().reference
                .child("Community")

            val commentId = dataRef.push().key
            val dataMap = HashMap<String, Any>()
            dataMap["communityId"] = commentId!!
            dataMap["communityCaption"] = com_editText.text.toString()
            dataMap["communityPublisher"] = FirebaseAuth.getInstance().currentUser!!.uid

            dataRef.child(commentId).updateChildren(dataMap)
            com_editText.text.clear()
            root.create_query_CV.visibility = View.GONE
        }
    }

}
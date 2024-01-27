
package com.hindu.joltt.Fragments.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentCommunityBinding
import com.hindu.joltt.Adapter.CommunityAdapter

class CommunityFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private lateinit var viewModel: CommunityViewModel
    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private var communityAdapter: CommunityAdapter? = null

    private lateinit var com_post:AppCompatButton
    private lateinit var add_button_commnunity:Button
    private lateinit var close_ccp:ImageView
    private lateinit var communityBack:ImageView
    private lateinit var communityTxt:TextView
    private lateinit var create_query_CV:CardView
    private lateinit var com_editText:EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(CommunityViewModel::class.java)
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        val root: View = binding.root


        com_post = root.findViewById(R.id.com_post)
        add_button_commnunity = root.findViewById(R.id.add_button_commnunity)
        close_ccp = root.findViewById(R.id.close_ccp)
        communityBack = root.findViewById(R.id.communityBack)
        communityTxt = root.findViewById(R.id.communityTxt)
        create_query_CV = root.findViewById(R.id.create_query_CV)
        com_post = root.findViewById(R.id.com_post)
        com_editText = root.findViewById(R.id.com_editText)


        viewModel.communityModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            communityAdapter = context?.let { it1 -> CommunityAdapter(it1, it) }
            recyclerView!!.adapter = communityAdapter
            communityAdapter!!.notifyDataSetChanged()
        })

        com_post.setOnClickListener {
            addCommunityPost(root)
        }

        add_button_commnunity.setOnClickListener {
            create_query_CV.visibility = View.VISIBLE
        }


        close_ccp.setOnClickListener {
            create_query_CV.visibility = View.GONE
        }

        communityBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_communityFragment_to_navigation_dashboard3)
        }
        communityTxt.setOnClickListener {
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
            create_query_CV.visibility = View.GONE
        }
    }

}
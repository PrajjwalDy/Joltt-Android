package com.hindu.cunow.Fragments.Chat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Adapter.ChatListAdapter
import com.hindu.cunow.R
import com.hindu.cunow.databinding.ChatFragmentBinding
import com.hindu.cunow.databinding.PeopleFragmentBinding

class ChatFragment : Fragment() {
    var recyclerView:RecyclerView? = null
    private var chatListAdapter:ChatListAdapter? = null
    private var _binding:ChatFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //homeViewModel =
        //            ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        _binding = ChatFragmentBinding.inflate(inflater,container,false)
        val root:View = binding.root

        viewModel.chatListModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            chatListAdapter = context?.let { it1-> ChatListAdapter(it1,it) }
            recyclerView!!.adapter = chatListAdapter
            chatListAdapter!!.notifyDataSetChanged()
        })



        return root

    }

    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.chatList_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }


}
package com.hindu.joltt.Fragments.Chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.hindu.cunow.R
import com.hindu.cunow.databinding.ChatFragmentBinding
import com.hindu.joltt.Adapter.ChatListAdapter

class ChatFragment : Fragment() {
    var recyclerView:RecyclerView? = null
    private var chatListAdapter: ChatListAdapter? = null
    private var _binding:ChatFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ChatViewModel

    private lateinit var noChatsLayout:LinearLayout
    private lateinit var chatList_RV:RecyclerView
    private lateinit var addChat:FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //homeViewModel =
        //            ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        _binding = ChatFragmentBinding.inflate(inflater,container,false)
        val root:View = binding.root


        noChatsLayout = root.findViewById(R.id.noChatsLayout)
        chatList_RV = root.findViewById(R.id.chatList_RV)
        addChat = root.findViewById(R.id.addChat)



        viewModel.chatListModel!!.observe(viewLifecycleOwner, Observer {chatList->
            if (chatList.isNullOrEmpty()){
                noChatsLayout?.visibility = View.VISIBLE
                chatList_RV?.visibility = View.GONE
            }else{
                noChatsLayout?.visibility = View.GONE
                chatList_RV?.visibility = View.VISIBLE
                initView(root)
                chatListAdapter = context?.let { it1-> ChatListAdapter(it1,chatList) }
                recyclerView!!.adapter = chatListAdapter
                chatListAdapter!!.notifyDataSetChanged()
            }

        })

        addChat.setOnClickListener{
            val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref!!.putString("uid",FirebaseAuth.getInstance().currentUser!!.uid)
            pref.putString("title","Chat")
            pref.apply()
            Navigation.findNavController(root).navigate(R.id.action_chatFragment_to_showUserFragment)
        }

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
package com.hindu.joltt.Fragments.Chat

import android.content.Context
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
import com.google.firebase.auth.FirebaseAuth
import com.hindu.cunow.R
import com.hindu.cunow.databinding.ChatFragmentBinding
import com.hindu.joltt.Adapter.ChatListAdapter
import kotlinx.android.synthetic.main.chat_fragment.view.addChat
import kotlinx.android.synthetic.main.chat_fragment.view.chatList_RV
import kotlinx.android.synthetic.main.chat_fragment.view.noChatsLayout

class ChatFragment : Fragment() {
    var recyclerView:RecyclerView? = null
    private var chatListAdapter: ChatListAdapter? = null
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

        viewModel.chatListModel!!.observe(viewLifecycleOwner, Observer {chatList->
            if (chatList.isNullOrEmpty()){
                root.noChatsLayout?.visibility = View.VISIBLE
                root.chatList_RV?.visibility = View.GONE
            }else{
                root.noChatsLayout?.visibility = View.GONE
                root.chatList_RV?.visibility = View.VISIBLE
                root.noChatsLayout.visibility = View.VISIBLE
                initView(root)
                chatListAdapter = context?.let { it1-> ChatListAdapter(it1,chatList) }
                recyclerView!!.adapter = chatListAdapter
                chatListAdapter!!.notifyDataSetChanged()
            }

        })

        root.addChat.setOnClickListener{
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
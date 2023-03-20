package com.hindu.cunow.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.NotificationAdapter
import com.hindu.cunow.Model.NotificationModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentNotificationsBinding
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {

    var recyclerView:RecyclerView? = null
    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    private var notificationList: List<NotificationModel>? = null
    private var notificationAdapter: NotificationAdapter? = null

    private val binding get() = _binding!!

    //Pagination
    private lateinit var database:DatabaseReference
    //private lateinit var adapter: NotificationAdapter
    private lateinit var data: MutableList<NotificationModel>
    private var lastLoadedItemName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        notificationsViewModel.notificationViewModel.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            initView(root)
            notificationAdapter = context?.let { it1-> NotificationAdapter(it1,it) }
            recyclerView!!.adapter = notificationAdapter
            notificationAdapter!!.notifyDataSetChanged()
            CoroutineScope(Dispatchers.IO).launch {
                markAllAsRead()
            }
        })

        /*database = FirebaseDatabase.getInstance().reference
            .child("Notification")
            .child("AllNotification")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        data = mutableListOf()

        adapter = NotificationAdapter(requireContext(),data)

        recyclerView = root.findViewById(R.id.notificationRecycler) as RecyclerView
        recyclerView!!.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition == data.size-1){
                    CoroutineScope(Dispatchers.IO).launch{
                        loadMoreData()
                    }
                }
            }
        })
        recyclerView!!.adapter =adapter
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        CoroutineScope(Dispatchers.IO).launch{
            launch { loadInitialData() }
            launch { markAllAsRead() }
        }*/

        root.followRequest.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_notifications_to_followRequest2)
        }

        return root
    }

    private  fun initView(root:View){

        recyclerView = root.findViewById(R.id.notificationRecycler) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }

   /* private fun loadInitialData(){
        database.orderByKey().limitToLast(10).get().addOnSuccessListener { snapshot->
            val newData = mutableListOf<NotificationModel>()
            snapshot.children.forEach { child->
                val item = child.getValue(NotificationModel::class.java)
                if (item != null){
                    data.add(item)
                    lastLoadedItemName = item.notificationId
                }
            }
            data.addAll(0,newData.reversed())
            adapter.notifyDataSetChanged()
        }
    }
    private suspend fun loadMoreData(){
        if (lastLoadedItemName == null) {
            return
        }

        val lastItem = data.lastOrNull()
        if (lastItem != null){
            database.orderByKey().endBefore(lastLoadedItemName)
                .limitToLast(10).get().addOnSuccessListener { snapshot->
                val newData = mutableListOf<NotificationModel>()
                snapshot.children.forEach{child->
                    val item = child.getValue(NotificationModel::class.java)
                    if (item != null){
                        data.add(item)
                        lastLoadedItemName = item.notificationId
                    }
                }
                data.addAll(0,newData.reversed())
                adapter.notifyDataSetChanged()
            }
        }
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun markAllAsRead(){
        val notification = FirebaseDatabase.getInstance().reference
            .child("Notification")
            .child("UnReadNotification")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .removeValue()
    }

}
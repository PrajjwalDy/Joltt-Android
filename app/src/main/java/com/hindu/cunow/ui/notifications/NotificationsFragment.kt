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
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentNotificationsBinding
import kotlinx.android.synthetic.main.fragment_notifications.followRequest
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
                launch { markAllAsRead() }
            }
        })

        root.followRequest.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_notifications_to_followRequest2)
        }

        CoroutineScope(Dispatchers.IO).launch {
            checkPrivacy()
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

    private fun checkPrivacy(){
        val db = FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        db.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(UserModel::class.java)
                if (!data!!.private){
                    followRequest.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}
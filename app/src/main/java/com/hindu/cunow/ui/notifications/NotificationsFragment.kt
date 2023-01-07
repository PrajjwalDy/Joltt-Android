package com.hindu.cunow.ui.notifications

import android.app.Dialog
import android.content.Intent
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.NotificationAdapter
import com.hindu.cunow.Model.NotificationModel
import com.hindu.cunow.PushNotification.Token
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentNotificationsBinding
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class NotificationsFragment : Fragment() {

    var recyclerView:RecyclerView? = null
    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    private var notificationList: List<NotificationModel>? = null
    private var notificationAdapter: NotificationAdapter? = null
    private val binding get() = _binding!!

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
        })

        markAllAsRead()

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








    /*private fun readNotification() {
        val progressDialog = context?.let { Dialog(it) }
        progressDialog!!.setContentView(R.layout.profile_dropdown_menu)
        progressDialog.show()
        val dataRef = FirebaseDatabase.getInstance().reference.child("Notification")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (notificationList as ArrayList<NotificationModel>).clear()
                    for (snapshot in snapshot.children){
                        val data = snapshot.getValue(NotificationModel::class.java)
                        (notificationList as ArrayList<NotificationModel>).add(data!!)

                        Collections.reverse(notificationList)
                        notificationAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        progressDialog.dismiss()
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
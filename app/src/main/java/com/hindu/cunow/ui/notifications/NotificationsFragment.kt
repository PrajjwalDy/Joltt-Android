package com.hindu.cunow.ui.notifications

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import java.util.*
import kotlin.collections.ArrayList

class NotificationsFragment : Fragment() {

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

        val recyclerView: RecyclerView = root.findViewById(R.id.notificationRecycler) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        notificationList = ArrayList()
        notificationAdapter = context?.let { NotificationAdapter(it,notificationList as ArrayList<NotificationModel>) }
        recyclerView.adapter = notificationAdapter

        readNotification()


        return root
    }

    private fun readNotification() {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
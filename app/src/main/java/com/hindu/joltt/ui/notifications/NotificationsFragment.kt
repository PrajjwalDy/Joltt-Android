package com.hindu.joltt.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentNotificationsBinding
import com.hindu.joltt.Model.NotificationModel
import com.hindu.joltt.Model.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {

    var recyclerView:RecyclerView? = null
    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    private var notificationList: MutableList<NotificationModel>? = null
    private var notificationAdapter: com.hindu.joltt.Adapter.NotificationAdapter? = null


    private val binding get() = _binding!!

    //Pagination
    private lateinit var database:DatabaseReference
    //private lateinit var adapter: NotificationAdapter
    private lateinit var data: MutableList<NotificationModel>
    private var lastLoadedItemName: String? = null

    private lateinit var noNotificationLayout:LinearLayout
    private lateinit var notificationRecycler:RecyclerView
    private lateinit var followRequest:TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        noNotificationLayout = root.findViewById(R.id.noNotificationLayout)
        notificationRecycler = root.findViewById(R.id.notificationRecycler)
        followRequest = root.findViewById(R.id.followRequest)





        notificationsViewModel.notificationViewModel.observe(viewLifecycleOwner, androidx.lifecycle.Observer {notificationList->

            if (notificationList.isNullOrEmpty()){
                noNotificationLayout.visibility = View.VISIBLE
                notificationRecycler.visibility= View.GONE
            }else{
                noNotificationLayout.visibility = View.GONE
                notificationRecycler.visibility= View.VISIBLE
                initView(root)
                notificationAdapter = context?.let { it1-> com.hindu.joltt.Adapter.NotificationAdapter(it1,notificationList) }
                recyclerView!!.adapter = notificationAdapter
                notificationAdapter!!.notifyDataSetChanged()
                CoroutineScope(Dispatchers.IO).launch {
                    launch { markAllAsRead() }
                }
            }

        })

        followRequest.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_notifications_to_followRequest2)
        }

        CoroutineScope(Dispatchers.IO).launch {
            checkPrivacy()
            launch { loadNotification() }
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

    private suspend fun loadNotification(){
        val notificationData = FirebaseDatabase.getInstance().reference.child("Notification").child("AllNotification")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        notificationData.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList?.clear()
                for (snapshot in snapshot.children){
                    val nData = snapshot.getValue(NotificationModel::class.java)
                    notificationList?.add(nData!!)
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}
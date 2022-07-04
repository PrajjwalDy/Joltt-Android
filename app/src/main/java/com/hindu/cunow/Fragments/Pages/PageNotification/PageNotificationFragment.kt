package com.hindu.cunow.Fragments.Pages.PageNotification

import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Context
import com.hindu.cunow.Adapter.PageNotificationAdapter
import com.hindu.cunow.Adapter.PostAdapter
import com.hindu.cunow.Model.PageNotificationModel
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.R

class PageNotificationFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var pageNotificationAdapter:PageNotificationAdapter? = null
    private var mNotificationList:MutableList<PageNotificationModel>? = null
    private lateinit var pageId:String


    private lateinit var viewModel: PageNotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.fragment_page_notification, container, false)

        val pref = context?.getSharedPreferences("PREFS", android.content.Context.MODE_PRIVATE)
        if (pref != null){
            this.pageId = pref.getString("pageId","none")!!
        }
        println("this is pageId: $pageId")

        recyclerView = root.findViewById(R.id.pageNotification_RV)
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView!!.setHasFixedSize(true)


        mNotificationList = ArrayList()
        pageNotificationAdapter = context?.let { PageNotificationAdapter(it,mNotificationList as ArrayList<PageNotificationModel>) }
        recyclerView?.adapter = pageNotificationAdapter
        retrieveNotification()

        return root
    }
    private fun retrieveNotification(){
        val ref = FirebaseDatabase
            .getInstance()
            .reference
            .child("PageNotification")
            .child("AllNotifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(pageId)
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mNotificationList!!.clear()

                for(snapshot in snapshot.children){
                    val notification = snapshot.getValue(PageNotificationModel::class.java)
                    if (notification != null){
                        mNotificationList?.add(notification)
                    }
                }
                pageNotificationAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}
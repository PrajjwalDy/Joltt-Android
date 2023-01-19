package com.hindu.cunow.Fragments.Explore

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.FeedbackActivity
import com.hindu.cunow.Adapter.AboutMeTabsAdapter
import com.hindu.cunow.Adapter.ExploreTabAdapter
import com.hindu.cunow.Adapter.UserAdapter
import com.hindu.cunow.Fragments.Circle.CircleTabActivity
import com.hindu.cunow.Fragments.Pages.PagesTabActivity
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.explore_fragment.view.*
import kotlinx.android.synthetic.main.tag_item_layout.view.*
import kotlin.collections.ArrayList

class ExploreFragment : Fragment() {

    private var recyclerView:RecyclerView? = null
    private var userAdapter:UserAdapter? = null
    private var mUser:MutableList<UserModel>? = null
    private var checker = "Name"

    private lateinit var viewModel: ExploreViewModel

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View= inflater.inflate(R.layout.explore_fragment, container, false)


        recyclerView = root.findViewById(R.id.searchUserRV)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it,mUser as ArrayList<UserModel>) }
        recyclerView?.adapter = userAdapter

        root.searchWithName.setOnClickListener {
            root.search_edit_text.text.clear()
            checker = "Name"
            root.searchWithUID.background= resources.getDrawable(R.drawable.box_grey)
            root.searchWithName.background= resources.getDrawable(R.drawable.green_button)
            root.searchWithName.setBackgroundDrawable(resources.getDrawable(R.drawable.green_button))
            root.searchWithUID.setTextColor(resources.getColor(R.color.red))
            root.searchWithName.setTextColor(resources.getColor(R.color.white))
        }

        root.searchWithUID.setOnClickListener {
            root.search_edit_text.text.clear()
            checker = "UID"
            root.searchWithName.background= resources.getDrawable(R.drawable.box_grey)
            root.searchWithUID.background= resources.getDrawable(R.drawable.green_button)
            root.searchWithUID.setBackgroundDrawable(resources.getDrawable(R.drawable.green_button))
            root.searchWithUID.setTextColor(resources.getColor(R.color.white))
            root.searchWithName.setTextColor(resources.getColor(R.color.red))
        }

        root.search_edit_text.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                recyclerView?.visibility = View.GONE
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view!!.search_edit_text.text.toString() == ""){

                }
                else{
                    recyclerView?.visibility = View.VISIBLE
                    retrieveUsers()
                    if (checker == "Name"){
                        searchUsers(p0!!.toString())
                    }else if (checker == "UID"){
                        searchWithUID(p0.toString())
                    }else{
                        searchUsers(p0!!.toString())
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }

        })


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = view.findViewById(R.id.explore_tabs_layout)
        viewPager = view.findViewById(R.id.explore_viewPager)

        val adapter = ExploreTabAdapter(childFragmentManager,lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout,viewPager){tab,position->

            when(position){
                0-> {
                    tab.text = "Home"
                }
                1->{
                    tab.text = "Trending"
                }
                2->{
                    tab.text = "Academics"
                }
                3->{
                    tab.text = "Events"
                }
                4->{
                    tab.text = "Post"
                }
                5->{
                    tab.text = "Internships"
                }
                6->{
                    tab.text = "Hackathons"
                }
            }

        }.attach()

    }


    private fun searchUsers(input:String){
        val array = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("searchName")
            .startAt(input)
            .endAt(input +"\uf88f")
        array.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mUser?.clear()

                for (snapshot in snapshot.children){
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null){
                        mUser?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun searchWithUID(input:String){
        val array = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("ID")
            .startAt(input)
            .endAt(input +"\uf88f")
        array.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mUser?.clear()

                for (snapshot in snapshot.children){
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null){
                        mUser?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun retrieveUsers(){
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        userRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (view!!.search_edit_text?.text.toString() == ""){
                    mUser?.clear()
                    for (snapshot in snapshot.children){
                        val user = snapshot.getValue(UserModel::class.java)
                        if (user != null){
                            mUser?.add(user)
                        }
                    }
                userAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}
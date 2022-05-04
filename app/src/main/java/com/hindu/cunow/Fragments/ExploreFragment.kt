package com.hindu.cunow.Fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.DocumentsContract
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.FeedbackActivity
import com.hindu.cunow.Adapter.UserAdapter
import com.hindu.cunow.Fragments.Circle.CircleTabActivity
import com.hindu.cunow.Fragments.Pages.PagesTabActivity
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.explore_fragment.view.*
import java.util.*
import kotlin.collections.ArrayList

class ExploreFragment : Fragment() {

    private var recyclerView:RecyclerView? = null
    private var userAdapter:UserAdapter? = null
    private var mUser:MutableList<UserModel>? = null

    private lateinit var viewModel: ExploreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View= inflater.inflate(R.layout.explore_fragment, container, false)


        root.ll_confessionRoom.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_dashboard_to_confessionRoomFragment)
        }

        root.circle_ll.setOnClickListener {
            val intent = Intent(context, CircleTabActivity::class.java)
            startActivity(intent)

        }
        root.feedback.setOnClickListener {
            val intent = Intent(context, FeedbackActivity::class.java)
            startActivity(intent)
        }

        root.publicPost.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_dashboard_to_publicPostFragement)
        }

        root.explore_people.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_dashboard_to_peopleFragment)
        }

        recyclerView = root.findViewById(R.id.searchUserRV)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it,mUser as ArrayList<UserModel>) }
        recyclerView?.adapter = userAdapter


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
                    searchUsers(p0!!.toString())

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        root.pages.setOnClickListener {
            val intent = Intent(context,PagesTabActivity::class.java)
            startActivity(intent)
        }


        return root
    }

    private fun searchUsers(input:String){
        val array = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("searchName")
            .startAt(input)
            .endAt(input +"\uf88f")
        array.addListenerForSingleValueEvent(object :ValueEventListener{
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
package com.hindu.joltt.Fragments.ExploreTabs

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Activity.FeedbackActivity
import com.hindu.joltt.Adapter.UserAdapter
import com.hindu.joltt.Fragments.Pages.PagesTabActivity
import com.hindu.joltt.Model.UserModel

class HomeTab : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<UserModel>? = null
    private var checker = "Name"

    private lateinit var ll_confessionRoom:LinearLayout
    private lateinit var ll_post:LinearLayout
    private lateinit var ll_events:LinearLayout
    private lateinit var ll_study_abroad:LinearLayout
    private lateinit var ll_govt_schemes:LinearLayout
    private lateinit var ll_clubs:LinearLayout
    private lateinit var ll_community:LinearLayout
    private lateinit var ll_pages:LinearLayout
    private lateinit var ll_projects:LinearLayout
    private lateinit var ll_courses:LinearLayout
    private lateinit var ll_people:LinearLayout
    private lateinit var ll_feedback:LinearLayout
    private lateinit var search_edit_text:EditText






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_home_tab, container, false)

        ll_confessionRoom = root.findViewById(R.id.ll_confessionRoom)
        ll_post = root.findViewById(R.id.ll_post)
        ll_events = root.findViewById(R.id.ll_events)
        ll_study_abroad = root.findViewById(R.id.ll_study_abroad)
        ll_govt_schemes = root.findViewById(R.id.ll_govt_schemes)
        ll_clubs = root.findViewById(R.id.ll_clubs)
        ll_community = root.findViewById(R.id.ll_community)
        ll_pages = root.findViewById(R.id.ll_pages)
        ll_projects = root.findViewById(R.id.ll_projects)
        ll_courses = root.findViewById(R.id.ll_courses)
        ll_people = root.findViewById(R.id.ll_people)
        ll_feedback = root.findViewById(R.id.ll_feedback)
        search_edit_text = root.findViewById(R.id.search_edit_text)


        ll_confessionRoom.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_confessionRoomFragment)
        }


        recyclerView = root.findViewById(R.id.searchUserR)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<UserModel>) }
        recyclerView?.adapter = userAdapter


        search_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                recyclerView?.visibility = View.GONE
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (search_edit_text.text.toString() == "") {

                } else {
                    recyclerView?.visibility = View.VISIBLE
                    retrieveUsers()
                    searchUsers(p0!!.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        ll_post.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_publicPostFragement)
        }
        ll_events.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_communityFragment)
        }
        ll_study_abroad.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_abroadFragment)
        }
        ll_govt_schemes.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_schemesFragment)
        }
        ll_clubs.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_jobsFragment)
        }
        ll_community.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_flash)
        }
        ll_pages.setOnClickListener {
            startActivity(Intent(context, PagesTabActivity::class.java))
        }
        ll_projects.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_eventFragment)
        }
        ll_courses.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_coursesFragment)
        }
        ll_people.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_peopleFragment)
        }
        ll_feedback.setOnClickListener {
            startActivity(Intent(context, FeedbackActivity::class.java))
        }

        return root
    }
    private fun searchUsers(input: String) {
        val array = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("searchName")
            .startAt(input)
            .endAt(input + "\uf88f")
        array.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mUser?.clear()

                for (snapshot in snapshot.children) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
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
    private fun retrieveUsers() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (search_edit_text?.text.toString() == "") {
                    mUser?.clear()
                    for (snapshot in snapshot.children) {
                        val user = snapshot.getValue(UserModel::class.java)
                        if (user != null) {
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
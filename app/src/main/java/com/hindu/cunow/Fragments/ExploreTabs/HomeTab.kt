package com.hindu.cunow.Fragments.ExploreTabs

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.AddPostActivity
import com.hindu.cunow.Activity.FeedbackActivity
import com.hindu.cunow.Adapter.UserAdapter
import com.hindu.cunow.Fragments.Pages.PagesTabActivity
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.filter_dialog_box.view.*
import kotlinx.android.synthetic.main.fragment_home_tab.view.*

class HomeTab : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<UserModel>? = null
    private var checker = "Name"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_home_tab, container, false)
        root.ll_confessionRoom.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_confessionRoomFragment)
        }


        recyclerView = root.findViewById(R.id.searchUserR)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<UserModel>) }
        recyclerView?.adapter = userAdapter

       root.filter.setOnClickListener {
           val dialogView = LayoutInflater.from(context).inflate(R.layout.filter_dialog_box, null)

           val dialogBuilder = AlertDialog.Builder(context)
               .setView(dialogView)

           val alertDialog = dialogBuilder.show()
           dialogView.with_name.setOnClickListener {
               checker="Name"
               alertDialog.dismiss()
           }
           dialogView.with_UID.setOnClickListener {
               checker = "UID"
               alertDialog.dismiss()
           }

       }

        root.search_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                recyclerView?.visibility = View.GONE
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view!!.search_edit_text.text.toString() == "") {

                } else {
                    recyclerView?.visibility = View.VISIBLE
                    retrieveUsers()
                    if (checker == "Name") {
                        searchUsers(p0!!.toString())
                    } else if (checker == "UID") {
                        searchWithUID(p0.toString())
                    } else {
                        searchUsers(p0!!.toString())
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        root.ll_post.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_publicPostFragement)
        }
        root.ll_events.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_eventFragment)
        }
        root.ll_study_abroad.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_abroadFragment)
        }
        root.ll_govt_schemes.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_schemesFragment)
        }
        root.ll_clubs.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_clubsFragment)
        }
        root.ll_community.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_communityFragment)
        }
        root.ll_pages.setOnClickListener {
            startActivity(Intent(context, PagesTabActivity::class.java))
        }
        root.ll_projects.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_projectsFragment)
        }
        root.ll_courses.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_coursesFragment)
        }
        root.ll_people.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_peopleFragment)
        }
        root.ll_feedback.setOnClickListener {
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

    private fun searchWithUID(input: String) {
        val array = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("ID")
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
                if (view!!.search_edit_text?.text.toString() == "") {
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
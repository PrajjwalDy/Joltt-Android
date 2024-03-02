
package com.hindu.joltt.Fragments.People

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.cunow.databinding.PeopleFragmentBinding
import com.hindu.joltt.Adapter.ExploreUserAdapter
import com.hindu.joltt.Adapter.UserAdapter
import com.hindu.joltt.Model.UserModel

class PeopleFragment : Fragment() {

    var recyclerView:RecyclerView? = null
    private var exploreUserAdapter: ExploreUserAdapter? = null
    private var _binding:PeopleFragmentBinding? =null
    private val binding get() = _binding!!

    private lateinit var viewModel: PeopleViewModel

    private lateinit var exploreBack:ImageView
    private lateinit var exploreTxt:TextView

    private var recyclerView2: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<UserModel>? = null

    private lateinit var search_edit_text: EditText



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(PeopleViewModel::class.java)

        _binding = PeopleFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root


        exploreBack = root.findViewById(R.id.exploreBack)
        search_edit_text = root.findViewById(R.id.search_edit_text)
        recyclerView2 = root.findViewById(R.id.searchUserR)


        viewModel.userModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            exploreUserAdapter = context?.let { it1-> ExploreUserAdapter(it1,it) }
            recyclerView!!.adapter = exploreUserAdapter
            exploreUserAdapter!!.notifyDataSetChanged()

        })

        exploreBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_peopleFragment_to_navigation_dashboard)
        }

        search_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                recyclerView?.visibility = View.GONE
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (search_edit_text.text.toString() == "") {

                } else {
                    recyclerView?.visibility = View.GONE
                    recyclerView2?.visibility = View.VISIBLE
                    retrieveUsers()
                    searchUsers(p0!!.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })


        recyclerView2 = root.findViewById(R.id.searchUserR)
        recyclerView2!!.setHasFixedSize(true)
        recyclerView2!!.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<UserModel>) }
        recyclerView2?.adapter = userAdapter


        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.explorePeople_rv) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
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

}
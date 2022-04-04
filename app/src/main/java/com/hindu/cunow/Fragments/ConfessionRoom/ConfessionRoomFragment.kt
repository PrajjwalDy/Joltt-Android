package com.hindu.cunow.Fragments.ConfessionRoom

import android.app.Dialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.AddConfessionActivity
import com.hindu.cunow.Activity.AddPostActivity
import com.hindu.cunow.Adapter.ConfessionAdapter
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.ConfessionRoomFragmentBinding
import kotlinx.android.synthetic.main.confession_room_fragment.*
import kotlinx.android.synthetic.main.confession_room_fragment.view.*
import kotlinx.android.synthetic.main.fragment_home.*

class ConfessionRoomFragment : Fragment() {

    var recyclerView:RecyclerView? = null
    private var confessionAdapter:ConfessionAdapter? = null

    private lateinit var viewModel: ConfessionRoomViewModel

    private var _binding: ConfessionRoomFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(ConfessionRoomViewModel::class.java)
        _binding = ConfessionRoomFragmentBinding.inflate(inflater,container,false)
        val root: View = binding.root
        initView(root)
        checkFirstVisit()

        viewModel.confessionViewModel!!.observe(viewLifecycleOwner, Observer {

            confessionAdapter = context?.let { it1-> ConfessionAdapter(it1,it) }
            recyclerView!!.adapter = confessionAdapter
            confessionAdapter!!.notifyDataSetChanged()
        })
        root.send_confession.setOnClickListener { view->
            postText(view)
        }
        root.confessionMedia.setOnClickListener {
            startActivity(Intent(context, AddConfessionActivity::class.java))
        }

        root.confess_button.setOnClickListener {
            root.confess_button.visibility = View.GONE
            root.CV_upload_confession.visibility =View.VISIBLE
        }
        root.accept_confession_tnc.setOnClickListener {
            updateVisit(root)
        }

        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.confessionRV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.isNestedScrollingEnabled = false
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun postText(view: View){
        Snackbar.make(view,"adding confession....", Snackbar.LENGTH_SHORT).show()

        val ref = FirebaseDatabase.getInstance().reference.child("Confession")
        val postId = ref.push().key

        val postMap = HashMap<String,Any>()
        postMap["confessionId"] = postId!!
        postMap["confesserId"] = FirebaseAuth.getInstance().currentUser!!.uid
        postMap["confessionText"] = confess_editText.text.toString()
        //postMap["image"] = ""

        ref.child(postId).updateChildren(postMap)

        Snackbar.make(view,"confession added successfully", Snackbar.LENGTH_SHORT).show()

        confess_editText.text.clear()

    }

    private fun checkFirstVisit(){
        val progressDialog = context?.let { Dialog(it) }
        progressDialog!!.setContentView(R.layout.profile_dropdown_menu)
        progressDialog.show()
        val dataRef = FirebaseDatabase
            .getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    if (data!!.confessionVisited){
                        tnc_confessionView.visibility = View.VISIBLE
                    }else{
                        confession_main_ll.visibility = View.VISIBLE
                    }
                }
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun updateVisit(view: View){

        val ref = FirebaseDatabase.getInstance().reference
            .child("Users")

        val postMap = HashMap<String,Any>()
        postMap["confessionVisited"] = false
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .updateChildren(postMap)

        checkFirstVisit()
    }

}
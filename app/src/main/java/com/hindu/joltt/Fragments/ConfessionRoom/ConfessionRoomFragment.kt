package com.hindu.joltt.Fragments.ConfessionRoom

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.cunow.databinding.ConfessionRoomFragmentBinding
import com.hindu.joltt.Activity.TermsAndCondition
import com.hindu.joltt.Adapter.ConfessionAdapter
import com.hindu.joltt.Model.UserModel

class ConfessionRoomFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private var confessionAdapter: ConfessionAdapter? = null

    private lateinit var viewModel: ConfessionRoomViewModel

    private var _binding: ConfessionRoomFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var send_confession:ImageView
    private lateinit var button_confession:FloatingActionButton
    private lateinit var CV_upload_confession:CardView

    private lateinit var accept_confession_tnc:AppCompatButton
    private lateinit var terms_condition_confession:TextView
    private lateinit var confess_editText:EditText
    private lateinit var confessionBack:ImageView
    private lateinit var confessionTxt:TextView
    private lateinit var tnc_confessionView:LinearLayout
    private lateinit var confession_main_ll:RelativeLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(ConfessionRoomViewModel::class.java)
        _binding = ConfessionRoomFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root



        send_confession = root.findViewById(R.id.send_confession)
        button_confession = root.findViewById(R.id.button_confession)
        CV_upload_confession = root.findViewById(R.id.CV_upload_confession)
        accept_confession_tnc = root.findViewById(R.id.accept_confession_tnc)
        terms_condition_confession = root.findViewById(R.id.terms_condition_confession)
        confess_editText = root.findViewById(R.id.confess_editText)
        confessionTxt = root.findViewById(R.id.confessionTxt)
        tnc_confessionView = root.findViewById(R.id.tnc_confessionView)
        confession_main_ll = root.findViewById(R.id.confession_main_ll)
        confessionBack = root.findViewById(R.id.confessionBack)


        initView(root)
        checkFirstVisit()




        viewModel.confessionViewModel!!.observe(viewLifecycleOwner, Observer {

            confessionAdapter = context?.let { it1 -> ConfessionAdapter(it1, it) }
            recyclerView!!.adapter = confessionAdapter
            confessionAdapter!!.notifyDataSetChanged()
        })
        send_confession.setOnClickListener { view ->
            postText(view)
        }


        button_confession.setOnClickListener {
            button_confession.visibility = View.GONE
            CV_upload_confession.visibility = View.VISIBLE
        }
        accept_confession_tnc.setOnClickListener {
            updateVisit(root)
        }

        terms_condition_confession.setOnClickListener {
            val intent = Intent(context, TermsAndCondition::class.java)
            startActivity(intent)
        }
        confessionBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_confessionRoomFragment_to_navigation_dashboard)
        }
        confessionTxt.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_confessionRoomFragment_to_navigation_dashboard)
        }


        return root
    }

    private fun initView(root: View) {
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

    private fun postText(view: View) {
        Snackbar.make(view, "adding confession....", Snackbar.LENGTH_SHORT).show()

        val ref = FirebaseDatabase.getInstance().reference.child("Confession")
        val postId = ref.push().key

        val postMap = HashMap<String, Any>()
        postMap["confessionId"] = postId!!
        postMap["confesserId"] = FirebaseAuth.getInstance().currentUser!!.uid
        postMap["confessionText"] = confess_editText.text.toString()
        //postMap["image"] = ""

        ref.child(postId).updateChildren(postMap)

        Snackbar.make(view, "confession added successfully", Snackbar.LENGTH_SHORT).show()

        confess_editText.text.clear()
        CV_upload_confession.visibility = View.GONE
        button_confession.visibility = View.VISIBLE

    }

    private fun checkFirstVisit() {
        val progressDialog = context?.let { Dialog(it) }
        progressDialog!!.setContentView(R.layout.profile_dropdown_menu)
        progressDialog.show()
        val dataRef = FirebaseDatabase
            .getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.getValue(UserModel::class.java)
                    if (data!!.confessionVisited) {
                        tnc_confessionView.visibility = View.VISIBLE
                    } else {
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

    private fun updateVisit(view: View) {

        val ref = FirebaseDatabase.getInstance().reference
            .child("Users")

        val postMap = HashMap<String, Any>()
        postMap["confessionVisited"] = false
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .updateChildren(postMap)

        checkFirstVisit()
    }

}
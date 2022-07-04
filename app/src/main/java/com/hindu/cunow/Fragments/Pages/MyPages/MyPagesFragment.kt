package com.hindu.cunow.Fragments.Pages.MyPages

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.PageAdapter
import com.hindu.cunow.Model.PageModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.ExplorePagesFragmentBinding
import com.hindu.cunow.databinding.MyPagesFragmentBinding
import kotlinx.android.synthetic.main.my_pages_fragment.*

class MyPagesFragment : Fragment() {

    var recyclerView:RecyclerView? = null
    private var pageAdapter:PageAdapter? = null

    private lateinit var viewModel: MyPagesViewModel
    private var _binding:MyPagesFragmentBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =ViewModelProvider(this).get(MyPagesViewModel::class.java)
        _binding = MyPagesFragmentBinding.inflate(inflater,container,false)
        val root:View = binding!!.root
        check()
        viewModel.myPageViewModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            pageAdapter = context?.let { it1-> PageAdapter(it1,it) }
            recyclerView!!.adapter = pageAdapter
            pageAdapter!!.notifyDataSetChanged()
        })

        return root
    }

    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.myPages_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }

    private fun check(){
        val data = FirebaseDatabase
            .getInstance()
            .reference
            .child("Pages")
        data.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children){
                    val dataModel = snapshot.getValue(PageModel::class.java)
                    if (dataModel!!.pageAdmin == FirebaseAuth.getInstance().currentUser!!.uid){
                        no_page_txt.visibility = View.GONE
                        myPages_RV.visibility = View.VISIBLE
                    }else{
                        no_page_txt.visibility = View.VISIBLE
                        myPages_RV.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
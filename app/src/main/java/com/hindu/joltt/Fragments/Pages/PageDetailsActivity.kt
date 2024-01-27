package com.hindu.joltt.Fragments.Pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Activity.CreatePagePostActivity
import com.hindu.joltt.Activity.ShowUsersActivity
import com.hindu.joltt.Adapter.PostAdapter
import com.hindu.joltt.Model.PageModel
import com.hindu.joltt.Model.PostModel
import de.hdodenhof.circleimageview.CircleImageView

class PageDetailsActivity : AppCompatActivity() {
    private var pageId = ""
    private var pageAdmin = ""
    private var pageName = ""
    var postList:MutableList<PostModel>? = null
    private var postAdapter: PostAdapter? = null

    private lateinit var pageFollow_Btn_pd:AppCompatButton
    private lateinit var createPost_page:AppCompatButton
    private lateinit var createPost_page_img:ImageView
    private lateinit var pageNotification:ImageView
    private lateinit var pageName_detailsPage_appbar:TextView
    private lateinit var pageFollowersCount:TextView
    private lateinit var pagePostCount:TextView
    private lateinit var pageIcon_pd:CircleImageView
    private lateinit var pageDescription_pd:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_details)


        pageDescription_pd = findViewById(R.id.pageDescription_pd)
        createPost_page = findViewById(R.id.createPost_page)
        pageFollow_Btn_pd = findViewById(R.id.pageFollow_Btn_pd)
        createPost_page_img = findViewById(R.id.createPost_page_img)
        pageNotification = findViewById(R.id.pageNotification)
        pageName_detailsPage_appbar = findViewById(R.id.pageName_detailsPage_appbar)
        pageFollowersCount = findViewById(R.id.pageFollowersCount)
        pagePostCount = findViewById(R.id.pagePostCount)
        pageIcon_pd = findViewById(R.id.pageIcon_pd)


        val intent = intent
        pageAdmin = intent.getStringExtra("pageAdmin").toString()
        pageName = intent.getStringExtra("pageName").toString()
        pageId = intent.getStringExtra("pageId").toString()
        checkFollowing()

        if (pageAdmin == FirebaseAuth.getInstance().currentUser!!.uid){
            pageFollow_Btn_pd.visibility = View.GONE
            createPost_page.visibility = View.VISIBLE
            createPost_page_img.visibility = View.VISIBLE
            pageNotification.visibility = View.VISIBLE
        }else{
            pageFollow_Btn_pd.visibility = View.VISIBLE
            createPost_page.visibility = View.GONE
            createPost_page_img.visibility = View.GONE
            pageNotification.visibility = View.GONE
        }
        pageFollow_Btn_pd.setOnClickListener {
            when (pageFollow_Btn_pd.text.toString()) {
                "Follow" -> {
                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("FollowingPages")
                        .child(pageId)
                        .setValue(true)

                    FirebaseDatabase.getInstance().reference.child("Pages")
                        .child(pageId)
                        .child("pageFollowers")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(true)
                }
                "Unfollow"->{
                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("FollowingPages")
                        .child(pageId)
                        .removeValue()

                    FirebaseDatabase.getInstance().reference.child("Pages")
                        .child(pageId)
                        .child("pageFollowers")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .removeValue()
                }

            }
        }

        createPost_page_img.setOnClickListener {

            val dialogView = LayoutInflater.from(this).inflate(R.layout.page_post_dialog,null)

            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)

            val alertdialog = dialogBuilder.show()

            val selectVideoPage = dialogView.findViewById<LinearLayout>(R.id.selectVideoPage)
            val selectImagePage = dialogView.findViewById<LinearLayout>(R.id.selectImagePage)

            selectImagePage.setOnClickListener {
                val intent = Intent(this, CreatePagePostActivity::class.java)
                intent.putExtra("Id",pageId)
                intent.putExtra("admin",pageAdmin)
                intent.putExtra("name",pageName_detailsPage_appbar.text.toString())
                startActivity(intent)
                alertdialog.dismiss()
            }
            selectVideoPage.setOnClickListener {
                val intent = Intent(this,CreateVideoPostPage::class.java)
                intent.putExtra("Id",pageId)
                intent.putExtra("admin",pageAdmin)
                intent.putExtra("name",pageName_detailsPage_appbar.text.toString())
                startActivity(intent)
                alertdialog.dismiss()
            }
        }

        createPost_page.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.page_post_dialog,null)

            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)

            val alertdialog = dialogBuilder.show()

            val selectVideoPage = dialogView.findViewById<LinearLayout>(R.id.selectVideoPage)
            val selectImagePage = dialogView.findViewById<LinearLayout>(R.id.selectImagePage)

            selectImagePage.setOnClickListener {
                val intent = Intent(this,CreatePagePostActivity::class.java)
                intent.putExtra("Id",pageId)
                intent.putExtra("admin",pageAdmin)
                intent.putExtra("name",pageName_detailsPage_appbar.text.toString())
                startActivity(intent)
                alertdialog.dismiss()
            }
            selectVideoPage.setOnClickListener {
                val intent = Intent(this,CreateVideoPostPage::class.java)
                intent.putExtra("pageId",pageId)
                intent.putExtra("pageAdmin",pageAdmin)
                intent.putExtra("pageName",pageName_detailsPage_appbar.text.toString())
                startActivity(intent)
                alertdialog.dismiss()
            }
        }

        loadPagesDetails()
        val recyclerView:RecyclerView = findViewById(R.id.pagePost_RV)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout =true
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true

        postList = ArrayList()
        postAdapter = PostAdapter(this,postList as ArrayList<PostModel>,"Page")
        recyclerView.adapter = postAdapter
        loadPagePosts()
        totalFollowers()

        pageFollowersCount.setOnClickListener {
            val intent = Intent(this, ShowUsersActivity::class.java)
            intent.putExtra("id", pageId)
            startActivity(intent)
        }
        pageNotification.setOnClickListener {
            deletePage()
        }

    }

    private fun loadPagesDetails(){
        val data = FirebaseDatabase.getInstance().reference
            .child("Pages")
            .child(pageId)
        data.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val pageData = snapshot.getValue(PageModel::class.java)
                    pageName_detailsPage_appbar.text = pageData!!.pageName
                    pageDescription_pd.text = pageData.pageDescription
                    //pageFollowersCount.text = pageData.
                    Glide.with(this@PageDetailsActivity).load(pageData.pageIcon).into(pageIcon_pd)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun loadPagePosts(){
        val database = FirebaseDatabase.getInstance().reference
            .child("Post")
        database.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (postList as ArrayList<PostModel>).clear()
                    for (snapshot in snapshot.children){
                        val postModel = snapshot.getValue(PostModel::class.java)
                        if (postModel!!.publisher == pageId){
                            postList!!.add(postModel)
                            pagePostCount.text = (postList as ArrayList<PostModel>).size.toString()
                        }
                }
                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun totalFollowers(){
        val data = FirebaseDatabase.getInstance().reference.child("Pages")
            .child(pageId).child("pageFollowers")

        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pageFollowersCount.text = snapshot.childrenCount.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun checkFollowing(){
        val data = FirebaseDatabase.getInstance().reference.child("Pages")
            .child(pageId)
            .child("pageFollowers")
        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){
                    pageFollow_Btn_pd.text = "Unfollow"
                }else{
                    pageFollow_Btn_pd.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun deletePage(){
        FirebaseDatabase.getInstance().reference.child("Pages")
            .child(pageId).removeValue()
        Toast.makeText(this,"Page Deleted",Toast.LENGTH_SHORT).show()
        finish()
    }

}
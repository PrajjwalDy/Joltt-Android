package com.hindu.cunow.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.resources.TextAppearanceFontCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Fragments.Pages.PageDetailsActivity
import com.hindu.cunow.Model.PageModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_pages_tab.*
import org.w3c.dom.Text

class PageAdapter(private var mContext: Context,
                  private var mPage:List<PageModel>):RecyclerView.Adapter<PageAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pageIcon: CircleImageView? = itemView.findViewById(R.id.pagesIco) as? CircleImageView
        val pageName: TextView = itemView.findViewById(R.id.pageName_CV) as TextView
        val pageDescription: TextView = itemView.findViewById(R.id.pageDescription_Cv) as TextView
        val followButton: Button = itemView.findViewById(R.id.pageFollow_Btn) as Button

        fun bind(list: PageModel) {
            pageName.text = list.pageName
            pageDescription.text = list.pageDescription
            Glide.with(mContext).load(list.pageIcon).into(pageIcon!!)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.page_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mPage[position])

        checkFollowing(holder.followButton,mPage[position].pageId!!)
        holder.followButton.setOnClickListener {
            followPage(holder.followButton,mPage[position].pageId!!)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext,PageDetailsActivity::class.java)
            intent.putExtra("pageId",mPage[position].pageId)
            intent.putExtra("pageAdmin",mPage[position].pageAdmin)
            intent.putExtra("pageName",mPage[position].pageName)
            mContext.startActivity(intent)
        }
        if (mPage[position].pageAdmin == FirebaseAuth.getInstance().currentUser!!.uid){
            holder.followButton.visibility =View.GONE
        }else{
            holder.followButton.visibility =View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mPage.size
    }

    private fun followPage(button: Button, pageId: String) {

        when (button.text.toString()) {
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

    private fun checkFollowing(button: Button,pageId: String){
        val data = FirebaseDatabase.getInstance().reference.child("Pages")
            .child(pageId)
            .child("pageFollowers")
        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){
                    button.text = "Unfollow"
                }else{
                    button.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
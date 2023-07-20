package com.hindu.cunow.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hindu.cunow.Activity.WebView2
import com.hindu.cunow.Activity.WebViewActivity
import com.hindu.cunow.Model.ClubModel
import com.hindu.cunow.Model.CourseModel
import com.hindu.cunow.R

class ClubsAdapter(private val mContext: Context,
                   private val mList:List<ClubModel>):RecyclerView.Adapter<ClubsAdapter.ViewHolder>() {

                       inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                           private val jobTitle:TextView = itemView.findViewById(R.id.TV_jobTitle) as TextView
                           private val jobImage: ImageView = itemView.findViewById(R.id.IV_job) as ImageView
                           private val jobLocation:TextView = itemView.findViewById(R.id.jobLocationTV) as TextView
                           private val jobSalary:TextView = itemView.findViewById(R.id.jobSalaryTV) as TextView
                           private val jobExperience:TextView = itemView.findViewById(R.id.jobExperience_tv) as TextView
                           private val jobSkills:TextView = itemView.findViewById(R.id.jobSkillTV) as TextView
                           private val company:TextView = itemView.findViewById(R.id.TV_company) as TextView
                           private val imageCV:CardView = itemView.findViewById(R.id.CV_jobImage) as CardView


                           fun bind(list:ClubModel){
                               if (list.jobImage.isNullOrEmpty()){
                                   imageCV.visibility = View.GONE
                                   jobImage.visibility = View.GONE
                               }else{
                                   Glide.with(mContext).load(list.jobImage).into(jobImage)
                                   imageCV.visibility = View.VISIBLE
                                   jobImage.visibility = View.VISIBLE
                               }

                               jobTitle.text = list.jobTitle
                               jobLocation.text = list.jobLocation
                               jobSalary.text = list.jobSalary
                               jobExperience.text = list.jobExperience
                               jobSkills.text = list.jobSkills
                               company.text = list.jobCompany

                               itemView.setOnClickListener{
                                   openLink(list.jobLink!!,list.jobTitle!!)
                               }
                           }
                       }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.club_item_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    private fun openLink(link:String,title:String){

       /* val intent = Intent(mContext, WebView2::class.java)
        intent.putExtra("url", link)
        intent.putExtra("title", title)
        mContext.startActivity(intent)*/

        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(mContext, Uri.parse(link))
    }
}
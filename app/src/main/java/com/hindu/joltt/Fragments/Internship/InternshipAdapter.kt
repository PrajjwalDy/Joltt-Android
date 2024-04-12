package com.hindu.joltt.Fragments.Internship

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hindu.cunow.R
import com.hindu.joltt.Model.InternshipModel

class InternshipAdapter(private val mContext: Context) : RecyclerView.Adapter<InternshipAdapter.ViewHolder>() {

    private var internship: List<InternshipModel> = mutableListOf()
    private val PREF_NAME = "InternshipCache"
    private val KEY_INTERNSHIPS = "internships"
    private val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


    init {
        internship = getCachedInternships()
    }

    fun setItems(items:List<InternshipModel>){
        internship = items
        notifyDataSetChanged()

        cacheInternships(items)
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val intern_name: TextView = itemView.findViewById(R.id.intern_title) as TextView
        val offered: TextView = itemView.findViewById(R.id.tv_offeredBy) as TextView
        val location: TextView = itemView.findViewById(R.id.tv_iLocation) as TextView
        val startDate: TextView = itemView.findViewById(R.id.tv_startDate) as TextView
        val duration: TextView = itemView.findViewById(R.id.tv_duration) as TextView
        val stipend: TextView = itemView.findViewById(R.id.tv_startStipend) as TextView
        val type: TextView = itemView.findViewById(R.id.tv_iType) as TextView
        val posted: TextView = itemView.findViewById(R.id.tv_iPosted) as TextView


        fun bind(list: InternshipModel) {
            intern_name.text = list.internTitle
            offered.text = list.iOffered
            location.text = list.iLocation
            startDate.text = list.iStart
            duration.text = list.iDuration
            stipend.text = list.iStipend
            type.text = list.iType
            /*posted.text = "posted " + list.iPosted*/

            itemView.setOnClickListener {
                openLink(list.iLink!!, list.internTitle!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.internship_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return internship.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(internship[position])
    }

    private fun openLink(link: String, title: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(mContext, Uri.parse(link))
    }

    private fun cacheInternships(internships: List<InternshipModel>) {
        val gson = Gson()
        val json = gson.toJson(internships)
        sharedPreferences.edit().putString(KEY_INTERNSHIPS, json).apply()
    }

    private fun getCachedInternships(): List<InternshipModel> {
        val gson = Gson()
        val json = sharedPreferences.getString(KEY_INTERNSHIPS, "")
        val type = object : TypeToken<List<InternshipModel>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

}
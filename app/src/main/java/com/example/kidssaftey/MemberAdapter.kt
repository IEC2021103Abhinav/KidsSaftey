package com.example.kidssaftey

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


//adapter basically gives the data to the recycler view
//ViewHolder basically holds the view
//jo bhi views hamesa change ho rahi hai usko hame viewhoder mein hold kar ke rakhna padta hai

class MemberAdapter(private val memberslist: List<MemberModel>) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val item=inflater.inflate(R.layout.item_members,parent,false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        basically ye data ko set karta hai
//        ek baar mein ek member set karna hai
        val item=memberslist[position]
        holder.name.text=item.name
        holder.address.text=item.address
        holder.wifiValue.text=item.wifiValue
        holder.distance.text=item.distance
        holder.battery.text=item.battery
    }

    override fun getItemCount(): Int {
        return memberslist.size
    }


    class ViewHolder(private val item:View) :RecyclerView.ViewHolder(item){
        val userImage=item.findViewById<ImageView>(R.id.img_user)
        val name: TextView =item.findViewById(R.id.name)
        val battery: TextView =item.findViewById(R.id.battery_percent)
        val distance: TextView =item.findViewById(R.id.distance_value)
        val wifiValue: TextView =item.findViewById(R.id.wifi_value)
        val address: TextView =item.findViewById(R.id.address)
        val phoneNumber:ImageView=item.findViewById(R.id.img_call)

    }
}
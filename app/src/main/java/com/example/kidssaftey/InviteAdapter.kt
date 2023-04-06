package com.example.kidssaftey

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class InviteAdapter(private val ContactList:List<ContactModel>):RecyclerView.Adapter<InviteAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val item=inflater.inflate(R.layout.item_invites,parent,false)
        return ViewHolder(item)
    }

    override fun getItemCount(): Int {
        return ContactList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=ContactList[position]
        holder.name.text=item.name
    }
    class ViewHolder(private val item: View):RecyclerView.ViewHolder(item) {
        val name: TextView =item.findViewById<TextView>(R.id.name)
    }

}
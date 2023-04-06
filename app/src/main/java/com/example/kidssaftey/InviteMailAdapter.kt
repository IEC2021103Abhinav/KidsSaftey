package com.example.kidssaftey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kidssaftey.databinding.ItemInviteMailBinding

class InviteMailAdapter (private val InvitesList:List<String>,
private val onActionClick: OnActionClick):
    RecyclerView.Adapter<InviteMailAdapter.ViewHolder>() {
    class ViewHolder(binding:ItemInviteMailBinding):RecyclerView.ViewHolder(binding.root) {
        val name=binding.mailTv
        val acceptBtn=binding.acceptBtn
        val denyBtn=binding.denyBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val item=ItemInviteMailBinding.inflate(inflater,parent,false)
        return ViewHolder(item)
    }

    override fun getItemCount(): Int {
        return InvitesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=InvitesList[position]
        holder.name.text=item
        holder.acceptBtn.setOnClickListener {
            onActionClick.onAcceptClick(item)
        }
        holder.denyBtn.setOnClickListener {
            onActionClick.onDenyClick(item)
        }



    }

    interface OnActionClick{
        fun onAcceptClick(mail:String)
        fun onDenyClick(mail:String)

    }



}
package com.example.karunadaan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.karunadaan.entity.FriendList
import com.example.karunadaan.R

class AllChatAdapter(val list:ArrayList<FriendList>,
                     private val onListItemClick: (position:Int) -> Unit
) :RecyclerView.Adapter<AllChatAdapter.ViewHolder>() {

    inner class ViewHolder(view: View,
                           private val onItemClicked: (position: Int) -> Unit
    ):RecyclerView.ViewHolder(view),View.OnClickListener{
        var userName=view.findViewById<TextView>(R.id.donarUserName)

        init {
            view.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position= adapterPosition
            onItemClicked(position)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_entity_layour,parent,false)
        return ViewHolder(view, onListItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text=list[position].userName

    }

    override fun getItemCount(): Int {
        return list.size
    }

}
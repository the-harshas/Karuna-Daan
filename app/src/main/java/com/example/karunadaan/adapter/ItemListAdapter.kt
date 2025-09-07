package com.example.karunadaan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.karunadaan.R
import com.example.karunadaan.entity.DonateEntiy

class ItemListAdapter(
    val list: ArrayList<DonateEntiy>,
    private val onItemClick: (Int) -> Unit
):RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var username=view.findViewById<TextView>(R.id.user_name)
        var location=view.findViewById<TextView>(R.id.location)
        var quantity=view.findViewById<TextView>(R.id.quantity)
        var imageItem=view.findViewById<ImageView>(R.id.imageOfItem)
        var itemName=view.findViewById<TextView>(R.id.itemName)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view= LayoutInflater.from(parent.context)
            .inflate(R.layout.mainpage_itemlist,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.username.text = list[position].userName
        holder.location.text = list[position].address
        holder.itemName.text = list[position].itemName
        holder.quantity.text = list[position].quantity+" p"
        Glide.with(holder.itemView)
            .load(list[position].imageUri)
            .centerCrop()
            .into(holder.imageItem)

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}
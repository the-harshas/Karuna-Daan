package com.example.karunadaan.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.karunadaan.R
import com.example.karunadaan.entity.DonateEntiy

class ItemListDistanceAdapter (
    val list: ArrayList<Pair<DonateEntiy,Float>>,
    private val onItemClick: (Int) -> Unit
):RecyclerView.Adapter<ItemListDistanceAdapter.ItemViewHolder>() {
    var TAG="ItemsAdapter"

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        var username=view.findViewById<TextView>(R.id.user_name)
        var location=view.findViewById<TextView>(R.id.location)
        var quantity=view.findViewById<TextView>(R.id.quantity)
        var imageItem=view.findViewById<ImageView>(R.id.imageOfItem)
        var itemName=view.findViewById<TextView>(R.id.itemName)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListDistanceAdapter.ItemViewHolder {
        var view= LayoutInflater.from(parent.context)
            .inflate( R.layout.mainpage_itemlist, parent,false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemListDistanceAdapter.ItemViewHolder, position: Int) {
        holder.username.text = list[position].first.userName
        holder.location.text = list[position].first.address
        holder.itemName.text = list[position].first.itemName

        Log.d(TAG,"Item adapter list size${list[position].first.userName}")
        holder.quantity.text = String.format("%.2f", (list[position].second/1000)).toString()+" Km"
        Glide.with(holder.itemView)
            .load(list[position].first.imageUri)
            .centerCrop()
            .into(holder.imageItem)

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }

    }

    override fun getItemCount(): Int {
        Log.d(TAG,"Item adapter list size${list.size}")
        return list.size
    }
}
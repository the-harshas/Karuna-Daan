package com.example.karunadaan.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.karunadaan.R

class categoryAdapter(private val dataSet: Array<String>, private val dataSet2: Array<Int>,
                      private val listener: (position:Int, dataset: Array<String>) -> Unit
):
    RecyclerView.Adapter<categoryAdapter.ViewHolder>() {

        var selectedPosition=0

        class ViewHolder(view: View):RecyclerView.ViewHolder(view){
            var textView = view.findViewById<TextView>(R.id.categoryName)
            var imageView= view.findViewById<ImageView>(R.id.categoryImage)
            var background = view.findViewById<TextView>(R.id.categoryBackground)
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.category_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.textView.text=dataSet[position]
        holder.imageView.setImageResource(dataSet2[position])
        if(selectedPosition==position){
            holder.background.setBackgroundResource(R.drawable.category_background)
        }
        else {
            holder.background.setBackgroundColor(Color.WHITE)
        }
        holder.itemView.setOnClickListener {
            notifyItemChanged(selectedPosition)
            selectedPosition=position
            notifyItemChanged(selectedPosition)
            listener(position,dataSet)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}
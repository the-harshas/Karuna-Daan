package com.example.karunadaan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.karunadaan.R
import com.example.karunadaan.entity.User

class LeaderBoardUserAdapter (
    val list: MutableList<Pair<String, Int>>
    ): RecyclerView.Adapter<LeaderBoardUserAdapter.LeaderBoardUserViewModel>()  {

        inner class LeaderBoardUserViewModel(itemView: View): RecyclerView.ViewHolder(itemView) {
            var donarName=itemView.findViewById<TextView>(R.id.userName)
            var donarScore=itemView.findViewById<TextView>(R.id.userXp)
            var donarRank=itemView.findViewById<TextView>(R.id.ranking)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderBoardUserViewModel {

            val view= LayoutInflater.from(parent.context).inflate(R.layout.leaderboard_item, parent, false)
            return LeaderBoardUserViewModel(view)
        }

        override fun onBindViewHolder(holder: LeaderBoardUserViewModel, position: Int) {

            holder.donarScore.text=list.get(position).second.toString()
            holder.donarRank.text=(position+1).toString()
            holder.donarName.text=list.get(position).first

        }

        override fun getItemCount(): Int {
            return list.size
        }
}
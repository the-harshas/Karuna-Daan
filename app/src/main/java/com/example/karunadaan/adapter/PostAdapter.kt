package com.example.karunadaan.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.karunadaan.R
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(val context: Context, val list: ArrayList<Post>)
    :RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
        var TAG ="PostAdapter"

         class PostViewHolder(view: View):RecyclerView.ViewHolder(view) {

            val postedByName = view.findViewById<TextView>(R.id.postUserName)
            val postContent = view.findViewById<TextView>(R.id.postText)
            val postDate = view.findViewById<TextView>(R.id.postTimeUpload)
            val postedBYImage = view.findViewById<ImageView>(R.id.postProfileImage)
            val postLikeCount = view.findViewById<TextView>(R.id.postLikeCount)
            val postCommentCount = view.findViewById<TextView>(R.id.postCommentCount)
            val postLikeButton = view.findViewById<Button>(R.id.postLikeButton)
            val postCommentButton = view.findViewById<Button>(R.id.postCommentButton)
            val listOfImages = view.findViewById<ListView>(R.id.postImages)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.feed_element, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d(TAG,list.size.toString())
        return list.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        Log.d(TAG,"items is being set")

        var post = list[position]

        val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        // Parse the input date-time string
        val date = inputFormat.parse(post.postTime)
        // Format it to "ddMMyy" format
        holder.postDate.text = date?.let { outputFormat.format(it) }

        holder.postedByName.text = post.postedBy
        holder.postContent.text = post.postContent
        holder.postLikeCount.text = post.postLikeCount.toString()
        holder.postCommentCount.text = post.postCommentCount.toString()

    }
}
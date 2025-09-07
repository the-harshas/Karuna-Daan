package com.example.karunadaan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.karunadaan.R
import com.example.karunadaan.entity.Message
import com.google.firebase.auth.FirebaseAuth

class MessagAdapter(val context: Context, val messageList: ArrayList<Message>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1;
    val ITEM_SENT =2;
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            val view : View = LayoutInflater.from(context).inflate(R.layout.recieve_layout,parent,false)
            return ReceiveViewHolder(view);
        }else{
            val view : View = LayoutInflater.from(context).inflate(R.layout.sent_layout,parent,false)
            return SentViewHolder(view);
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage =messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }else{
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]

        if(holder.javaClass == SentViewHolder::class.java){
            // code for sent veiw holder
            val viewHolder = holder as SentViewHolder
            val item = messageList[position]
            holder.sendMessage.text = currentMessage.message
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, item )
                }
            }
        }else{
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message

            val item = messageList[position]
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, item )
                }
            }
        }
    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    interface OnClickListener {
        fun onClick(position: Int, model: Message)
    }

    /*private fun onClick(position: Int, item:ArrayList<Message> ) {
        Log.d(TAG,"On click recycler view")
        if (item[position].message.equals("AUDIO")){
            Log.d(TAG,"message box clicked")
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Log.d(TAG,"message box clicked ready to play")
                mediaPlayer.setDataSource(messageList[position].uri.toString())
                mediaPlayer.prepare()
                mediaPlayer.start()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    } */

    class SentViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val sendMessage = itemView.findViewById<TextView>(R.id.sentMessage)

    }
    class ReceiveViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.recieveMessage)

    }
}

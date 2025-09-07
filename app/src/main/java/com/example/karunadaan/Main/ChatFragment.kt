package com.example.karunadaan.Main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.karunadaan.adapter.AllChatAdapter
import com.example.karunadaan.entity.FriendList
import com.example.karunadaan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var allChatRecyclerView: RecyclerView
    lateinit var mDatabaseReference: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    val TAG="chatActivity"
    var listUid= arrayListOf("")
    var list = arrayListOf<FriendList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDatabaseReference= FirebaseDatabase.getInstance().getReference()
        mAuth=FirebaseAuth.getInstance()


        allChatRecyclerView=view.findViewById(R.id.allChatRecyclerView)

        var list2 =arrayListOf("Rd4g5Nx8Ydhlt6GtXzGJNKFD6Rd2","pp3gHxjt7AUZU5382fzu0nklc6Z2","PdnjvBpxvbbyRILXgTTSHZEgZ6p2")

        //Recycler View settings
        var mLayout= LinearLayoutManager(requireContext())
        mLayout.orientation= LinearLayoutManager.VERTICAL
        var recyclerViewAdapter= AllChatAdapter( list)
        {   position -> onListItemClick(position) }
        allChatRecyclerView.layoutManager=mLayout
        allChatRecyclerView.adapter=(recyclerViewAdapter)
        Log.d(TAG,"chatActivity being executed")


        var friendList= FriendList("Rd4g5Nx8Ydhlt6GtXzGJNKFD6Rd2","anshu kumar")
        var friendList2= FriendList("pp3gHxjt7AUZU5382fzu0nklc6Z2","Abhishek Mishra ")

        val friendReference = mDatabaseReference.child("friendDb").child(mAuth.uid.toString()).child("friends")
//        var x = arrayListOf(friendList)
//        x.add(friendList2)
//        friendReference.setValue(x)
        friendReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for (friendSnapshot in dataSnapshot.children) {
                    val existingFriend = friendSnapshot.getValue(FriendList::class.java)
                    if (existingFriend != null) {
                        list.add(existingFriend)
                    }
                }
//                val friendList = dataSnapshot.getValue<ArrayList<FriendList>>()
//                if (friendList != null) {
//                    for( i in friendList){
//                        list.add(i)
//                    }
//                }
                recyclerViewAdapter.notifyDataSetChanged()
                Log.d(TAG,"list has been set + ${list.size}")
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }
    private fun onListItemClick(position: Int) {
        var intent= Intent(requireContext(), OneToOneChat::class.java)
//        var name=mDatabaseReference.child()

        intent.putExtra("name",list[position].userName)
        intent.putExtra("uid",list[position].userId)
        startActivity(intent)
    }
    fun fetchAllUserIds(callback: (List<String>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users") // Reference to "users" node

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userIdList = mutableListOf<String>()

                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        child.key?.let { userIdList.add(it) } // Add user ID (key) to list
                    }
                }
                callback(userIdList) // Return user IDs via callback
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList()) // Return empty list on error
            }
        })
    }
}
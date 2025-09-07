package com.example.karunadaan.Main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.karunadaan.R
import com.example.karunadaan.adapter.LeaderBoardUserAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LeaderShipFragment : Fragment() {

    var TAG="LeaderShipFragment"
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leader_ship, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Recycler View settings


        val pairList: MutableList<Pair<String, Int>> = mutableListOf()
//        var sortedDescending : List<Pair<String, Int>> = arrayListOf()
        var list1= arrayListOf<String>()
        var list2= arrayListOf<String>()

        var donarsList= view.findViewById<RecyclerView>(R.id.recyclerDonarUserList)
        var mLayout= LinearLayoutManager(requireContext())
        mLayout.orientation= LinearLayoutManager.VERTICAL
        var recyclerViewAdapter= LeaderBoardUserAdapter( pairList)
        donarsList.layoutManager=mLayout
        donarsList.adapter=(recyclerViewAdapter)
        Log.d(TAG,"chatActivity being executed")

        database = Firebase.database.reference
        var postReference=database.child("users")
        postReference.get().addOnSuccessListener {
                // Get Post object and use the values to update the UI
                pairList.clear()
                for( child in it.children) {
                    var score = child.child("score").value.toString()+""
                    var userName = child.child("fullName").value.toString()+""
                    if(score.isNullOrEmpty() || score.equals("null") )
                        score="0"
                    if(userName.isNullOrEmpty() )
                        userName="user"
                    Log.d(TAG,"$userName")
                    pairList.add(Pair(userName, score.toInt()))
                }
                val  sortedDescending = pairList.sortedByDescending { it.second }
                pairList.clear()
                pairList.addAll(sortedDescending)
                recyclerViewAdapter.notifyDataSetChanged()
        }

//        var list1= arrayListOf<String>()
//        var list2= arrayListOf<String>()
//        val db = FirebaseFirestore.getInstance()
//        val usersRef = db.collection("users").document().collection(User::class.java)
//        val query = usersRef.orderBy("score", Query.Direction.DESCENDING)
//
//        val docRef = db.collection("users").document()
//        docRef.get()
//            .addOnSuccessListener { document ->
//                if (document != null) {
//                    list1.add(document.data.toString())
//                    list2.add(document.data.toString())
//                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
//                } else {
//                    Log.d(TAG, "No such document")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "get failed with ", exception)
//            }


//        query.get().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                for (document in task.result) {
//                    val username = document.getString("fullName")
//                    //val score = document.getString("score")
//                    list1.add(username!!)
//                    list2.add("ihj")
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//            } else {
//                Log.w(TAG, "Error getting documents.", task.exception)
//            }
//        }

    }
}
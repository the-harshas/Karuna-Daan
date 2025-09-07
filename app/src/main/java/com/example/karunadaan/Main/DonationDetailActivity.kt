package com.example.karunadaan.Main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.karunadaan.R
import com.example.karunadaan.entity.FriendList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class DonationDetailActivity : AppCompatActivity() {

    private lateinit var itemName:TextView
    private lateinit var itemCategory:TextView
    private lateinit var itemAddress:TextView
    private lateinit var itemMobileNumber:TextView
    private lateinit var itemImage:ImageView
    private lateinit var itemDescription:TextView
    private lateinit var chatButton:Button
    private lateinit var donarName: TextView
    private val TAG="DonationDetailsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_donation_detail)
//        val wrapper = intent.getSerializableExtra("donation_data") as? DonationsWrapper
//        val donations = wrapper?.donation

        itemName=findViewById(R.id.donationItemName)
        itemCategory=findViewById(R.id.category)
        itemAddress=findViewById(R.id.itemLocation)
        itemMobileNumber=findViewById(R.id.mobileNumber)
        itemDescription=findViewById(R.id.itemDescription)
        chatButton=findViewById(R.id.chatToDonater)
        donarName=findViewById(R.id.donarUserName)
        itemImage=findViewById(R.id.previewImageOfItem)
        var imageUrl = intent.getStringExtra("donation_imageUri")

            itemName.text=intent.getStringExtra("donation_Name")
            itemCategory.text=intent.getStringExtra("donation_category")
            itemAddress.text=intent.getStringExtra("donation_Address")
            itemDescription.text=intent.getStringExtra("donation_descrption")
            donarName.text=intent.getStringExtra("donation_UserName")
            itemMobileNumber.text="+91 8483245067"
        try {
            Glide.with(this)
                .load(imageUrl) // Show while loading
                .error(R.drawable.upload_to_cloud) // Show if loading fails
                .into(itemImage)
        } catch (e: Exception) {
            Log.e("GlideException", "Error loading image: ${e.message}")
            itemImage.setImageResource(R.drawable.upload_to_cloud) // Set error image manually
        }

        chatButton.setOnClickListener {
//          var name=mDatabaseReference.child()

            var mAuth=FirebaseAuth.getInstance()
            var currentUseruid=mAuth.uid
            var donarUid=intent.getStringExtra("donation_UserId")
            var mDatabaseReference= FirebaseDatabase.getInstance().getReference()
            val friendReference = mDatabaseReference.child("friendDb").child(mAuth.uid.toString()).child("friends")

            var newFriend= intent.getStringExtra("donation_UserName")?.let { it1 ->
                FriendList(
                    intent.getStringExtra("donation_UserId")!!,
                    it1
                )
            }
            friendReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var containsKeyWithValue = false
                    for (snapshot in dataSnapshot.children) {
                        val child = snapshot.child("userId")
                        val childValue =child.getValue<String>()
                        Log.d(TAG,"${childValue.toString()} child value===  $donarUid")
                        if (childValue.toString().equals(donarUid.toString())) {

                            Log.d(TAG,"${childValue.toString()} child value---")
                            containsKeyWithValue = true
                            break
                        }
                    }
                    if (!containsKeyWithValue) {
                        friendReference.push().setValue(newFriend)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Error reading data
                    Log.d(TAG, "Error reading data: ${databaseError.message}")
                }
            });

            var intentNext= Intent(this, OneToOneChat::class.java)
            intentNext.putExtra("name",intent.getStringExtra("donation_UserName").toString())
            intentNext.putExtra("uid",intent.getStringExtra("donation_UserId"))
            startActivity(intentNext)

        }

    }
}
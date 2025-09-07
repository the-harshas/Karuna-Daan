package com.example.karunadaan.Main


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.karunadaan.adapter.ItemListAdapter
import com.example.karunadaan.R
import com.example.karunadaan.databinding.FragmentDonateBinding
import com.example.karunadaan.entity.DonateEntiy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DonateFragment : Fragment() {

    private var _binding: FragmentDonateBinding? = null
    private lateinit var donateButton:Button
    private lateinit var donationsByUser: RecyclerView
    private lateinit var currentUid:String
    private var TAG="DonateFragment"


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDonateBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        donateButton=view.findViewById(R.id.uploadDonation)
        donationsByUser=view.findViewById(R.id.donatedItemByUser)

        donateButton.setOnClickListener {
            var intent = Intent(requireContext(), DonateActivity::class.java)
            startActivity(intent)
        }
        currentUid=FirebaseAuth.getInstance().currentUser!!.uid

        var mDatabaseReference= FirebaseDatabase.getInstance().reference
        var dataset2 = arrayOf("Clothes", "Food", "Stationary","Electronics","Money")
        var itemsDataset = arrayListOf<DonateEntiy>()

        var customItemListAdapter = ItemListAdapter(itemsDataset) { position ->
            var intent= Intent(requireContext(), DonationDetailActivity::class.java)
            var obj=itemsDataset[position]
            intent.putExtra("donation_Name",obj.itemName )
            intent.putExtra("donation_category",obj.category )
            intent.putExtra("donation_Address",obj.address )
            intent.putExtra("donation_descrption",obj.itemDescription )
            intent.putExtra("donation_imageUri",obj.imageUri )
            intent.putExtra("donation_uploadTime",obj.time )
            intent.putExtra("donation_UserId",obj.uid )
            intent.putExtra("donation_UserName",obj.userName)
            startActivity(intent)
        }
        var mLayout3 = LinearLayoutManager(requireContext())
        donationsByUser.layoutManager = mLayout3
        mLayout3.orientation = LinearLayoutManager.VERTICAL
        donationsByUser.adapter = customItemListAdapter


        mDatabaseReference.child("donationItem").get().addOnSuccessListener {

//            Log.d(ContentValues.TAG,it.toString()+" parent hii"+it.childrenCount)
            itemsDataset.clear()
            for(  ch in it.children){
                Log.d(TAG,"${ch.child("Donation").value} ch")
                var donar=ch.child("Donation").getValue<DonateEntiy>()
                Log.d(TAG,"${donar?.uid}")
                if (donar != null) {
                    if(donar.uid.equals(currentUid)){
                            itemsDataset.add(donar)
                            Log.d(TAG,"$donar donation")
                    }
                }
            }
            customItemListAdapter.notifyDataSetChanged()

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

    }
}
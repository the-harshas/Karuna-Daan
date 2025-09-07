package com.example.karunadaan.Main

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.karunadaan.R
import com.example.karunadaan.adapter.ItemListDistanceAdapter
import com.example.karunadaan.adapter.categoryAdapter
import com.example.karunadaan.databinding.FragmentMainBinding
import com.example.karunadaan.adapter.topBarAdapter
import com.example.karunadaan.entity.DonateEntiy
import com.example.karunadaan.viewmodel.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.getValue

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment(){

    private var _binding: FragmentMainBinding? = null
    lateinit var categoryRecycler: RecyclerView
    private val viewModel: SharedViewModel by viewModels()
    lateinit var itemListRecyclerView: RecyclerView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private  var lat: String=""
    private  var log: String=""
    private var TAG="MainFragment"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryRecycler=view.findViewById(R.id.recyclerViewCategories)
        itemListRecyclerView=view.findViewById(R.id.recyclerViewItemList)

//      navigationView.setNavigationItemSelectedListener()
        var dataset2 = arrayOf("Clothes", "Food", "Stationary","Electronics","Money")
        var itemsDataset = arrayListOf<DonateEntiy>()
        var tempItemsDataset = arrayListOf<DonateEntiy>()
        var dataset3= arrayOf(R.drawable.cloths_icon,R.drawable.food2_icon,R.drawable.book_icon,R.drawable.electronics,R.drawable.crowdfunding_icon)

        var sortedPlacesWithDistance = ArrayList<Pair<DonateEntiy, Float>>()
        sortedPlacesWithDistance.add(Pair(DonateEntiy(), 0f))

        var customItemListAdapter = ItemListDistanceAdapter(sortedPlacesWithDistance) { position ->
            var intent= Intent(requireContext(), DonationDetailActivity::class.java)
            var obj=sortedPlacesWithDistance[position].first
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

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        while (checkLocationPermission()!=true) {
            checkLocationPermission() // check loaction permission
        }
        if (!isLocationEnabled()) {
            showEnableLocationDialog()
        } else {
            Toast.makeText(requireContext(), "Location is already enabled", Toast.LENGTH_SHORT).show()
        }

        getCurrentLocation()
        // setting up recycler view of itemList

        var mLayout3 = LinearLayoutManager(requireContext())
        itemListRecyclerView.layoutManager = mLayout3
        mLayout3.orientation = LinearLayoutManager.VERTICAL
        itemListRecyclerView.adapter = customItemListAdapter

        var customCategoryAdapter= categoryAdapter(dataset2,dataset3) {
                positon,dataset ->

            viewModel.mDatabaseReference.child("donationItem").get().addOnSuccessListener {
//
//                Log.d(TAG,it.toString()+" parent hii"+it.childrenCount)
                itemsDataset.clear()
                for(  child in it.children){
                    Log.d(TAG,child.value.toString()+"hii")
                    child.child("Donation").getValue<DonateEntiy>()?.let { it1 ->
                        if(it1.category.equals(dataset2[positon].toString()) ) itemsDataset.add(it1)
                    }
                }
                //  Calculate distance from current location

                Log.d(TAG,"Going to calculate distance ${itemsDataset.size}")
                val sharedPreferences = requireContext().getSharedPreferences("MyLocationPrefs", Context.MODE_PRIVATE)
                //if( sharedPreferences.getString("latitude",null)==null) getCurrentLocation()
                val currentLat = sharedPreferences.getString("latitude", null)
                val currentLon = sharedPreferences.getString("longitude", null)
                var sortedPlaces = itemsDataset.map { place ->
                    var donateLat = 6.0
                    var donateLog = 4.0
                    if(place.lat==null) donateLat= 0.0
                    else donateLat=place.lat!!.toDouble()
                    if(place.log==null) donateLog= 0.0
                    else donateLog=place.log!!.toDouble()
                    val result = FloatArray(1)
                    Location.distanceBetween(currentLat!!.toDouble(), currentLon!!.toDouble(),
                        donateLat, donateLog, result)
                    Log.d(TAG,"hii $place ${result[0]}")
                    place to result[0] // Store the distance
                }.sortedBy { it.second } // Sort by nearest
                sortedPlacesWithDistance.clear()
                sortedPlacesWithDistance.addAll(sortedPlaces)

                Log.d(TAG,"Now updating the adapter of items ${sortedPlacesWithDistance.size}")
                customItemListAdapter.notifyDataSetChanged()

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }

        }
        var mLayout2= LinearLayoutManager(MainActivity2())
        categoryRecycler.layoutManager=mLayout2
        mLayout2.orientation= LinearLayoutManager.HORIZONTAL
        categoryRecycler.adapter= customCategoryAdapter

        //
        val datasetBanner = arrayOf(R.drawable.bannerimage_1,R.drawable.bannerimage_2,R.drawable.bannerimage_3,R.drawable.bannerimage_4,
            R.drawable.bannerimage_5,R.drawable.bannerimage_6,R.drawable.bannerimage_7)
        val customAdapter = topBarAdapter(datasetBanner)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewBanner)
        var mLayout= LinearLayoutManager(MainActivity2())
        recyclerView.layoutManager = mLayout
        mLayout.orientation=LinearLayoutManager.HORIZONTAL
        recyclerView.adapter = customAdapter

        //

        viewModel.mDatabaseReference.child("donationItem").get().addOnSuccessListener {
//
//                Log.d(TAG,it.toString()+" parent hii"+it.childrenCount)
            itemsDataset.clear()
            for(  child in it.children){
                Log.d(TAG,child.value.toString()+"hii")
                child.child("Donation").getValue<DonateEntiy>()?.let { it1 ->
                    if(it1.category.equals(dataset2[0].toString()) ) itemsDataset.add(it1)
                }
            }
            //  Calculate distance from current location

            Log.d(TAG,"Going to calculate distance ${itemsDataset.size}")
            val sharedPreferences = requireContext().getSharedPreferences("MyLocationPrefs", Context.MODE_PRIVATE)
            //if( sharedPreferences.getString("latitude",null)==null) getCurrentLocation()
            val currentLat = sharedPreferences.getString("latitude", null)
            val currentLon = sharedPreferences.getString("longitude", null)
            var sortedPlaces = itemsDataset.map { place ->
                var donateLat = 6.0
                var donateLog = 4.0
                if(place.lat==null) donateLat= 0.0
                else donateLat=place.lat!!.toDouble()
                if(place.log==null) donateLog= 0.0
                else donateLog=place.log!!.toDouble()
                val result = FloatArray(1)
                Location.distanceBetween(currentLat!!.toDouble(), currentLon!!.toDouble(),
                    donateLat, donateLog, result)
                Log.d(TAG,"hii $place ${result[0]}")
                place to result[0] // Store the distance
            }.sortedBy { it.second } // Sort by nearest
            sortedPlacesWithDistance.clear()
            sortedPlacesWithDistance.addAll(sortedPlaces)

            Log.d(TAG,"Now updating the adapter of items ${sortedPlacesWithDistance.size}")
            customItemListAdapter.notifyDataSetChanged()

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //  Check if permission is granted, else request it
    private fun checkLocationPermission():Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return false
        }
    }

    //  Handle permission request result
    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ Fetch current location and store it
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                // Store location in SharedPreferences
                saveLocation(latitude, longitude)

                Toast.makeText(requireContext(), "Location Saved! Lat: $latitude, Long: $longitude", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //  Store location in SharedPreferences
    private fun saveLocation(lat: Double, lon: Double) {
        val sharedPreferences = requireContext().getSharedPreferences("MyLocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("latitude", lat.toString())
        editor.putString("longitude", lon.toString())
        editor.apply()
    }

    //  Retrieve location from SharedPreferences
    private fun getStoredLocation(): Pair<String?, String?> {
        val sharedPreferences = requireContext().getSharedPreferences("MyLocationPrefs", Context.MODE_PRIVATE)
        val lat = sharedPreferences.getString("latitude", null)
        val lon = sharedPreferences.getString("longitude", null)
        return Pair(lat, lon)
    }
    // ✅ Check if location services (GPS) are enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // ✅ Show a dialog prompting the user to enable location
    private fun showEnableLocationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enable Location")
        builder.setMessage("Location services are required for this app. Please enable GPS.")
        builder.setPositiveButton("Enable") { _, _ ->
            // Open location settings
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(requireContext(), "Location is required!", Toast.LENGTH_SHORT).show()
        }
        builder.setCancelable(false)
        builder.show()
    }
}
package com.example.karunadaan.Main

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.karunadaan.R
import com.example.karunadaan.entity.DonateEntiy
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class DonateActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var moneyLinearLayout:LinearLayout
    private lateinit var quantitySelectionLayout:LinearLayout
    lateinit var address:EditText
    lateinit var uploadButton:Button
    lateinit var itemName:EditText
    lateinit var itemDescription:EditText
    lateinit var image:ImageView
    lateinit var userNameTextView:TextView
    lateinit var enterMobileNumber:EditText
    lateinit var selectQuantity:Spinner
    lateinit var mAuth:FirebaseAuth
    lateinit var mDatabaseReference: DatabaseReference
    lateinit var mFirestore:StorageReference
    var SELECT_PICTURE = 200
    private var category:String ?= null
    private var quantity:Int? =null
    var imageUri:Uri?=null
    var TAG="DonateActivity"
    private val CAMERA_PERMISSION_CODE = 1000
    private val READ_PERMISSION_CODE = 1001
    private var filePath:Uri?=null
    private lateinit var dounloadUri:Uri
    private var userName:String="User"
    private lateinit var currentUserUid:String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private  var lat: String=""
    private  var log: String=""
    private  var boolValue:Boolean=false


    val itemsCategory = arrayOf("Select category",
        "Clothes", "Stationary",
        "Food", "Electronics","Money"
    )
    val itemsQuantity = arrayOf("Number of items","1","2","3","4","5","6")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_donate)
//        userName = intent.extras?.getString("userName").toString()

        moneyLinearLayout=findViewById(R.id.selectAmountLinearLayout)
        quantitySelectionLayout=findViewById(R.id.selectItem)
        address=findViewById(R.id.enterLocation)
        image=findViewById(R.id.previewImageOfItem)
        selectQuantity=findViewById(R.id.selectQuantity)
        uploadButton=findViewById(R.id.uploadDonation)
        itemName=findViewById(R.id.enterItemName)
        itemDescription=findViewById(R.id.itemDescription)
        userNameTextView=findViewById(R.id.donarUserNameDonation)
        enterMobileNumber=findViewById(R.id.EnterMobileNumber)
        mAuth=FirebaseAuth.getInstance()
        mDatabaseReference=FirebaseDatabase.getInstance().getReference()
        mFirestore=FirebaseStorage.getInstance().reference.child("images").child("donationImage")




        boolValue = intent.getBooleanExtra("KEY_MONEY_BOOL", false)
        if(boolValue==true) {
            quantitySelectionLayout.visibility=View.INVISIBLE
            moneyLinearLayout.visibility=View.VISIBLE
            itemName.visibility=View.INVISIBLE
            image.setImageResource(R.drawable.money_image)
            image.isClickable=false
            image.isClickable = false
            image.isFocusable = false

        }
        category=intent.getStringExtra("KEY_CATEGORY")

        currentUserUid= mAuth.currentUser!!.uid.toString()
        getUsername(currentUserUid)
        Log.d("Firestore","$userName")
        //userNameTextView.text = userName


        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        while (checkLocationPermission()!=true) {
            checkLocationPermission() // check loaction permission
        }
            if (!isLocationEnabled()) {
                showEnableLocationDialog()
            } else {
                Toast.makeText(this, "Location is already enabled", Toast.LENGTH_SHORT).show()
            }

        getCurrentLocation()

        selectQuantity.setOnItemSelectedListener(this)

        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            itemsCategory
        )
        val ad2 = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            itemsQuantity
        )
        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        ad2.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        selectQuantity.setAdapter(ad2)

        uploadButton.setOnClickListener {
            if(address.text.toString().equals("")){
                Snackbar.make(findViewById(R.id.donate),"Please enter address",Snackbar.LENGTH_SHORT).show()
            }
            else if( boolValue==false && itemName.text.toString().equals("")){
                Snackbar.make(findViewById(R.id.donate),"Please enter item's name",Snackbar.LENGTH_SHORT).show()
            }
//            else if(itemDescription.text.toString().length<10){
//                Snackbar.make(findViewById(R.id.donate),"Decription should be longer",Snackbar.LENGTH_SHORT).show()
//            }
            else if(enterMobileNumber.text.equals("") ){
                Snackbar.make(findViewById(R.id.donate),"Please select mobile Number",Snackbar.LENGTH_SHORT).show()
            }
            else if(enterMobileNumber.text.length<10  && enterMobileNumber.text.length>10){
                Snackbar.make(findViewById(R.id.donate),"Please enter correct Number",Snackbar.LENGTH_SHORT).show()
            }
            else if( boolValue==false && quantity == null ){
                Snackbar.make(findViewById(R.id.donate),"Please select category",Snackbar.LENGTH_SHORT).show()
            }
//            else if(category == null){
//                Snackbar.make(findViewById(R.id.donate),"Please select number of item",Snackbar.LENGTH_SHORT).show()
//            }

            else if (!isLocationEnabled()) {
                showEnableLocationDialog()
            }
            else if( filePath==null) {
                if(boolValue==false)
                  Snackbar.make(findViewById(R.id.donate),"Please select Image",Snackbar.LENGTH_SHORT).show()
                else{


                    // Code for showing progressDialog while uploading
                    var progressDialog =  ProgressDialog(this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                        Log.d(TAG,"started to update database")
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val currentDate = sdf.format(Date())
                        System.out.println(" C DATE is  "+currentDate)
                        if(quantity==null)quantity=1
                        val sharedPreferences = getSharedPreferences("MyLocationPrefs", Context.MODE_PRIVATE)
                        var donateEntity=
                            DonateEntiy(
                                uid = currentUserUid,
                                userName = userName,
                                category = intent.getStringExtra("KEY_CATEGORY")!!,
                                address = address.text.toString(),
                                imageUri = "https://firebasestorage.googleapis.com/v0/b/find-the-person-31258.appspot.com/o/images%2FdonationImage%2Fimages%2Fb526fca4-644b-4267-9e68-3e4a04dc8678?alt=media&token=bf570edc-ab87-4984-90d4-a85eb4a80602",
                                time=currentDate.toString(),
                                itemDescription=itemDescription.text.toString(),
                                itemName=itemName.text.toString(),
                                quantity= quantity.toString(),
                                mobileNumber = enterMobileNumber.text.toString(),
                                lat = sharedPreferences.getString("latitude", null)!!,
                                log = sharedPreferences.getString("longitude", null)!!
                            )
                        if (boolValue==true) {
                            donateEntity.amount=findViewById<EditText>(R.id.enterAmount).text.toString().toInt()
                            donateEntity.itemName=" Donation"
                        }
                        var hashMapDonateEntity= HashMap<String, DonateEntiy>()
                        hashMapDonateEntity.put("Donation",donateEntity)
                        mDatabaseReference.child("donationItem").push()
                            .setValue(hashMapDonateEntity).addOnSuccessListener {

                                // after uploading updaate the score of the user
                                var updateScoreRef=mDatabaseReference.child("users").child(currentUserUid.toString()).child("score")
                                updateScoreRef.get().addOnSuccessListener { snapshot ->
                                    val currentValue = snapshot.getValue(Long::class.java) ?: 0 // Default to 0 if value is null
                                    updateScoreRef.setValue(currentValue + 5) // Increase value by 5
                                }
                                Log.d(TAG,"updated sucessfully+"+category)
                                progressDialog.dismiss()


                                address.text.clear()
                                image.setImageResource(R.drawable.upload_to_cloud)
                                enterMobileNumber.text.clear()
                                selectQuantity.setSelection(0)
                                itemName.text.clear()
                                itemDescription.text.clear()
                                findViewById<EditText>(R.id.enterAmount).text.clear()
                                Toast
                                    .makeText(this,
                                        "Donated, Thanks for your Donation",
                                        Toast.LENGTH_SHORT)
                                    .show();
                            }.addOnFailureListener{
                                Log.d(TAG,"Failed to update")
                            }
                }
            }
            else{
                uploadImage(filePath!!)
            }
        }
        image.setOnClickListener{
            requestStoragePermission()
            imageChoose()
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val spin = parent as Spinner
        if(spin.id== R.id.selectItemcategory) {
            if (position != 0) { category = itemsCategory[position] }
            else category=null
            Toast.makeText(
                getApplicationContext(),
                itemsCategory[position],
                Toast.LENGTH_LONG
            )
                .show();
        }
        else if(spin.id == R.id.selectQuantity) {
            if (position != 0) quantity = itemsQuantity[position].toInt()
            else category=null
            Toast.makeText(
                getApplicationContext(),
                itemsCategory[position],
                Toast.LENGTH_LONG
            )
                .show();
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
    private fun imageChoose(){
        var intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent,"Select Image"),SELECT_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === SELECT_PICTURE &&
            resultCode === RESULT_OK &&
            data != null &&
            data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData()!!
            try {

                // Setting image on image view using Bitmap
                val bitmap = MediaStore.Images.Media
                    .getBitmap(
                        contentResolver,
                        filePath
                    )
                image.setImageBitmap(bitmap)
            } catch (e: IOException) {
                // Log the exception
                e.printStackTrace()
            }
        }
    }
    private fun uploadImage(imageUri:Uri) {
        if (filePath != null) {
            Log.d(TAG,filePath.toString())

            // Code for showing progressDialog while uploading
            var progressDialog =  ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            var ref = mFirestore
                .child(
                    "images/"
                            + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath!!)
                .addOnSuccessListener(
                     OnSuccessListener<UploadTask.TaskSnapshot>() {
                            // Image uploaded successfully
                            // Dismiss dialog

                         it.storage.downloadUrl.addOnSuccessListener {
                             Log.d(TAG,"started to update database")
                             val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                             val currentDate = sdf.format(Date())
                             System.out.println(" C DATE is  "+currentDate)
                             if(quantity==null)quantity=1
                             val sharedPreferences = getSharedPreferences("MyLocationPrefs", Context.MODE_PRIVATE)
                             var donateEntity=
                                 DonateEntiy(
                                     uid = currentUserUid,
                                     userName = userName,
                                     category = intent.getStringExtra("KEY_CATEGORY")!!,
                                     address = address.text.toString(),
                                     imageUri = it.toString(),
                                     time=currentDate.toString(),
                                     itemDescription=itemDescription.text.toString(),
                                     itemName=itemName.text.toString(),
                                     quantity= quantity.toString(),
                                     mobileNumber = enterMobileNumber.text.toString(),
                                     lat = sharedPreferences.getString("latitude", null)!!,
                                     log = sharedPreferences.getString("longitude", null)!!
                                 )
                             if (boolValue==true) {
                                 donateEntity.amount=findViewById<EditText>(R.id.enterAmount).text.toString().toInt()
                                 donateEntity.itemName=" Donation"
                             }
                             var hashMapDonateEntity= HashMap<String,DonateEntiy>()
                             hashMapDonateEntity.put("Donation",donateEntity)
                             mDatabaseReference.child("donationItem").push()
                                    .setValue(hashMapDonateEntity).addOnSuccessListener {

                                        // after uploading updaate the score of the user
                                    var updateScoreRef=mDatabaseReference.child("users").child(currentUserUid.toString()).child("score")
                                     updateScoreRef.get().addOnSuccessListener { snapshot ->
                                         val currentValue = snapshot.getValue(Long::class.java) ?: 0 // Default to 0 if value is null
                                         updateScoreRef.setValue(currentValue + 5) // Increase value by 5
                                     }
                                     Log.d(TAG,"updated sucessfully+"+category)
                                     progressDialog.dismiss()


                                     address.text.clear()
                                     image.setImageResource(R.drawable.upload_to_cloud)
                                     enterMobileNumber.text.clear()
                                     selectQuantity.setSelection(0)
                                     itemName.text.clear()
                                     itemDescription.text.clear()
                                     findViewById<EditText>(R.id.enterAmount).text.clear()
                                     Toast
                                         .makeText(this,
                                             "Donated, Thanks for your Donation",
                                             Toast.LENGTH_SHORT)
                                         .show();
                             }.addOnFailureListener{
                                 Log.d(TAG,"Failed to update")
                             }
                         }
                    })

                .addOnFailureListener( OnFailureListener() {
                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast
                            .makeText(this,
                                "Failed " + it.message,
                                Toast.LENGTH_SHORT)
                            .show();
                })
                .addOnProgressListener(
                     OnProgressListener<UploadTask.TaskSnapshot>() {

                        // Progress Listener for loading
                        // percentage on the dialog box
                            var progress: Double
                            = (100.0
                                * it.getBytesTransferred()
                                / it.getTotalByteCount());
                            progressDialog.setMessage(
                                "Uploaded "
                                        + progress.toInt() + "%");
                    });
        }
    }

    var launchSomeActivity = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode
            == RESULT_OK
        ) {
            val data = result.data
            // do your operation from here....
            if (data != null
                && data.data != null
            ) {
                val selectedImageUri = data.data
                var selectedImageBitmap: Bitmap?=null
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        selectedImageUri
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                image.setImageBitmap(
                    selectedImageBitmap
                )
            }
        }
    }
    private fun requestStoragePermission(): Boolean {
        var permissionGranted = false
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            val storagePermissionNotGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
            if ( storagePermissionNotGranted ) {
                val permission = arrayOf( Manifest.permission.READ_EXTERNAL_STORAGE )
                requestPermissions( permission, READ_PERMISSION_CODE )
            } else {
                permissionGranted = true
            }
        } else {
            permissionGranted = true
        }
        return permissionGranted
    }
    private fun getUsername(uid: String) {
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("fullName") ?: "Unknown"
                    userName=name
                    userNameTextView.text=name

                    Log.d("Firestore", "Name: $name")
                } else {
                    Log.e("Firestore", "User not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching data", e)
            }
    }
    //  Check if permission is granted, else request it
    private fun checkLocationPermission():Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
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
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ Fetch current location and store it
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                // Store location in SharedPreferences
                saveLocation(latitude, longitude)

                Toast.makeText(this, "Location Saved! Lat: $latitude, Long: $longitude", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //  Store location in SharedPreferences
    private fun saveLocation(lat: Double, lon: Double) {
        val sharedPreferences = getSharedPreferences("MyLocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("latitude", lat.toString())
        editor.putString("longitude", lon.toString())
        editor.apply()
    }

    //  Retrieve location from SharedPreferences
    private fun getStoredLocation(): Pair<String?, String?> {
        val sharedPreferences = getSharedPreferences("MyLocationPrefs", Context.MODE_PRIVATE)
        val lat = sharedPreferences.getString("latitude", null)
        val lon = sharedPreferences.getString("longitude", null)
        return Pair(lat, lon)
    }
    // ✅ Check if location services (GPS) are enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // ✅ Show a dialog prompting the user to enable location
    private fun showEnableLocationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enable Location")
        builder.setMessage("Location services are required for this app. Please enable GPS.")
        builder.setPositiveButton("Enable") { _, _ ->
            // Open location settings
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "Location is required!", Toast.LENGTH_SHORT).show()
        }
        builder.setCancelable(false)
        builder.show()
    }
}


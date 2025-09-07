package com.example.karunadaan.Main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.karunadaan.R
import com.example.karunadaan.repository.AuthRepositoryImpl
import com.example.karunadaan.viewmodel.AuthViewModel
import com.example.karunadaan.viewmodel.AuthViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    lateinit var drawableLayout: DrawerLayout
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var navigationView:NavigationView
    lateinit var bottomNavigation: BottomNavigationView
    lateinit var mDatabaseRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var userName:String
    private var selectedItemId: Int? = null
    //lateinit var frameContainer: FrameLayout
    private lateinit var navController: NavController
    var TAG="MainActivity2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        drawableLayout=findViewById(R.id.main2)
        toolbar=findViewById(R.id.toolbar)
        navigationView=findViewById(R.id.nav_view2)
        bottomNavigation=findViewById(R.id.bottomNavigationView)
        mAuth=FirebaseAuth.getInstance()
        mDatabaseRef= FirebaseDatabase.getInstance().getReference()
        statusOnline()
        Log.d(TAG,"mAIN ACTIVITY 2");


        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(bottomNavigation, navController)
        navigationView.setNavigationItemSelectedListener(this)

        navigationView.bringToFront()
        val toggle = ActionBarDrawerToggle(
            this, drawableLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawableLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.nav_home)


//        bottomNavigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.home -> loadFragment( MainFragment() )
//                R.id.donate-> loadFragment( DonateFragment() )
//                R.id.chat -> loadFragment( ChatFragment() )
//                R.id.feed-> loadFragment( LeaderShipFragment() )
//            }
//            true
//        }
//
//        frameContainer=findViewById<FrameLayout>(R.id.frameContainer)
//        replaceFragment(MainFragment())
//        bottomNavigation.setOnNavigationItemSelectedListener {
//            onClick(it)
//            return@setOnNavigationItemSelectedListener true
//        }
    }
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
    private fun requestLocationPermission(){
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){
            if(it){
                acessLocation()
            }
            else{
                Toast.makeText(this,"Location permission in required", Toast.LENGTH_SHORT).show()
            }
        }
        //requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    override fun onStart() {
        super.onStart()
        navigationView.setCheckedItem(R.id.nav_home) // Replace with your menu item ID

    }

    private fun statusOnline(){
        mDatabaseRef.child("onlineUser").child(mAuth.uid!!).child("status").setValue("online")
        Log.d("MainActivity","online updated")
    }
    private fun statusOffline(){
        mAuth.uid?.let { mDatabaseRef.child("onlineUser").child(it).removeValue() }
    }
    private fun acessLocation(){
        Toast.makeText(this,"Accessing location..... ",Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.getItem(0).setChecked(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        statusOffline()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.right_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val newSelection = item.itemId
        if (newSelection!=selectedItemId){
            selectedItemId=newSelection
            when (item.itemId) {
                R.id.nav_home -> {

                }
                R.id.nav_communityWork->{
                    val intent = Intent(this, com.example.karunadaan.Main.DonateActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_aboutUs -> {
                    // Handle Home action
                    var intentAbout = Intent(this, AboutUsActivity::class.java)
                    startActivity(intentAbout)
                }
                R.id.nav_taxBenefit ->{
                    var intentTaxBenefit=Intent(this, TaxBenefitActivity::class.java)
                    startActivity(intentTaxBenefit)
                }
                R.id.nav_logout -> {
                    // Handle Profile action
                    signOut()
                }
            }
        }

        // Close the drawer after selecting an item
         if (drawableLayout.isDrawerOpen(GravityCompat.START)) {
            drawableLayout.closeDrawer(GravityCompat.START)
        }
        return true
    }
    private fun signOut(){
        val authRepo = AuthRepositoryImpl.getInstance(FirebaseAuth.getInstance())

        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(authRepo))[AuthViewModel::class.java]
        authViewModel.logout()
        var intent= Intent(this, LoginActivity::class.java)
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", false)
        editor.clear()  // Clears all stored data
        editor.apply()
        startActivity(intent)
        finish()
        Log.d("Main Acitivty2","Logged out")

    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (drawableLayout.isDrawerOpen(GravityCompat.START)) {
            drawableLayout.closeDrawer(GravityCompat.START) // Close drawer when touching outside
            return true
        }
        return super.onTouchEvent(event)
    }

    //    private fun replaceFragment(fragment: Fragment) {
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//       fragmentTransaction.replace(R.id.frameContainer, fragment)
//        fragmentTransaction.commit()
//    }
//    private fun onClick(menu:MenuItem){
//        var id=menu.itemId
//        when(id){
//            R.id.home->{
//                replaceFragment(MainFragment())
//            }
//            R.id.chat->{   }
//            R.id.donate->{
//                replaceFragment(DonateFragment())
//            }
//            R.id.feed->{
//                var intent = Intent(this, FeedPageActivity::class.java)
//                startActivity(intent)
//            }
//        }
//    }

}
package com.example.karunadaan.Main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.karunadaan.R
import com.example.karunadaan.databinding.ActivitySplashBinding
import com.example.karunadaan.repository.AuthRepositoryImpl
import com.example.karunadaan.viewmodel.AuthViewModel
import com.example.karunadaan.viewmodel.AuthViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)


        val authRepo = AuthRepositoryImpl.getInstance(FirebaseAuth.getInstance())
        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(authRepo))[AuthViewModel::class.java]

        Log.d("Splash Screen"," logged in $isLoggedIn ")
        if(authViewModel.currentUser != null && isLoggedIn ) {
            intent = Intent(this,MainActivity2::class.java);
            intent.putExtra("uid", authViewModel.currentUser!!.uid.toString())
            startActivity(intent);
            finish()
        } else {
            intent =Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this, FirstPage::class.java))
//            finish()
//        }, 500)


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_splash)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
package com.example.karunadaan.Main

import android.app.Application
import com.example.karunadaan.repository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth

class MyApp:Application() {

    override fun onCreate() {
        super.onCreate()
        AuthRepositoryImpl.getInstance(FirebaseAuth.getInstance())
    }
}
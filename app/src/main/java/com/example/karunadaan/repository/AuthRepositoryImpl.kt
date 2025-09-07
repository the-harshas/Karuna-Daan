package com.example.karunadaan.repository

import android.app.Application
import com.example.karunadaan.R
import com.example.karunadaan.data.Resource
import com.example.karunadaan.utils.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
//    private val applicationContext: Application
): AuthRepository {

//    init {
//        val appName = applicationContext.getString(R.string.app_name)
//        println("Hello from the repository. The app is famous $appName")
//    }
    companion object {
        @Volatile private var instance: AuthRepositoryImpl? = null

        fun getInstance(firebaseAuth: FirebaseAuth): AuthRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: AuthRepositoryImpl(firebaseAuth).also { instance = it }
            }
        }
    }

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try{
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        }catch (e:Exception){
            Resource.Failure(e)
        }
    }

    override suspend fun signup(
        name: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        return try{
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
            Resource.Success(result.user!!)
        }catch (e:Exception){
            Resource.Failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()

    }
}
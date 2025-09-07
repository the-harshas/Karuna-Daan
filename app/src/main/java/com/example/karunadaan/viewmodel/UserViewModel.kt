package com.example.karunadaan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.karunadaan.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> get() = _userData


    fun signUpUser(fullName: String, email: String, password: String, role: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userData = User(user!!.uid, fullName, email, role)

                    // Store user data in Firestore
                    db.collection("users").document(user.uid).set(userData)
                        .addOnSuccessListener {
                            _userData.value = userData  // Update ViewModel
                            onComplete(true, null)
                        }
                        .addOnFailureListener { exception ->
                            onComplete(false, exception.message)
                        }
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }
    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        fetchUserData(user.uid, onComplete)
                    }
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun fetchUserData(uid: String, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = document.toObject(User::class.java)
                    _userData.value = userData
                    onComplete(true, null)
                } else {
                    onComplete(false, "User data not found")
                }
            }
            .addOnFailureListener { exception ->
                _userData.value = null
                onComplete(false, exception.message)
            }
    }
}
package com.example.karunadaan.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class SharedViewModel : ViewModel() {
    private val _sharedData = MutableLiveData<String>("Initial Value")
    val sharedData: LiveData<String> get() = _sharedData
    val mDatabaseReference  = FirebaseDatabase.getInstance().getReference()

    fun updateData(newData: String) {
        _sharedData.value = newData
    }
}

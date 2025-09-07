package com.example.karunadaan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karunadaan.data.Resource
import com.example.karunadaan.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

//@HiltViewModel
class AuthViewModel  (
    private val repository: AuthRepository
):ViewModel() {
    // removed null possibility
    private val _loginFlow = MutableLiveData<Resource<FirebaseUser>?>(null)
    val loginFlow: LiveData<Resource<FirebaseUser>?> = _loginFlow

    private val _signUpFlow = MutableLiveData<Resource<FirebaseUser>?>(null)
    val signUpFlow: LiveData<Resource<FirebaseUser>?> = _signUpFlow

    val currentUser: FirebaseUser?
        get() = repository.currentUser
    init{
        if(repository.currentUser !=null){
            _loginFlow.value = Resource.Success(repository.currentUser!!)
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginFlow.value = Resource.Loading
        val result = repository.login(email, password)
        _loginFlow.value = result
    }

    fun signup(name: String, email: String, password: String) = viewModelScope.launch {
        _signUpFlow.value = Resource.Loading
        val result = repository.signup(name, email, password)

        _signUpFlow.value = result
    }
    fun logout() {
        repository.logout()
        _loginFlow.value=null
        _signUpFlow.value=null
    }
}
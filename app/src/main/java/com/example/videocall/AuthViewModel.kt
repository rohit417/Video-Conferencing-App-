package com.example.videocall

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _authResult = MutableLiveData<Result<FirebaseUser>>()
    val authResult: LiveData<Result<FirebaseUser>> = _authResult

    fun signUp(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) _authResult.postValue(Result.success(auth.currentUser!!))
                else _authResult.postValue(Result.failure(task.exception!!))
            }
    }

    fun login(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) _authResult.postValue(Result.success(auth.currentUser!!))
                else _authResult.postValue(Result.failure(task.exception!!))
            }
    }
}

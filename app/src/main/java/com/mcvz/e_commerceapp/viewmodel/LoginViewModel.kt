package com.mcvz.e_commerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mcvz.e_commerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth:FirebaseAuth

) :  ViewModel() {
    private val pLogin= MutableSharedFlow<Resource<FirebaseUser>>()
    val login= pLogin.asSharedFlow()    //Sadece okunabilir ama yazılamaz

    private val pResetPassword=MutableSharedFlow<Resource<String>>()
    val resetPassword=pResetPassword.asSharedFlow()

    fun login(email:String,password:String){
        viewModelScope.launch { pLogin.emit(Resource.Loading()) }
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    it.user?.let {
                        pLogin.emit(Resource.Success(it))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    pLogin.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun resetPassword(email: String){
        viewModelScope.launch {
            pResetPassword.emit(Resource.Loading())
        }

            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    viewModelScope.launch {
                    pResetPassword.emit(Resource.Success(email))
                }

                }.addOnFailureListener {viewModelScope.launch {
                    pResetPassword.emit(Resource.Error(it.message.toString()))
                }
                }
        }
    }
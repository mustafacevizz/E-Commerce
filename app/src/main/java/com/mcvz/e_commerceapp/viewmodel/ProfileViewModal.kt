package com.mcvz.e_commerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.mcvz.e_commerceapp.data.User
import com.mcvz.e_commerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModal @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth

) :ViewModel() {

    private val pUser= MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user=pUser.asStateFlow()

    init {
        getUser()
    }

    fun getUser(){
        viewModelScope.launch {
            pUser.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).addSnapshotListener { value, error ->
            if (error!=null){
                viewModelScope.launch {
                    pUser.emit(Resource.Error(error.message.toString()))
                }
            }else{
                val user=value?.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        pUser.emit(Resource.Success(user))
                    }
                }
            }
        }
    }

    fun logout(){
        auth.signOut()
    }

}
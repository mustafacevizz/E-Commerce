package com.mcvz.e_commerceapp.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.StorageReference
import com.mcvz.e_commerceapp.EcommerceApplication
import com.mcvz.e_commerceapp.data.User
import com.mcvz.e_commerceapp.util.RegisterValidation
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.util.validateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModal @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app:Application
): AndroidViewModel(app){

    private val pUser= MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user=pUser.asStateFlow()

    private val pUpdateInfo=MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo=pUpdateInfo.asStateFlow()


    init {
        getUser()
    }
    fun getUser(){
        viewModelScope.launch {
            pUser.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).get().addOnSuccessListener {
            val user=it.toObject(User::class.java)
            user?.let {
                viewModelScope.launch {
                    pUser.emit(Resource.Success(it))
                }

            }
        }.addOnFailureListener {
            viewModelScope.launch {
                pUser.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    fun updateUser(user: User,imageUri: Uri?){
        val areInputsValid= validateEmail(user.email) is RegisterValidation.Success && user.firstName.trim().isNotEmpty()
                &&user.lastName.trim().isNotEmpty()
        if (!areInputsValid){
            viewModelScope.launch {
                pUser.emit(Resource.Error("Lütfen bilgilerinizi kontrol ediniz"))
            }
            return
        }

        viewModelScope.launch {
            pUpdateInfo.emit(Resource.Loading())
        }

        if (imageUri==null){
            saveUserInformation(user,true)
        }else{
            saveUserInformationWithNewImage(user,imageUri)
        }
    }

    private fun saveUserInformationWithNewImage(user: User,imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val imageBitmap=MediaStore.Images.Media.getBitmap(getApplication<EcommerceApplication>().contentResolver,imageUri)
                val byteArrayOutputStream=ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG,96,byteArrayOutputStream)
                val imageByteArray=byteArrayOutputStream.toByteArray()
                val imageDirectory=storage.child("profileImages/${auth.uid}/${UUID.randomUUID().toString()}")
                val result=imageDirectory.putBytes(imageByteArray).await()
                val imageUrl=result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = imageUrl),false)

            }catch (e:Exception){
                viewModelScope.launch {
                    pUpdateInfo.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun saveUserInformation(user: User, retrieveOldImg: Boolean) {
        firestore.runTransaction {transaction->
            val documentRef=firestore.collection("user").document(auth.uid!!)
            if (retrieveOldImg){
                val currentUser=transaction.get(documentRef).toObject(User::class.java)
                val newUser=user.copy(imagePath = currentUser!!.imagePath)
                transaction.set(documentRef,newUser)
            }else{
                transaction.set(documentRef,user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                pUpdateInfo.emit(Resource.Success(user))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                pUpdateInfo.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}
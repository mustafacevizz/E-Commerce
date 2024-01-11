package com.mcvz.e_commerceapp.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mcvz.e_commerceapp.data.User
import com.mcvz.e_commerceapp.fragments.RegisterFragment
import com.mcvz.e_commerceapp.util.Constans.USER_COLLECTION
import com.mcvz.e_commerceapp.util.RegisterFieldsState
import com.mcvz.e_commerceapp.util.RegisterValidation
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.util.validateEmail
import com.mcvz.e_commerceapp.util.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db:FirebaseFirestore
):ViewModel(){

    private val Pregister =MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register:Flow<Resource<User>> = Pregister

    private val Pvalidation= Channel<RegisterFieldsState>() //Channel veri iletişimini sağlar bir taraf veri gönderir diğeri alır
    val validation=Pvalidation.receiveAsFlow()  //Dinleme yapar ve değerleri dışarı aktarır


    fun createAccountWithEmailAndPassword(user: User,password:String){
        if(checkValidation(user, password)) {   //Doğru olup olmadığı kontrol edilir
            runBlocking {
                Pregister.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password).addOnSuccessListener {
                it.user?.let {
                    saveUserInfo(it.uid,user)   //SaveUserInfo ile kullanıcı bilgileri firestore'a kaydedilir

                }

            }.addOnFailureListener {
                Pregister.value = Resource.Error(it.message.toString()) //hata mesajı yolladık
            }
        }
        else{
            val registerFieldsState=RegisterFieldsState(    //yanlış değerler girildiği için kontrol edilmesi için pvalidationa gönderilir
                validateEmail(user.email),validatePassword(password)
            )
            runBlocking {
                Pvalidation.send(registerFieldsState)
            }
        }
    }

    private fun saveUserInfo(userUid: String,user: User) {  //userUid ile firestorea kullanıcıyı kaydeder
        db.collection(USER_COLLECTION)  //USER_COLLECTION'ın içine kullanıcının uid belgesine verileri kaydeder
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                Pregister.value = Resource.Success(user)
            }.addOnFailureListener {
                Pregister.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String): Boolean{ //Bilgilerin doğruluğu kontrol edilir
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegisterValidation.Success  //Doğrulamaların durumu kontrol edilir
                && passwordValidation is RegisterValidation.Success
        return shouldRegister
    }
}
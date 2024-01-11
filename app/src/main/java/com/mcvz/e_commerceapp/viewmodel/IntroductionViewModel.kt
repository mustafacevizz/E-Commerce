package com.mcvz.e_commerceapp.viewmodel

import android.accounts.Account
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.util.Constans.INTRODUCTION_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
):ViewModel() {

    private val pNavigate=MutableStateFlow(0)
    val navigate:StateFlow<Int> = pNavigate

    companion object{
        const val SHOPPING_ACTIVITY=23
        val ACCOUNT_OPTIONS_FRAGMENT = R.id.action_introductionFragment_to_accountOptionsFragment   //Geçiş
    }

    init {
        val isButtonClicked=sharedPreferences.getBoolean(INTRODUCTION_KEY,false)    //Kullanıcı butona tıkladı tıklamadı kontrolü
        val user=firebaseAuth.currentUser       //Mevcut oturum açmış kullanıcı kontrolü

        if (user!=null){
            viewModelScope.launch {
                pNavigate.emit(SHOPPING_ACTIVITY)
            }
        }else if(isButtonClicked){
            viewModelScope.launch {
                pNavigate.emit(ACCOUNT_OPTIONS_FRAGMENT)    //Kullanıcı butona tıkladıysa geçiş yapar
            }

        }else{
            Unit
        }
    }
    fun startButtonClick(){
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY,true).apply()  //Kullanıcının uygulamayı ilk kez açtığını anlarız
    }
}
package com.mcvz.e_commerceapp.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mcvz.e_commerceapp.data.Address
import com.mcvz.e_commerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModal @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) :ViewModel() {

    private val pAddNewAddress= MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress=pAddNewAddress.asStateFlow()

    private val pError= MutableSharedFlow<String>()
    val error=pError.asSharedFlow()

    fun addAddress(address: Address){
        val validateInput=validateInput(address)

        if (validateInput) {
            viewModelScope.launch { pAddNewAddress.emit(Resource.Loading()) }
            firestore.collection("user").document(auth.uid!!).collection("address").document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch { pAddNewAddress.emit(Resource.Success(address)) }
                }.addOnFailureListener {
                    viewModelScope.launch { pAddNewAddress.emit(Resource.Error(it.message.toString())) }
                }
        }else{
            viewModelScope.launch {
                pError.emit("Tüm alanlar doldurulmalıdır")
            }
        }

    }

    private fun validateInput(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty()&&
                address.city.trim().isNotEmpty()&&
                address.phone.trim().isNotEmpty()&&
                address.state.trim().isNotEmpty()&&
                address.fullName.trim().isNotEmpty()&&
                address.street.trim().isNotEmpty()

    }
}
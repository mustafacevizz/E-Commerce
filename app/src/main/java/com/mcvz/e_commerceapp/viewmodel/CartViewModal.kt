package com.mcvz.e_commerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.mcvz.e_commerceapp.data.CartProduct
import com.mcvz.e_commerceapp.firebase.FirebaseCommon
import com.mcvz.e_commerceapp.helper.getProductPrice
import com.mcvz.e_commerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModal @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
):ViewModel() {
    private val pCartProduct= MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProduct=pCartProduct.asStateFlow()
    private var cartProductDocument= emptyList<DocumentSnapshot>()
    val productsPrice=cartProduct.map {
        when(it){
            is Resource.Success->{
                priceCalculator(it.data!!)
            }
            else-> null
        }
    }
    private val pDeleteDialog= MutableSharedFlow<CartProduct>()
    val deleteDialog=pDeleteDialog.asSharedFlow()

    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = pCartProduct.value.data?.indexOf(cartProduct)
        if (index!=null&&index!=-1) {
            val documentId = cartProductDocument[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart").document(documentId).delete()
        }
    }



    private fun priceCalculator(data: List<CartProduct>): Float {
        return data.sumByDouble {
            (it.product.offerPercentage.getProductPrice(it.product.price)*it.amount).toDouble()
        }.toFloat()
    }

    init {
        getCartProduct()
    }

    private fun getCartProduct(){
        viewModelScope.launch { pCartProduct.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart").addSnapshotListener{
            value,error->
            if (error!=null||value==null){
                viewModelScope.launch { pCartProduct.emit(Resource.Error(error?.message.toString())) }
            }else{
                cartProductDocument=value.documents
                val cartProduct=value.toObjects(CartProduct::class.java)
                viewModelScope.launch { pCartProduct.emit(Resource.Success(cartProduct)) }
            }
        }
    }



    fun changeAmount(cartProduct: CartProduct, amountChanging: FirebaseCommon.AmountChanging){


        val index = pCartProduct.value.data?.indexOf(cartProduct)



        if (index!=null&&index!=-1) {
            val documentId = cartProductDocument[index].id
            when (amountChanging) {
                FirebaseCommon.AmountChanging.INCREASE -> {
                viewModelScope.launch { pCartProduct.emit(Resource.Loading()) }
                increaseAmount(documentId)
            }
            FirebaseCommon.AmountChanging.DECREASE->{
                if (cartProduct.amount==1){
                    viewModelScope.launch{
                        pDeleteDialog.emit(cartProduct)
                    }
                    return
                }
                viewModelScope.launch { pCartProduct.emit(Resource.Loading()) }
                decreaseAmount(documentId)
            }
        }
        }
    }

    private fun increaseAmount(documentId: String) {
        firebaseCommon.increaseTheAmount(documentId){result,exception->
            if (exception!=null){
                viewModelScope.launch{pCartProduct.emit(Resource.Error(exception.message.toString()))}
            }
        }
    }
    private fun decreaseAmount(documentId: String) {
        firebaseCommon.decreaseTheAmount(documentId){result,exception->
            if (exception!=null){
                viewModelScope.launch{pCartProduct.emit(Resource.Error(exception.message.toString()))}
            }
        }
    }

}
package com.mcvz.e_commerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mcvz.e_commerceapp.data.Product
import com.mcvz.e_commerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel@Inject constructor(
    private val firestore: FirebaseFirestore
):ViewModel() {
    private val pSepicalProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = pSepicalProducts
    private val pBestDealsProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts: StateFlow<Resource<List<Product>>> = pBestDealsProducts
    private val pBestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = pBestProducts
    private val pagingInfo=PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProduct()
    }

    fun fetchSpecialProducts() {
        viewModelScope.launch {
            pSepicalProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", "Special Products").get() //Firestore sorgusu categorydeki Special Productları getir
            .addOnSuccessListener { result ->
                val specialproductsList = result.toObjects(Product::class.java) //Special product listesine product sınıfına dönüştürerek atar
                viewModelScope.launch {
                    pSepicalProducts.emit(Resource.Success(specialproductsList))
                }

            }.addOnFailureListener {
            viewModelScope.launch {
                pSepicalProducts.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    fun fetchBestDeals() {
        viewModelScope.launch {
            pBestDealsProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", "Best Deals").get()   //Firestore sorgusu categorydeki Best Deals getir
            .addOnSuccessListener { result ->
                val bestDealsProducts = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    pBestDealsProducts.emit(Resource.Success(bestDealsProducts))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    pBestDealsProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProduct(){
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                pBestProducts.emit(Resource.Loading())
            }
            firestore.collection("Products")
                .limit(pagingInfo.bestProductsPage * 10).get()  //Productstaki en iyi 10 ürünü çeker
                .addOnSuccessListener { result ->
                    val bestProducts = result.toObjects(Product::class.java)    //Product sınıfına dönüştürerek best producta ekler
                    pagingInfo.isPagingEnd = bestProducts == pagingInfo.oldBestProducts //Şuanki ürünün önceki sayfadakiyle aynı olup olmadığı kontrolü
                    pagingInfo.oldBestProducts = bestProducts   //Şuanki ürünle önceki sayfadaki ürünlerin karışmaması için saklar
                    viewModelScope.launch {
                        pBestProducts.emit(Resource.Success(bestProducts))
                    }
                    pagingInfo.bestProductsPage++       //Sayfa dolduysa ve hala gözükmeyen ürün varsa sayfa sayısını arttırır
                }.addOnFailureListener {
                    viewModelScope.launch {
                        pBestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}
internal data class PagingInfo(
    var bestProductsPage:Long=1,
    var oldBestProducts:List<Product> = emptyList(),
    var isPagingEnd:Boolean=false
)
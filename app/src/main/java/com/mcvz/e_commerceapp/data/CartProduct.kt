package com.mcvz.e_commerceapp.data

data class CartProduct(
    val product: Product,
    val amount: Int,
    val selectedColor: Int?=null,
    val selectedSize: String?=null
){
    constructor():this(Product(),1,null,null)
}

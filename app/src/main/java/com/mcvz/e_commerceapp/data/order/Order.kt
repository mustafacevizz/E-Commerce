package com.mcvz.e_commerceapp.data.order

import com.mcvz.e_commerceapp.data.Address
import com.mcvz.e_commerceapp.data.CartProduct
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

data class Order(
    val orderStatus:String,
    val totalPrice:Float=0f,
    val products:List<CartProduct> = emptyList(),
    val address:Address= Address(),
    val date:String=SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Date()),
    val orderId:Long = nextLong(0,100_000_000_000) +totalPrice.toLong()
){
    constructor() : this(
        orderStatus = "",
        totalPrice = 0f,
        products = emptyList(),
        address = Address(),
        date = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Date()),
        orderId = nextLong(0, 100_000_000_000)
    )
}

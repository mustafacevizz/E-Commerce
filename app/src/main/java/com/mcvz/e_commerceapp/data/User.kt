package com.mcvz.e_commerceapp.data

data class User (
    val firstName:String,
    val lastName:String,
    val email:String,
    val imagePath:String="",
    val categories:List<CategoryItem> = emptyList()


    ){
    constructor():this("","","","", emptyList())
}
data class CategoryItem(
    val id: Int?=0,
    val name: String?=null
)

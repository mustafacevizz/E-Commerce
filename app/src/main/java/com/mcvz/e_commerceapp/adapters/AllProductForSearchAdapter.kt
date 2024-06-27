package com.mcvz.e_commerceapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.data.Product
import com.mcvz.e_commerceapp.fragments.shopping.ProductDetailsFragment
import com.mcvz.e_commerceapp.fragments.shopping.SearchFragment
import com.squareup.picasso.Picasso
import java.util.ArrayList

class AllProductForSearchAdapter(private var productList: List<Product>, private val fragment: Fragment
):RecyclerView.Adapter<AllProductForSearchAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val imageProduct:ImageView=itemView.findViewById(R.id.productImage)
        val nameProduct:TextView=itemView.findViewById(R.id.productName)
        val priceProduct:TextView=itemView.findViewById(R.id.productPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.all_product_rv_item,parent,false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.nameProduct.text = product.name
        holder.priceProduct.text = "${product.price} TL"
        if (product.images.isNotEmpty()) {
            Picasso.get().load(product.images[0]).into(holder.imageProduct)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
    fun searchDataList(searchlist:List<Product>){
        productList=searchlist
        notifyDataSetChanged()
    }

}
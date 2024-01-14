package com.mcvz.e_commerceapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mcvz.e_commerceapp.data.Product
import com.mcvz.e_commerceapp.databinding.ProductRvItemBinding

class UserCategoryAdapter: RecyclerView.Adapter<UserCategoryAdapter.UserCategoryViewHolder>() {
    inner class UserCategoryViewHolder(private val binding: ProductRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * product.price
                    tvNewPrice.text = "TL ${String.format("%.2f", priceAfterOffer)}"
                    tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                if (product.offerPercentage == null) {
                    tvNewPrice.visibility = View.INVISIBLE
                }

                tvPrice.text = "TL ${product.price}"
                tvName.text = product.name
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCategoryViewHolder {
        return UserCategoryViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: UserCategoryViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
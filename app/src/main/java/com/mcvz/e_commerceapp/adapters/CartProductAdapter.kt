package com.mcvz.e_commerceapp.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mcvz.e_commerceapp.data.CartProduct
import com.mcvz.e_commerceapp.data.Product
import com.mcvz.e_commerceapp.databinding.CartProductItemBinding
import com.mcvz.e_commerceapp.helper.getProductPrice

class CartProductAdapter: RecyclerView.Adapter<CartProductAdapter.CartProductViewHolder>() {
    inner class CartProductViewHolder(val binding: CartProductItemBinding) : RecyclerView
    .ViewHolder(binding.root) {
        fun bind(cartProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = cartProduct.product.name
                tvCartProductQuantity.text = cartProduct.amount.toString()

                val priceAfterPercentage =
                    cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
                tvProductCartPrice.text = "${String.format("%.2f", priceAfterPercentage)} TL"

                imageCartProductColor.setImageDrawable(
                    ColorDrawable(
                        cartProduct.selectedColor ?: Color.TRANSPARENT
                    )
                )
                tvCartProductSize.text = cartProduct.selectedSize ?: "".also {
                    imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }
    }

        private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
            override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
                return oldItem.product.id == newItem.product.id
            }

            override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
                return oldItem == newItem
            }
        }
        val differ = AsyncListDiffer(this, diffCallback)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
            return CartProductViewHolder(
                CartProductItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
            val cartProduct = differ.currentList[position]
            holder.bind(cartProduct)

            holder.itemView.setOnClickListener {
                onProductClick?.invoke(cartProduct)
            }
            holder.binding.imagePlus.setOnClickListener {
                onPlusClick?.invoke(cartProduct)
            }
            holder.binding.imageMinus.setOnClickListener {
                onMinusClick?.invoke(cartProduct)
            }
        }

        override fun getItemCount(): Int {
            return differ.currentList.size
        }

        var onProductClick: ((CartProduct) -> Unit)? = null
        var onPlusClick: ((CartProduct) -> Unit)? = null
        var onMinusClick: ((CartProduct) -> Unit)? = null
    }


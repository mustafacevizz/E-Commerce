package com.mcvz.e_commerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.mcvz.e_commerceapp.databinding.ViewpagerImageBinding

class ViewPagerImages:RecyclerView.Adapter<ViewPagerImages.ViewPagerImagesViewHolder>() {
    inner class ViewPagerImagesViewHolder(val binding:ViewpagerImageBinding):ViewHolder(binding.root){
        fun bind(imagePath:String){
            Glide.with(itemView).load(imagePath).into(binding.imageProductDetails)
        }
    }
    private val diffCallback=object:DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem==newItem
        }
    }
    val differ = AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerImagesViewHolder {
        return ViewPagerImagesViewHolder(
            ViewpagerImageBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerImagesViewHolder, position: Int) {
        val image=differ.currentList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
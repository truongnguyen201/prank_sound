package com.hola360.pranksounds.ui.sound_funny.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.SoundCategory
import com.hola360.pranksounds.databinding.ItemSoundCategoryBinding
import com.hola360.pranksounds.utils.Constants

class SoundCategoryAdapter(
    private val onItemClick: (soundCategory: SoundCategory) -> Unit
) :
    RecyclerView.Adapter<SoundCategoryAdapter.CategoryViewHolder>() {
    val data = mutableListOf<SoundCategory>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<SoundCategory>?) {
        data.clear()
        if (!newData.isNullOrEmpty()) {
            data.addAll(newData)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemSoundCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class CategoryViewHolder(private val binding: ItemSoundCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val category = data[position]
            binding.tvTitle.text = category.title
            binding.ivThumbnail.let {
                Glide.with(it)
                    .load(Constants.SUB_URL + category.thumbUrl)
                    .placeholder(R.drawable.smaller_loading)
                    .error(R.drawable.default_thumbnail)
                    .into(it)
            }
            binding.root.setOnClickListener {
                onItemClick(category)
            }
        }
    }
}
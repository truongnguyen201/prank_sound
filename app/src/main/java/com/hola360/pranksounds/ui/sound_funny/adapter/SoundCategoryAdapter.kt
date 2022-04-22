package com.hola360.pranksounds.ui.sound_funny.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.pranksounds.databinding.ItemSoundCategoryBinding

class SoundCategoryAdapter(private val onSelect: (String) -> Unit) : RecyclerView.Adapter<SoundCategoryAdapter.CategoryViewHolder>() {

    private val data = listOf<String>(
        "Favorite Sound",
        "Category 2",
        "Category 3",
        "Category 4",
        "Category 5",
    )

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
            binding.tvTitle.text = data[position]
            binding.root.setOnClickListener{
                onSelect(data[position])
            }
        }
    }
}
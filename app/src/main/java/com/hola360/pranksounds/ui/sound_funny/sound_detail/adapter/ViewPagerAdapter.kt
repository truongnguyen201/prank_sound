package com.hola360.pranksounds.ui.sound_funny.sound_detail.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Predicate
import androidx.recyclerview.widget.RecyclerView
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.databinding.ItemSoundDetailBinding

class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.SoundDetailViewHolder>() {

    private val data = mutableListOf<Sound>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData: Array<Sound>?) {
        data.clear()
        if (!newData.isNullOrEmpty()) {
            data.addAll(newData)
        }
        filter(data) { i: Sound -> i.isBanner }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundDetailViewHolder {
        val binding =
            ItemSoundDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoundDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoundDetailViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class SoundDetailViewHolder(private val binding: ItemSoundDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val sound = data[position]
            if (!sound.isBanner) {
                binding.apply {
                    tvTitle.text = sound.title
                    ivThumbnail.setImageResource(sound.thumbRes)
                }
            } else {
                binding.root.visibility = View.INVISIBLE
            }
        }
    }

    private fun <T> filter(list: MutableList<T>, predicate: Predicate<T>) {
        for (i in list.indices.reversed())
        {
            if (predicate.test(list[i])) {
                list.removeAt(i)
            }
        }
    }
}
package com.hola360.pranksounds.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.pranksounds.data.model.Prank
import com.hola360.pranksounds.databinding.ItemPrankBinding

class PrankAdapter(private val onSelect: (Int) -> Unit) :
    RecyclerView.Adapter<PrankAdapter.PrankViewHolder>() {
    private var data = emptyList<Prank>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data : List<Prank>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrankViewHolder {
        val binding = ItemPrankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PrankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrankViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class PrankViewHolder(private val binding: ItemPrankBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvItem.text = data[position].text
            binding.ivItem.setImageResource(data[position].img)
            binding.root.setOnClickListener {
                onSelect(data[position].id)
            }
        }
    }

}
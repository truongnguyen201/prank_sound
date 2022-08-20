package com.hola360.pranksounds.ui.taser_prank.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.pranksounds.data.model.Taser
import com.hola360.pranksounds.databinding.ItemTrimmerBinding

class TaserPrankAdapter(private val optionSelect: OptionSelect) : RecyclerView.Adapter<TaserPrankAdapter.TaserViewHolder>() {
    private var data = emptyList<Taser>()

    fun setData(data: List<Taser>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaserViewHolder {
        val binding = ItemTrimmerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaserViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class TaserViewHolder(private val binding: ItemTrimmerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                clItem.setBackgroundResource(data[position].background)
                ivItem.setImageResource(data[position].img)
                root.setOnClickListener { optionSelect.onOptionSelected(position) }
            }
        }
    }

    interface OptionSelect {
        fun onOptionSelected(position: Int)
    }
}
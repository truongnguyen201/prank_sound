package com.hola360.pranksounds.ui.callscreen.callingscreen.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.ItemPanelBinding

class PanelAdapter() : RecyclerView.Adapter<PanelAdapter.PanelViewHolder>() {

    val titles = arrayListOf(
        "Add call", "Hold call", "Bluetooth", "Speaker", "Mute", "Keypad"
    )
    val icons = arrayListOf(
        R.drawable.ic_baseline_add_24,
        R.drawable.ic_baseline_hold_24,
        R.drawable.ic_baseline_bluetooth_24,
        R.drawable.ic_baseline_speaker_24,
        R.drawable.ic_baseline_mute_24,
        R.drawable.ic_baseline_keypad
    )
    val isClicked = arrayOf(false, false, false, false, false, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanelViewHolder {
        val binding = ItemPanelBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PanelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PanelViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    inner class PanelViewHolder(private val binding: ItemPanelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(position: Int) {
            binding.apply {
                tvText.text = titles[position]
                ivIcon.setImageResource(icons[position])
                if (isClicked[position]) {
                    ivIcon.setColorFilter(Color.parseColor("#84E361"))
                } else {
                    ivIcon.setColorFilter(Color.parseColor("#FFFFFF"))
                }

                root.setOnClickListener {
                    isClicked[position] = !isClicked[position]
                    notifyItemChanged(position)
                }
            }
        }
    }
}
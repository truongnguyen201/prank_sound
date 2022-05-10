package com.hola360.pranksounds.ui.callscreen.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.ItemCallBinding
import com.hola360.pranksounds.utils.Constants

class CallAdapter(private val onSelected: (Int) -> Unit) : RecyclerView.Adapter<CallAdapter.CallViewHolder>() {
    private var listData = listOf<Call>(
//        Call("Selena", "09834792", "kashf"),
//        Call("Daddy", "09834792", "kashf"),
//        Call("Cristiano Ronaldo", "09834792", "kashf"),
//        Call("Ariana", "09834792", "kashf"),
//        Call("Justin Bieber", "09834792", "kashf"),
//        Call("Selena", "09834792", "kashf"),
    )

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<Call>?) {
        if (!list.isNullOrEmpty())
            listData = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
        val binding = ItemCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CallViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class CallViewHolder(private val binding: ItemCallBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val call = listData[position]
            binding.apply {
                tvContactPersonName.text = call.name
                tvPhoneNumber.text = call.phone
                root.setOnClickListener {
                    onSelected(position)
                }
                ivAvatarCall.let { imgView->
                    Glide.with(imgView)
                        .load(Constants.SUB_URL + call.avatarUrl)
                        .placeholder(R.drawable.smaller_loading)
                        .error(R.drawable.img_avatar_call)
                        .into(imgView)
                }
                ivOptionMenu.setOnClickListener {
                    Log.e("TAG", "bind: $position")
                }
            }
        }
    }
}
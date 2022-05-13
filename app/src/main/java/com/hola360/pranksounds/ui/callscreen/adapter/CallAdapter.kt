package com.hola360.pranksounds.ui.callscreen.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.ItemCallBinding
import com.hola360.pranksounds.ui.callscreen.CallItemListener
import com.hola360.pranksounds.utils.Constants

class CallAdapter(private val onSelected: (Int) -> Unit) :
    RecyclerView.Adapter<CallAdapter.CallViewHolder>() {
    private var listData = listOf<Call>()
    lateinit var callItemListener: CallItemListener

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<Call>?) {
        if (!list.isNullOrEmpty())
            listData = list
        notifyDataSetChanged()
    }

    fun setListener(callItemListener: CallItemListener) {
        this.callItemListener = callItemListener
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

    inner class CallViewHolder(private val binding: ItemCallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(position: Int) {
            val call = listData[position]
            binding.apply {
                tvContactPersonName.text = call.name
                tvPhoneNumber.text = call.phone
                if (call.isLocal) {
                    if (call.avatarUrl != "") {
                        ivAvatarCall.setImageURI(Uri.parse(call.avatarUrl))
                    } else {
                        ivAvatarCall.setImageDrawable(
                            binding.root.context.resources.getDrawable(
                                R.drawable.img_avatar_default
                            )
                        )
                    }
                }
                else {
                    ivAvatarCall.let { imgView ->
                        Glide.with(imgView)
                            .load(Constants.SUB_URL + call.avatarUrl)
                            .placeholder(R.drawable.smaller_loading)
                            .error(R.drawable.img_avatar_default)
                            .into(imgView)
                    }
                }
                if (call.isLocal) {
                    icIsLocal.visibility = View.VISIBLE
                }
                root.setOnClickListener {
                    callItemListener.onItemClick(position)
                }
                ivOptionMenu.setOnClickListener {
                    callItemListener.onMoreClick(this.root, call)
                }
            }
        }
    }
}
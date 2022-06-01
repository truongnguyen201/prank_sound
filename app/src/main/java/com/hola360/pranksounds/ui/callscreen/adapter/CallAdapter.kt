package com.hola360.pranksounds.ui.callscreen.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.os.SystemClock
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
    private var mLastClickTime: Long = 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<Call>?, status: Int) {
        if (!list.isNullOrEmpty())
            listData = list
        if (status == 1) listData = listOf<Call>()
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
                    ivAvatarCall.let { imgView ->
                        Glide.with(imgView)
                            .load(call.avatarUrl)
                            .placeholder(R.drawable.img_avatar_default)
                            .error(R.drawable.img_avatar_default)
                            .into(imgView)
                    }
                }
                else {
                    ivAvatarCall.let { imgView ->
                        Glide.with(imgView)
                            .load(Constants.SUB_URL + call.avatarUrl)
                            .placeholder(R.drawable.img_avatar_default)
                            .error(R.drawable.img_avatar_default)
                            .into(imgView)
                    }
                }
                if (call.isLocal) {
                    icIsLocal.visibility = View.VISIBLE
                }
                else {
                    icIsLocal.visibility = View.GONE
                }
                root.setOnClickListener {
                    if (SystemClock.elapsedRealtime() - mLastClickTime > 500) {
                        callItemListener.onItemClick(call, position)
                    }
                    mLastClickTime = SystemClock.elapsedRealtime()

                }
                ivOptionMenu.setOnClickListener {
                    if (SystemClock.elapsedRealtime() - mLastClickTime > 500) {
                        callItemListener.onMoreClick(it, call)
                    }
                    mLastClickTime = SystemClock.elapsedRealtime()
                }
            }
        }
    }
}
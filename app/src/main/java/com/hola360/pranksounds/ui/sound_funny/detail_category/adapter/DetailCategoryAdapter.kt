package com.hola360.pranksounds.ui.sound_funny.detail_category.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.databinding.ItemBannerBinding
import com.hola360.pranksounds.databinding.ItemSoundBinding
import com.hola360.pranksounds.utils.listener.SoundListener

@SuppressLint("NotifyDataSetChanged")
class DetailCategoryAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val data = mutableListOf<Sound>()
    val favoriteData = mutableListOf<Sound>()
    private var currentPosition: Int? = null
    lateinit var listener: SoundListener

    fun updateData(isAddMore: Boolean, newData: List<Sound>?) {
        if (isAddMore) {
            val oldSize = data.size
            if (!newData.isNullOrEmpty()) {
                data.addAll(newData)
                notifyItemInserted(oldSize)
            }
        } else {
            data.clear()
            if (!newData.isNullOrEmpty()) {
                data.addAll(newData)
                notifyDataSetChanged()
            }
        }
    }

    fun updatePlayingItem(newPosition: Int, isPlaying: Boolean){
        if(currentPosition != null){
            data[currentPosition!!].isPlaying = false
            notifyItemChanged(currentPosition!!)
        }
        if(!data.isNullOrEmpty()){
            data[newPosition].isPlaying = isPlaying
            notifyItemChanged(newPosition)
            currentPosition = newPosition
        }
    }

    //update favorite data
    fun updateFavoriteData(newData: List<Sound>?) {
        if (!newData.isNullOrEmpty()) {
            favoriteData.clear()
            favoriteData.addAll(newData)
            notifyDataSetChanged()
        }
    }

    @JvmName("setListener1")
    fun setListener(listener: SoundListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == NORMAL_TYPE) {
            SoundViewHolder(
                ItemSoundBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            BannerViewHolder(
                ItemBannerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].isBanner) {
            BANNER_TYPE
        } else {
            NORMAL_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SoundViewHolder) {
            holder.bind(position)
        }
    }

    inner class SoundViewHolder(private val binding: ItemSoundBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val sound = data[position]
            binding.apply {
                ivThumbnail.setImageResource(sound.thumbRes)
                tvTitle.text = sound.title
                //show popup menu when click on icon more
                ivMore.setOnClickListener {
                    listener.onMoreIconClick(this.root, position)
                }
                //check if the sound is exists in favorite data, set isChecked of the checkbox is true
                //set listener when click on the checkbox
                cbFavorite.apply {
                    isChecked = sound in favoriteData
                    setOnClickListener {
                        if (cbFavorite.isChecked) {
                            listener.onCheckedButton(sound)
                        } else {
                            listener.onUncheckedButton(sound)
                        }
                    }
                }

                root.setOnClickListener {
                    listener.onItemClick(position)
                }

                ivPlayPause.setImageResource(
                    if (sound.isPlaying) {
                        R.drawable.ic_pause_arrow
                    } else {
                        R.drawable.ic_play_arrow
                    }
                )
            }
        }
    }

    inner class BannerViewHolder(private val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    companion object {
        const val BANNER_TYPE = 0
        const val NORMAL_TYPE = 1
    }
}

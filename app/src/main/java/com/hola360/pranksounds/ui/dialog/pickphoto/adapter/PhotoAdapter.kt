package com.hola360.pranksounds.ui.dialog.pickphoto.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.pranksounds.data.model.PhotoModel
import com.hola360.pranksounds.databinding.ItemAlbumBinding
import com.hola360.pranksounds.databinding.ItemPhotoBinding
import com.hola360.pranksounds.ui.dialog.pickphoto.data.PickModelDataType
import com.hola360.pranksounds.ui.dialog.pickphoto.data.PickPhotoModel

class PhotoAdapter(
    private val listener: ListenClickItem
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var pickPhotoModel: PickPhotoModel? = null


    @SuppressLint("NotifyDataSetChanged")
    fun update(pickPhotoModel: PickPhotoModel) {
        this.pickPhotoModel = pickPhotoModel
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (pickPhotoModel != null) {
            pickPhotoModel!!.pickModelDataType.ordinal
        } else {
            PickModelDataType.LoadAlbum.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == PickModelDataType.LoadAlbum.ordinal) {
            val itemBinding = ItemAlbumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            AlbumViewHolder(itemBinding)
        } else {

            val itemBinding = ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            PhotoViewHolder(itemBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AlbumViewHolder -> {
                holder.bind(position)
            }
            is PhotoViewHolder -> {
                holder.bind(position)
            }
        }
    }

    override fun getItemCount(): Int
    = if (pickPhotoModel != null) {
        pickPhotoModel!!.photoList.size
    }
    else {
        0
    }
    inner class AlbumViewHolder(itemView: ItemAlbumBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        internal val binding = itemView
        fun bind(position: Int) {
            binding.photoModel = pickPhotoModel!!.photoList[position]
            binding.myLayoutRoot.setOnClickListener {
                if (position < itemCount) {
                    listener.onClick(
                        position,
                        pickPhotoModel!!.pickModelDataType,
                        pickPhotoModel!!.photoList[position]
                    )
                }
            }
        }
    }

    inner class PhotoViewHolder(itemView: ItemPhotoBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        internal val binding = itemView
        fun bind(position: Int) {
            binding.photoModel = pickPhotoModel!!.photoList[position]
            binding.myLayoutRoot.setOnClickListener {
                if (position < itemCount) {
                    listener.onClick(
                        position,
                        pickPhotoModel!!.pickModelDataType,
                        pickPhotoModel!!.photoList[position]
                    )
                }
            }
        }
    }

    interface ListenClickItem {
        fun onClick(
            position: Int,
            pickModelDataType: PickModelDataType,
            photoModel: PhotoModel
        )
    }
}
package com.hola360.pranksounds.ui.home.section

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Prank
import com.hola360.pranksounds.ui.home.adapter.SlideAdapter
import com.smarteist.autoimageslider.SliderView
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters

class PrankSection constructor(
    sectionParameters: SectionParameters, bannerImage: List<Int>,
    itemList: List<Prank>, _clickListener: ClickListener
) :
    Section(sectionParameters) {

    private var itemData = itemList
    private val slideAdapter = SlideAdapter(bannerImage)
    private val clickListener = _clickListener

    override fun getContentItemsTotal(): Int {
        return itemData.size
    }

    override fun getItemViewHolder(view: View?): PrankViewHolder? {
        return view?.let { PrankViewHolder(it) }
    }

    override fun onBindItemViewHolder(holder: ViewHolder?, position: Int) {
        val prankHolder = holder as PrankViewHolder
        val prank = itemData[position]

        prankHolder.ivBackground.setImageResource(prank.background)
        prankHolder.tvItem.text = prank.text
        prankHolder.ivItem.setImageResource(prank.img)
        prankHolder.root.setOnClickListener {
            clickListener.onItemRootViewClicked(this, holder.adapterPosition)
        }
    }

    override fun onBindHeaderViewHolder(holder: ViewHolder?) {
        val headHolder = holder as HeaderViewHolder
        headHolder.sliderView.setSliderAdapter(slideAdapter)
        headHolder.sliderView.startAutoCycle()
    }

    override fun getHeaderViewHolder(view: View?): HeaderViewHolder? {
        return view?.let { HeaderViewHolder(it) }
    }

    inner class PrankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: View = itemView
        val ivBackground: ImageView = itemView.findViewById(R.id.ivBackground)
        val ivItem: AppCompatImageView = itemView.findViewById(R.id.ivItem)
        val tvItem: TextView = itemView.findViewById(R.id.tvItem)
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sliderView: SliderView = itemView.findViewById(R.id.imageSlider)
    }

    interface ClickListener {
        fun onItemRootViewClicked(section: PrankSection, itemAdapterPosition: Int)
    }
}
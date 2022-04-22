package com.hola360.pranksounds.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.hola360.pranksounds.R
import com.smarteist.autoimageslider.SliderViewAdapter

class SlideAdapter(private val images: List<Int>) : SliderViewAdapter<SlideAdapter.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup): Holder? {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slider, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(viewHolder: Holder, position: Int) {
        viewHolder.imageView.setImageResource(images[position])
    }

    override fun getCount(): Int {
        return images.size
    }

    class Holder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
    }
}
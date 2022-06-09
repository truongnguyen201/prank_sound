package com.hola360.pranksounds.ui.callscreen.popup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.hola360.pranksounds.databinding.ItemPopupActionBinding

class ActionAdapter(
    private val actions: MutableList<ActionModel>,
    private val onItemClickListener: OnActionListener?
) : BaseAdapter() {

    interface OnActionListener {
        fun onItemClickListener(position: Int)
    }

    override fun getCount(): Int = actions.size

    override fun getItem(position: Int): ActionModel {
        return actions[position]
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val actionHolder: ActionHolder
        if (view == null) {
            val binding =
                ItemPopupActionBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
            actionHolder = ActionHolder(binding)
            actionHolder.binding.root.tag = actionHolder
        } else {
            actionHolder = view.tag as ActionHolder
        }
        actionHolder.bind(position)
        return actionHolder.binding.root
    }

    inner class ActionHolder(val binding: ItemPopupActionBinding) {
        fun bind(position: Int) {
            binding.actionModel = actions[position]
            binding.myLayoutRoot.setOnClickListener {
                try {
                    onItemClickListener?.onItemClickListener(position)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }
}
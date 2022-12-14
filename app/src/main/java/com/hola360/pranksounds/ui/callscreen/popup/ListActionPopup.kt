package com.hola360.pranksounds.ui.callscreen.popup

import android.content.Context
import android.view.View
import android.widget.ListPopupWindow
import com.hola360.pranksounds.R

class ListActionPopup(private val context: Context) {
    private val popupMenu: ListPopupWindow = ListPopupWindow(context)
    private var isItemSelected = false;

    fun showPopup(
        anchor: View,
        array: MutableList<ActionModel>,
        onActionListener: ActionAdapter.OnActionListener?
        ) {
        isItemSelected = false
        val adapter = ActionAdapter(array, object : ActionAdapter.OnActionListener{
            override fun onItemClickListener(position: Int) {
                popupMenu.dismiss()
                if (!isItemSelected){
                    isItemSelected = true
                    onActionListener?.onItemClickListener(position)
                }

            }
        })
        popupMenu.setAdapter(adapter)
        popupMenu.width = context.resources.getDimension(R.dimen.popup_dialog_width).toInt()
        popupMenu.anchorView = anchor
        popupMenu.isModal = true
        popupMenu.show()
    }
}
package com.hola360.pranksounds.ui.dialog.confirmdelete

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.Display
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.App
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.ConfirmDeleteDialogBinding
import com.hola360.pranksounds.ui.callscreen.DeleteConfirmListener
import com.hola360.pranksounds.ui.dialog.base.BaseDialog


class ConfirmDeleteDialog(private val listener: DeleteConfirmListener, private val mCall: Call) :
    BaseDialog<ConfirmDeleteDialogBinding>() {
    lateinit var mViewModel: ConfirmDeleteDialogViewModel



    override fun getLayout(): Int {
        return R.layout.confirm_delete_dialog
    }

    override fun initViewModel() {
        val factory =
            ConfirmDeleteDialogViewModel.Factory(requireActivity().application as App, mCall)
        mViewModel = ViewModelProvider(this, factory)[ConfirmDeleteDialogViewModel::class.java]
    }

    override fun initView() {
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window?.let {
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                it.requestFeature(Window.FEATURE_NO_TITLE)
            }
        }

        with(binding) {
            viewModel = mViewModel
            btnOk.setOnClickListener {
                listener.onOkClick(mViewModel.getCall())
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val window = dialog!!.window
        val size = Point()

        val display: Display = window!!.windowManager.defaultDisplay
        display.getSize(size)

        val width: Int = size.x

        window.setLayout((width * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
    }

    companion object {
        fun create(listener: DeleteConfirmListener, call: Call): ConfirmDeleteDialog {
            return ConfirmDeleteDialog(listener, call)
        }
    }
}
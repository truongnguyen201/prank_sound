package com.hola360.pranksounds.ui.dialog.confirmdelete

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import android.view.Display
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.core.text.bold
import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.ConfirmDeleteDialogBinding
import com.hola360.pranksounds.ui.callscreen.DeleteConfirmListener
import com.hola360.pranksounds.ui.dialog.base.BaseDialog


class ConfirmDeleteDialog() : BaseDialog<ConfirmDeleteDialogBinding>() {
    private lateinit var listener: DeleteConfirmListener
    private var mCall: Call = Call()
    private lateinit var mViewModel: ConfirmDeleteDialogViewModel

    override fun getLayout(): Int {
        return R.layout.confirm_delete_dialog
    }

    override fun initViewModel() {
        val factory =
            ConfirmDeleteDialogViewModel.Factory(requireActivity().applicationContext, mCall)
        mViewModel = ViewModelProvider(this, factory)[ConfirmDeleteDialogViewModel::class.java]

        mViewModel.title.observe(this) {
            it.let {
                binding.tvMessage.text =
                    SpannableStringBuilder()
                    .append(requireContext().resources.getString(R.string.confirm_delete_start) + " ")
                    .bold { append(it) }
                    .append("?")
            }
        }
    }

    override fun initView() {
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window?.let {
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                it.requestFeature(Window.FEATURE_NO_TITLE)
            }
        }

        if (mCall.id == 0L) dismiss()

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

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onStop() {
        super.onStop()
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
        fun create(): ConfirmDeleteDialog {
            return ConfirmDeleteDialog()
        }
    }

    fun setOnClickListener(listener: DeleteConfirmListener, call: Call) {
        this.listener = listener
        this.mCall = call
    }
}
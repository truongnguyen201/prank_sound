package com.hola360.pranksounds.ui.dialog.confirmdelete

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.App
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.ConfirmDeleteDialogBinding
import com.hola360.pranksounds.ui.callscreen.DeleteConfirmListener
import com.hola360.pranksounds.ui.dialog.base.BaseDialog


class ConfirmDeleteDialog(private val listener: DeleteConfirmListener, private val mCall: Call) :
    BaseDialog<ConfirmDeleteDialogBinding>() {
    lateinit var mviewModel: ConfirmDeleteDialogViewModel


    override fun getLayout(): Int {
        return R.layout.confirm_delete_dialog
    }

    override fun initViewModel() {
        val factory = ConfirmDeleteDialogViewModel.Factory(requireActivity().application as App, mCall)
        mviewModel = ViewModelProvider(this, factory)[ConfirmDeleteDialogViewModel::class.java]
    }

    override fun initView() {
        with(binding) {
            viewModel = mviewModel
            btnOk.setOnClickListener {
                listener.onOkClick(mviewModel.getCall())
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }

        Log.e("----", "initView: ${mviewModel.getCall().name}", )
    }

    companion object {
        fun create(listener: DeleteConfirmListener, call: Call): ConfirmDeleteDialog {
            return ConfirmDeleteDialog(listener, call)
        }
    }
}
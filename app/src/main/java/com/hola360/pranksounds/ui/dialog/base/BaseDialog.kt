package com.hola360.pranksounds.ui.dialog.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hola360.pranksounds.R

abstract class BaseDialog<V: ViewDataBinding> : DialogFragment() {

    protected lateinit var binding: V
    private val isBindingInitialized get() = this::binding.isInitialized

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        binding.lifecycleOwner = this
        initView()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isBindingInitialized){
            binding.unbind()
        }
    }

    abstract fun initViewModel()

    abstract fun initView()

    abstract fun getLayout() : Int
}
package com.hola360.pranksounds.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class AbsBaseFragment<V : ViewDataBinding> : Fragment() {
    protected lateinit var binding: V
    private val isBindingInitialized get() = this::binding.isInitialized
    private var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (mView != null) {
            mView
        } else {
            binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
            binding.lifecycleOwner = this
            initView()
            mView = binding.root
            binding.root
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isBindingInitialized){
            binding.unbind()
        }
    }

    open fun onBackPressed(): Boolean {
        return true
    }

    abstract fun getLayout(): Int
    abstract fun initView()

}
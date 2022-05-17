package com.hola360.pranksounds.ui.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

@Suppress("SENSELESS_COMPARISON")
abstract class BaseFragment<V : ViewDataBinding> : Fragment() {
    protected lateinit var binding: V
    private var mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if(mView != null){
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
        binding.unbind()
    }

    open fun onBackPressed() : Boolean{
        return false
    }

    abstract fun getLayout(): Int
    abstract fun initView()
    abstract fun initViewModel()
}
package com.hola360.pranksounds.ui.callscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentCallerBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.callscreen.adapter.ViewPagerAdapter


class CallerFragment : BaseFragment<FragmentCallerBinding>() {
    private lateinit var action: Any
    override fun getLayout(): Int {
        return R.layout.fragment_caller
    }

    override fun initView() {
        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        binding.vpCaller.isSaveEnabled = false
        binding.vpCaller.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.vpCaller) { tab, position ->
            when(position) {
                0->{
                    tab.text = getString(R.string.trend_caller)
                }
                1->{
                    tab.text = getString(R.string.my_caller)
                }
            }
        }.attach()

        with(binding.tbCallScreen) {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.add_new_call -> {
                        action = CallScreenFragmentDirections.actionGlobalAddCallScreenFragment(null)
                        findNavController().navigate(action as NavDirections)
                        true
                    }
                    else -> false
                }
            }

            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun initViewModel() {
        doNothing()
    }

    fun doNothing() {

    }

}
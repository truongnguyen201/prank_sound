package com.hola360.pranksounds.ui.callscreen

import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentCallerBinding
import com.hola360.pranksounds.ui.base.AbsBaseFragment
import com.hola360.pranksounds.ui.base.BaseScreenWithViewModelFragment
import com.hola360.pranksounds.ui.callscreen.adapter.ViewPagerAdapter


class CallerFragment : BaseScreenWithViewModelFragment<FragmentCallerBinding>() {
    private lateinit var action: Any
    private val sharedViewModel by activityViewModels<CallScreenSharedViewModel>()
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
                        sharedViewModel.setCall(null)
                        sharedViewModel.setStatus(ShareViewModelStatus.AddCall)
                        action = CallerFragmentDirections.actionGlobalAddCallScreenFragment()
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

    override fun onResume() {
        if (sharedViewModel.isBackToMyCaller()) {
            binding.tabLayout.getTabAt(1)?.select()
        }else {
            binding.tabLayout.getTabAt(0)?.select()
        }
        sharedViewModel.setBackToMyCaller(false)
        super.onResume()
    }

    fun doNothing() {

    }

}
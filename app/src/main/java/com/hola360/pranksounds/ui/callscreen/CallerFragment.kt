package com.hola360.pranksounds.ui.callscreen

import android.util.Log
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.ResultDataResponse
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.FragmentCallerBinding
import com.hola360.pranksounds.ui.base.BaseScreenWithViewModelFragment
import com.hola360.pranksounds.ui.callscreen.adapter.ViewPagerAdapter
import com.hola360.pranksounds.ui.callscreen.data.ShareViewModelStatus


class CallerFragment : BaseScreenWithViewModelFragment<FragmentCallerBinding>(){
    private lateinit var action: Any

    //    private val sharedViewModel by activityViewModels<CallScreenSharedViewModel>()
    private lateinit var sharedViewModel: CallScreenSharedViewModel
    private lateinit var adapter: ViewPagerAdapter
    private val isAdapterInitialized get() = this::adapter.isInitialized

    override fun getLayout(): Int {
        return R.layout.fragment_caller
    }

    override fun initView() {
        if (!isAdapterInitialized) {
            adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        }
        binding.vpCaller.isSaveEnabled = false
        binding.vpCaller.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.vpCaller) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.trend_caller)
                }
                1 -> {
                    tab.text = getString(R.string.my_caller)
                }
            }
        }.attach()

//        onTabLayoutChange()

        with(binding.tbCallScreen) {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.add_new_call -> {
                        sharedViewModel.setResultData(ShareViewModelStatus.AddCall.ordinal, Call())
                        action = CallerFragmentDirections.actionGlobalAddCallScreenFragment()
                            .setIsAdd(true)
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
        sharedViewModel = CallScreenSharedViewModel.getInstance(mainActivity.application)
        sharedViewModel.resultLiveData.observe(this){
            it?.let {
                if (it.resultCode == ShareViewModelStatus.AddCall.ordinal){
                    val body = (it as ResultDataResponse.ResultDataSuccess).body

                }
            }
        }
    }

    private fun onTabLayoutChange() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateShareViewModel(tab?.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                doNothing()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                updateShareViewModel(tab?.position)
            }
        })
    }

    private fun updateShareViewModel(position: Int?) {
        when (position) {
            0 -> sharedViewModel.setBackToMyCaller(false)
            1 -> sharedViewModel.setBackToMyCaller(true)
            else -> sharedViewModel.setBackToMyCaller(false)
        }
    }
    private fun doNothing(){}

    override fun onResume() {
        if (sharedViewModel.isBackToMyCaller()) {
            binding.apply {
                tabLayout.getTabAt(1)?.select()
                vpCaller.setCurrentItem(tabLayout.selectedTabPosition, false)
            }
        }
        sharedViewModel.setBackToMyCaller(false)
        super.onResume()
    }
}
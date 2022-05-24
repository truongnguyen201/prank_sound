package com.hola360.pranksounds.ui.callscreen.trendcaller

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.ybq.android.spinkit.style.Circle
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.FragmentTrendCallerBinding
import com.hola360.pranksounds.ui.callscreen.CallScreenViewModel
import com.hola360.pranksounds.ui.callscreen.base.CallListBaseFragment

class TrendCallerFragment : CallListBaseFragment<FragmentTrendCallerBinding>() {
    private lateinit var trendCallerViewModel: TrendCallerViewModel

    override fun getLayout(): Int {
        return R.layout.fragment_trend_caller
    }

    override fun initView() {
        setUpProgressBar()
        binding.apply {
            rcvCall.adapter = callAdapter
            rcvCall.setHasFixedSize(true)
        }
        binding.viewModel = trendCallerViewModel

    }

    override fun initViewModel() {
        val factory = TrendCallerViewModel.Factory(requireActivity().application)
        trendCallerViewModel = ViewModelProvider(this, factory)[TrendCallerViewModel::class.java]
        trendCallerViewModel.phoneBookLiveData.observe(this) {
            if (it.loadingStatus == LoadingStatus.Success) {
                val phoneBook = (it as DataResponse.DataSuccess).body
                callAdapter.updateData(phoneBook, 0)
            } else if (it.loadingStatus == LoadingStatus.Error) {
                callAdapter.updateData(null, 0)
            }
        }
        try {
            trendCallerViewModel.getPhoneBook()
        } catch (e: Exception) {

        }
    }

    override fun getPhoneBook() {
        trendCallerViewModel.getPhoneBook()
    }

    override fun onResume() {
        super.onResume()
        getPhoneBook()
    }
    private fun setUpProgressBar() {
        val circle = Circle()
        circle.color = ContextCompat.getColor(requireActivity(), R.color.design_color)
        binding.contentLoadingProgressBar.indeterminateDrawable = circle
    }
}
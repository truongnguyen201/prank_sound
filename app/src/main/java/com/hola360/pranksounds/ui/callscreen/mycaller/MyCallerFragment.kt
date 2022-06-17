package com.hola360.pranksounds.ui.callscreen.mycaller

import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.github.ybq.android.spinkit.style.Circle
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.FragmentMyCallerBinding
import com.hola360.pranksounds.ui.callscreen.CallScreenSharedViewModel
import com.hola360.pranksounds.ui.callscreen.CallerFragmentDirections
import com.hola360.pranksounds.ui.callscreen.base.CallListBaseFragment
import com.hola360.pranksounds.ui.callscreen.data.ShareViewModelStatus

class MyCallerFragment : CallListBaseFragment<FragmentMyCallerBinding>() {
    private lateinit var action: Any
    private lateinit var myCallerViewModel: MyCallerViewModel
    private lateinit var sharedViewModel: CallScreenSharedViewModel

    override fun getLayout(): Int {
        return R.layout.fragment_my_caller
    }

    override fun initView() {
        super.initView()
        setUpProgressBar()
        binding.apply {
            viewModel = myCallerViewModel
            rcvCall.adapter = callAdapter
            rcvCall.setHasFixedSize(true)
            btnAdd.setOnClickListener {
                sharedViewModel.setResultData(ShareViewModelStatus.AddCall.ordinal, Call())
                action = CallerFragmentDirections.actionGlobalAddCallScreenFragment().setIsAdd(true)
                findNavController().navigate(action as NavDirections)
            }
        }
    }

    override fun initViewModel() {
        sharedViewModel = CallScreenSharedViewModel.getInstance(mainActivity.application)
        val factory = MyCallerViewModel.Factory(requireActivity().application)
        myCallerViewModel = ViewModelProvider(this, factory)[MyCallerViewModel::class.java]
        myCallerViewModel.phoneBookLiveData.observe(this) {
            if (it.loadingStatus == LoadingStatus.Success) {
                val phoneBook = (it as DataResponse.DataSuccess).body
                callAdapter.updateData(phoneBook, 0)
            } else if (it.loadingStatus == LoadingStatus.Error) {
                callAdapter.updateData(null, 1)
            }
        }
        try {
            myCallerViewModel.getPhoneBook()
        } catch (e: Exception) {

        }
    }

    override fun getPhoneBook() {
        myCallerViewModel.getPhoneBook()
    }

    private fun setUpProgressBar() {
        val circle = Circle()
        circle.color = ContextCompat.getColor(requireActivity(), R.color.design_color)
        binding.contentLoadingProgressBar.indeterminateDrawable = circle
    }

    override fun onResume() {
        super.onResume()
        getPhoneBook()
    }
}
package com.hola360.pranksounds.ui.callscreen.mycaller

import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.github.ybq.android.spinkit.style.Circle
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.databinding.FragmentMyCallerBinding
import com.hola360.pranksounds.ui.callscreen.CallScreenFragmentDirections
import com.hola360.pranksounds.ui.callscreen.base.CallListBaseFragment

class MyCallerFragment : CallListBaseFragment<FragmentMyCallerBinding>() {
    private lateinit var action: Any
    private lateinit var myCallerViewModel: MyCallerViewModel
    override fun getLayout(): Int {
        return R.layout.fragment_my_caller
    }

    override fun initView() {
        setUpProgressBar()
        binding.apply {
            rcvCall.adapter = callAdapter
            rcvCall.setHasFixedSize(true)
        }
        binding.viewModel = myCallerViewModel
        binding.btnAdd.setOnClickListener {
            action = CallScreenFragmentDirections.actionGlobalAddCallScreenFragment(null)
            findNavController().navigate(action as NavDirections)
        }
    }

    override fun initViewModel() {
        val factory = MyCallerViewModel.Factory(requireActivity().application)
        myCallerViewModel = ViewModelProvider(this, factory)[MyCallerViewModel::class.java]
        myCallerViewModel.phoneBookLiveData.observe(this) {
            if (it.loadingStatus == LoadingStatus.Success) {
                val phoneBook = (it as DataResponse.DataSuccess).body
                callAdapter.updateData(phoneBook)
            } else if (it.loadingStatus == LoadingStatus.Error) {
                callAdapter.updateData(null)
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
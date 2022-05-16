package com.hola360.pranksounds.ui.callscreen.mycaller

import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.databinding.FragmentMyCallerBinding
import com.hola360.pranksounds.ui.callscreen.base.CallListBaseFragment

class MyCallerFragment : CallListBaseFragment<FragmentMyCallerBinding>() {
    private lateinit var myCallerViewModel: MyCallerViewModel
    override fun getLayout(): Int {
        return R.layout.fragment_my_caller
    }

    override fun initView() {
        binding.apply {
            rcvCall.adapter = callAdapter
            rcvCall.setHasFixedSize(true)
        }
        binding.viewModel = myCallerViewModel
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

    override fun onResume() {
        super.onResume()
        getPhoneBook()
    }

}
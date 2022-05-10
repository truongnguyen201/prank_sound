package com.hola360.pranksounds.ui.callscreen

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.databinding.FragmentCallScreenBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.callscreen.adapter.CallAdapter

class CallScreenFragment : BaseFragment<FragmentCallScreenBinding>(){
    private lateinit var callScreenViewModel: CallScreenViewModel
    private lateinit var callAdapter: CallAdapter
    private lateinit var action: Any
    override fun getLayout(): Int {
        return R.layout.fragment_call_screen
    }

    override fun initView() {
        callAdapter = CallAdapter {
            handleOnclickItem(it)
        }
        binding.apply {
            rcvCall.adapter = callAdapter
            rcvCall.setHasFixedSize(true)
        }
        binding.viewModel = callScreenViewModel

        with(binding.tbCallScreen) {
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.add_new_call -> {
                        action = CallScreenFragmentDirections.actionGlobalAddCallScreenFragment()
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
        val factory = CallScreenViewModel.Factory(requireActivity().application)
        callScreenViewModel = ViewModelProvider(this, factory)[CallScreenViewModel::class.java]
        callScreenViewModel.phoneBookLiveData.observe(this) {
            if (it.loadingStatus == LoadingStatus.Success) {
                val phoneBook = (it as DataResponse.DataSuccess).body
                callAdapter.updateData(phoneBook)
            }
            else if (it.loadingStatus == LoadingStatus.Error) {
                callAdapter.updateData(null)
            }
        }
        try {
            callScreenViewModel.getPhoneBook()
        } catch (e: Exception) {

        }


    }

    private fun handleOnclickItem(p: Int) {

    }

    override fun onResume() {
        super.onResume()
        callScreenViewModel.getPhoneBook()
    }

}
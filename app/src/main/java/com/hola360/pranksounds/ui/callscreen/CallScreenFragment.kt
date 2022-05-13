package com.hola360.pranksounds.ui.callscreen

import android.content.Context
import android.content.res.Resources
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.FragmentCallScreenBinding
import com.hola360.pranksounds.databinding.PopUpCallMoreBinding
import com.hola360.pranksounds.databinding.PopUpWindowLayoutBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.callscreen.adapter.CallAdapter
import com.hola360.pranksounds.utils.Utils

class CallScreenFragment : BaseFragment<FragmentCallScreenBinding>(), CallItemListener {
    private lateinit var callScreenViewModel: CallScreenViewModel
    private lateinit var callAdapter: CallAdapter
    private lateinit var action: Any
    private lateinit var popUpWindow: PopupWindow
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels - 100
    override fun getLayout(): Int {
        return R.layout.fragment_call_screen
    }

    override fun initView() {
        callAdapter = CallAdapter {
            handleOnclickItem(it)
        }
        callAdapter.setListener(this)
        binding.apply {
            rcvCall.adapter = callAdapter
            rcvCall.setHasFixedSize(true)
        }
        binding.viewModel = callScreenViewModel


        with(binding.tbCallScreen) {
            setOnMenuItemClickListener {
                when (it.itemId) {
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
            } else if (it.loadingStatus == LoadingStatus.Error) {
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

    // set popup window when click more icon
    private fun setupPopUpWindow(isLocal: Boolean) {
        val popUpInflater =
            requireActivity().applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUpBinding = PopUpCallMoreBinding.inflate(popUpInflater)

        if (!isLocal) {
            popUpBinding.llDelete.visibility = View.GONE
        }


        popUpBinding.apply {
            llEdit.setOnClickListener {
                Toast.makeText(requireContext(), "edit", Toast.LENGTH_LONG).show()
            }

            llDelete.setOnClickListener{
                Toast.makeText(requireContext(), "delete", Toast.LENGTH_LONG).show()
            }
        }

        popUpWindow = PopupWindow(
            popUpBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 20F
            contentView.setOnClickListener { dismiss() }
        }
    }

    override fun onResume() {
        super.onResume()
        callScreenViewModel.getPhoneBook()
    }

    override fun onItemClick(position: Int) {
        action = CallScreenFragmentDirections.actionGlobalAddCallScreenFragment()
        findNavController().navigate(action as NavDirections)
    }

    override fun onMoreClick(view: View, call: Call) {
        setupPopUpWindow(call.isLocal)
        popUpWindow.showAsDropDown(
            view, (screenWidth * 0.68).toInt(),
            ((-view.height) * 0.7).toInt()
        )
    }

}
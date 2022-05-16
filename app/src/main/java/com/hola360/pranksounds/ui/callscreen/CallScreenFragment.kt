package com.hola360.pranksounds.ui.callscreen

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.repository.PhoneBookRepository
import com.hola360.pranksounds.databinding.FragmentCallScreenBinding
import com.hola360.pranksounds.databinding.PopUpCallMoreBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.callscreen.adapter.CallAdapter
import com.hola360.pranksounds.ui.callscreen.addcallscreen.AddCallScreenFragment
import kotlinx.coroutines.launch


class CallScreenFragment : BaseFragment<FragmentCallScreenBinding>(), CallItemListener {
    private lateinit var callScreenViewModel: CallScreenViewModel
    private lateinit var repository: PhoneBookRepository
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
        repository = PhoneBookRepository(requireActivity().application)
        binding.apply {
            rcvCall.adapter = callAdapter
            rcvCall.setHasFixedSize(true)
        }
        binding.viewModel = callScreenViewModel


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
    private fun setupPopUpWindow(call: Call) {
        val popUpInflater =
            requireActivity().applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUpBinding = PopUpCallMoreBinding.inflate(popUpInflater)

        if (!call.isLocal) {
            popUpBinding.llDelete.visibility = View.GONE
        }


        popUpBinding.apply {
            llEdit.setOnClickListener {
                passCallToUpDate(call)
            }
            llDelete.setOnClickListener{
                setUpAlertDialog(call)
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

    override fun onItemClick(call: Call,position: Int) {
        action = CallScreenFragmentDirections.actionGlobalAddCallScreenFragment(call)
        findNavController().navigate(action as NavDirections)
    }

    override fun onMoreClick(view: View, call: Call) {
        setupPopUpWindow(call)
        popUpWindow.showAsDropDown(
            view, (screenWidth * 0.68).toInt(),
            ((-view.height) * 0.7).toInt()
        )
    }

    private fun setUpAlertDialog(call: Call) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(requireActivity().resources.getString(R.string.confirm_delete))
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                lifecycleScope.launch {
                    repository.deleteCall(call)
                    callScreenViewModel.getPhoneBook()
                }
                Toast.makeText(requireContext(), requireActivity().resources.getString(R.string.delete_complete), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
        popUpWindow.dismiss()
    }
    private fun passCallToUpDate(call: Call) {
        val ft: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        val addCallFragment = AddCallScreenFragment()

        val bundle = Bundle()
        bundle.putSerializable("call", call)
        addCallFragment.arguments = bundle
        ft.replace(android.R.id.content, addCallFragment)
        ft.addToBackStack(null)
        ft.commit()
    }

}
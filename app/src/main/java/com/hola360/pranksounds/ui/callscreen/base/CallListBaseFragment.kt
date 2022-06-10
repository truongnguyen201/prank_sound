package com.hola360.pranksounds.ui.callscreen.base

import android.annotation.SuppressLint
import android.view.View
import android.widget.PopupWindow
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.repository.PhoneBookRepository
import com.hola360.pranksounds.ui.base.BaseScreenWithViewModelFragment
import com.hola360.pranksounds.ui.callscreen.*
import com.hola360.pranksounds.ui.callscreen.adapter.CallAdapter
import com.hola360.pranksounds.ui.callscreen.popup.ActionAdapter
import com.hola360.pranksounds.ui.callscreen.popup.ListActionPopup
import com.hola360.pranksounds.ui.dialog.confirmdelete.ConfirmDeleteDialog
import com.hola360.pranksounds.utils.Utils
import kotlinx.coroutines.launch

abstract class CallListBaseFragment<V : ViewDataBinding> : BaseScreenWithViewModelFragment<V>(),
    CallItemListener, DeleteConfirmListener {
    private lateinit var repository: PhoneBookRepository
    protected var callAdapter: CallAdapter = CallAdapter { handleOnclickItem(it) }
    private lateinit var action: Any
    private lateinit var popUpWindow: PopupWindow

    //    private val sharedViewModel by activityViewModels<CallScreenSharedViewModel>()
    private lateinit var sharedViewModel: CallScreenSharedViewModel
    private val isSharedViewModelInitialized get() = this::sharedViewModel.isInitialized
    private val listActionPopup by lazy { ListActionPopup(requireContext()) }
    private lateinit var navHostFragment: Fragment

    override fun initView() {
        callAdapter.setListener(this)
        repository = PhoneBookRepository(requireActivity().application)

        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.navHostFragmentContentMain)!!

        if (!isSharedViewModelInitialized) {
            sharedViewModel = CallScreenSharedViewModel.getInstance(mainActivity.application)
        }
    }

    override fun initViewModel() {
        sharedViewModel = CallScreenSharedViewModel.getInstance(mainActivity.application)
    }

    private fun handleOnclickItem(p: Int) {}

    private fun setupPopUpWindow(view: View, call: Call) {
        listActionPopup.showPopup(
            view,
            Utils.getActionPopup(call.isLocal, requireContext()),
            object : ActionAdapter.OnActionListener {
                override fun onItemClickListener(position: Int) {
                    when (position) {
                        0 -> {
                            passCallToUpDate(call)
                        }
                        1 -> {
                            setUpAlertDialog(call)
                        }
                    }
                }
            })
    }

    private fun setUpAlertDialog(call: Call) {
        val dialog = ConfirmDeleteDialog.create(this, call)
        dialog.show(parentFragmentManager, "")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOkClick(call: Call) {
        lifecycleScope.launch {
            repository.deleteCall(call)
            getPhoneBook()
            callAdapter.notifyDataSetChanged()
        }
        mainActivity.showToast(getString(R.string.delete_complete))
    }

    private fun passCallToUpDate(call: Call) {
        sharedViewModel.setStatus(ShareViewModelStatus.EditCall)
        sharedViewModel.setCall(call)
        action = CallerFragmentDirections.actionGlobalAddCallScreenFragment().setCallModel(call)
        findNavController().navigate(action as NavDirections)
    }

    override fun onItemClick(call: Call, position: Int) {
        sharedViewModel.setStatus(ShareViewModelStatus.SetCall)
        sharedViewModel.setCall(call)
        action = CallerFragmentDirections.actionGlobalSetupCallFragment()
        findNavController().navigate(action as NavDirections)
    }

    override fun onMoreClick(view: View, call: Call) {
        setupPopUpWindow(view, call)
    }

    abstract fun getPhoneBook()
}
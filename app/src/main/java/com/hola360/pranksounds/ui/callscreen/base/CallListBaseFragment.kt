package com.hola360.pranksounds.ui.callscreen.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.repository.PhoneBookRepository
import com.hola360.pranksounds.databinding.PopUpCallMoreBinding
import com.hola360.pranksounds.ui.callscreen.*
import com.hola360.pranksounds.ui.callscreen.adapter.CallAdapter
import com.hola360.pranksounds.ui.callscreen.addcallscreen.AddCallScreenFragment
import com.hola360.pranksounds.ui.callscreen.popup.ActionAdapter
import com.hola360.pranksounds.ui.callscreen.popup.ListActionPopup
import com.hola360.pranksounds.ui.dialog.confirmdelete.ConfirmDeleteDialog
import com.hola360.pranksounds.utils.Utils
import kotlinx.coroutines.launch

abstract class CallListBaseFragment<V : ViewDataBinding> : Fragment(), CallItemListener, DeleteConfirmListener {
    protected lateinit var binding: V
    val isBindingInitialized get() = this::binding.isInitialized
    private lateinit var repository: PhoneBookRepository
    protected var callAdapter: CallAdapter= CallAdapter { handleOnclickItem(it) }
    private lateinit var action: Any
    private lateinit var popUpWindow: PopupWindow
    private val sharedViewModel by activityViewModels<CallScreenSharedViewModel>()

    private val listActionPopup by lazy { ListActionPopup(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        binding.lifecycleOwner = this
        callAdapter.setListener(this)
        repository = PhoneBookRepository(requireActivity().application)
        initView()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isBindingInitialized){
            binding.unbind()
        }
    }

    private fun handleOnclickItem(p: Int) {

    }

    private fun setupPopUpWindow(view: View, call: Call) {

        listActionPopup.showPopup(view, Utils.getActionPopup(call.isLocal, requireContext()), object : ActionAdapter.OnActionListener{
            override fun onItemClickListener(position: Int) {
                when (position) {
                    0 -> {
                        passCallToUpDate(call)
                    }
                    else -> {
                        setUpAlertDialog(call)
                    }
                }
            }
        })
    }


    private fun setUpAlertDialog(call: Call) {
        val dialog = ConfirmDeleteDialog.create(this, call)
        dialog.show(childFragmentManager, "")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOkClick(call: Call) {
        lifecycleScope.launch {
            repository.deleteCall(call)
            getPhoneBook()
            callAdapter.notifyDataSetChanged()
        }
        Toast.makeText(
            requireContext(),
            requireActivity().resources.getString(R.string.delete_complete),
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun passCallToUpDate(call: Call) {
        sharedViewModel.setStatus(ShareViewModelStatus.EditCall)
        sharedViewModel.setCall(call)
        action = CallerFragmentDirections.actionGlobalAddCallScreenFragment()
        findNavController().navigate(action as NavDirections)
    }

    override fun onItemClick(call: Call,position: Int) {
        sharedViewModel.setStatus(ShareViewModelStatus.SetCall)
        sharedViewModel.setCall(call)
        action = CallerFragmentDirections.actionGlobalSetupCallFragment()
        findNavController().navigate(action as NavDirections)
    }

    override fun onMoreClick(view: View, call: Call) {
        setupPopUpWindow(view, call)
    }

    abstract fun getLayout(): Int
    abstract fun initView()
    abstract fun initViewModel()
    abstract fun getPhoneBook()
}
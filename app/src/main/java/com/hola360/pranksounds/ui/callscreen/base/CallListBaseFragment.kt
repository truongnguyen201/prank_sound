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
import com.hola360.pranksounds.ui.callscreen.CallItemListener
import com.hola360.pranksounds.ui.callscreen.CallScreenFragmentDirections
import com.hola360.pranksounds.ui.callscreen.CallScreenSharedViewModel
import com.hola360.pranksounds.ui.callscreen.DeleteConfirmListener
import com.hola360.pranksounds.ui.callscreen.adapter.CallAdapter
import com.hola360.pranksounds.ui.callscreen.addcallscreen.AddCallScreenFragment
import com.hola360.pranksounds.ui.dialog.confirmdelete.ConfirmDeleteDialog
import kotlinx.coroutines.launch

abstract class CallListBaseFragment<V : ViewDataBinding> : Fragment(), CallItemListener, DeleteConfirmListener {
    protected lateinit var binding: V
    private lateinit var repository: PhoneBookRepository
    protected var callAdapter: CallAdapter= CallAdapter { handleOnclickItem(it) }
    private lateinit var action: Any
    private lateinit var popUpWindow: PopupWindow
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels - 100
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels - 100
    private val sharedViewModel by activityViewModels<CallScreenSharedViewModel>()

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
//        callAdapter = CallAdapter {
//            handleOnclickItem(it)
//        }
        callAdapter.setListener(this)
        repository = PhoneBookRepository(requireActivity().application)
        initView()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

    private fun handleOnclickItem(p: Int) {

    }

    private fun setupPopUpWindow(call: Call) {
        val popUpInflater =
            requireActivity().applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUpBinding = PopUpCallMoreBinding.inflate(popUpInflater)

        if (!call.isLocal) {
            popUpBinding.llDelete.visibility = View.GONE
        }
        popUpBinding.apply {
            llEdit.setOnClickListener {
                popUpWindow.dismiss()
                passCallToUpDate(call)
            }
            llDelete.setOnClickListener {
                popUpWindow.dismiss()
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


    private fun setUpAlertDialog(call: Call) {
        val dialog = ConfirmDeleteDialog.create(this, call)
        dialog.show(childFragmentManager, "")
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setTitle(requireActivity().resources.getString(R.string.confirm_title))
//            .setMessage(requireActivity().resources.getString(R.string.confirm_delete_start)
//                    + call.name
//                    + requireActivity().resources.getString(R.string.confirm_delete_end))
//            .setCancelable(false)
//            .setPositiveButton("OK") { dialog, id ->
//                lifecycleScope.launch {
//                    repository.deleteCall(call)
//                    getPhoneBook()
//                    callAdapter.notifyDataSetChanged()
//                }
//                Toast.makeText(
//                    requireContext(),
//                    requireActivity().resources.getString(R.string.delete_complete),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            .setNegativeButton("Cancel") { dialog, id ->
//                // Dismiss the dialog
//                dialog.dismiss()
//            }
//        val alert = builder.create()
//        alert.show()
//        popUpWindow.dismiss()
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
        action = CallScreenFragmentDirections.actionGlobalAddCallScreenFragment(call)
        findNavController().navigate(action as NavDirections)
    }

    override fun onItemClick(call: Call,position: Int) {
        action = CallScreenFragmentDirections.actionGlobalSetupCallFragment(call)
        findNavController().navigate(action as NavDirections)
    }

    override fun onMoreClick(view: View, call: Call) {
        setupPopUpWindow(call)
        popUpWindow.showAsDropDown(
            view, 0,
            0
        )
    }

    abstract fun getLayout(): Int
    abstract fun initView()
    abstract fun initViewModel()
    abstract fun getPhoneBook()
}
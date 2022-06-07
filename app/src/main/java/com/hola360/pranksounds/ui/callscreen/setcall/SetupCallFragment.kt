package com.hola360.pranksounds.ui.callscreen.setcall


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.FragmentSetupCallBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.callscreen.CallScreenSharedViewModel
import com.hola360.pranksounds.ui.callscreen.CallerFragmentDirections
import com.hola360.pranksounds.ui.callscreen.DeleteConfirmListener
import com.hola360.pranksounds.ui.callscreen.ShareViewModelStatus
import com.hola360.pranksounds.ui.callscreen.callingscreen.receiver.CallingReceiver
import com.hola360.pranksounds.ui.dialog.confirmdelete.ConfirmDeleteDialog
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import com.hola360.pranksounds.utils.listener.Converter
import java.util.*


class SetupCallFragment : BaseFragment<FragmentSetupCallBinding>(), DeleteConfirmListener {
    lateinit var setupCallViewModel: SetupCallViewModel
    lateinit var receiver: CallingReceiver
    private val sharedViewModel by activityViewModels<CallScreenSharedViewModel>()
    private var mLastClickTime: Long = 0
    private lateinit var action: Any

    override fun getLayout(): Int {
        return R.layout.fragment_setup_call
    }
    override fun initView() {
        if (setupCallViewModel.getCurrentCall()?.isLocal == true) {
            binding.tbSetupCallScreen.inflateMenu(R.menu.setup_call_menu_2)
            sharedViewModel.setBackToMyCaller(true)

        }
        binding.tbSetupCallScreen.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        setupCallViewModel.callLiveData.observe(this) {
            with(binding) {
                it?.let { thisCall ->
                    val path =
                        if (thisCall.isLocal) thisCall.avatarUrl else Constants.SUB_URL + thisCall.avatarUrl
                    imgAvatar.let { imgView ->
                        Glide.with(imgView)
                            .load(path)
                            .placeholder(R.drawable.img_avatar_default)
                            .error(R.drawable.img_avatar_default)
                            .into(imgView)
                    }
                    tvCallerName.text = thisCall.name.ifEmpty {
                        requireContext().getString(R.string.unknown)
                    }
                    tvPhoneNumber.text = thisCall.phone
                }
            }
        }


        with(binding) {
            btnNow.setOnClickListener {
                setupCallViewModel.period.value = WaitCallPeriod.Now
            }

            btnFiveSeconds.setOnClickListener {
                setupCallViewModel.period.value = WaitCallPeriod.FiveSeconds
            }
            btnThirtySeconds.setOnClickListener {
                setupCallViewModel.period.value = WaitCallPeriod.ThirtySeconds
            }

            btnOneMinute.setOnClickListener {
                setupCallViewModel.period.value = WaitCallPeriod.OneMinute
            }

            btnSetCall.setOnClickListener {
                if (Utils.checkDisplayOverOtherAppPermission(requireContext())) {
                    setupCallViewModel.startCalling()
                }
                else {
                    Utils.setUpDialogGrantPermission(requireContext())
                }
            }
        }
        with(binding.tbSetupCallScreen) {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit_call -> {
                        if (SystemClock.elapsedRealtime() - mLastClickTime > 100) {
                            sharedViewModel.setBackToMyCaller(false)
                            sharedViewModel.setCall(setupCallViewModel.getCurrentCall())
                            sharedViewModel.setStatus(ShareViewModelStatus.EditCall)
                            action =
                                CallerFragmentDirections.actionGlobalAddCallScreenFragment()
                            findNavController().navigate(action as NavDirections)
                        }
                        mLastClickTime = SystemClock.elapsedRealtime()
                        true
                    }
                    R.id.delete_call -> {
                        if (SystemClock.elapsedRealtime() - mLastClickTime > 100) {
                            confirmDelete()
                        }
                        mLastClickTime = SystemClock.elapsedRealtime()
                        true
                    }
                    else -> false
                }
            }

            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        receiver = CallingReceiver()
        val intentFilter = IntentFilter(Constants.ALARM_SERVICE_ACTION)
        requireActivity().registerReceiver(receiver, intentFilter)
    }

    override fun initViewModel() {
        val factory = SetupCallViewModel.Factory(requireActivity().application)
        setupCallViewModel = ViewModelProvider(this, factory)[SetupCallViewModel::class.java]
        setDataByViewModel()
    }


    private fun setDataByViewModel() {
        if (sharedViewModel.getStatus() == ShareViewModelStatus.SetCall) {
            setupCallViewModel.setCall(sharedViewModel.getCall())
        }
        sharedViewModel.setCall(null)
        sharedViewModel.setStatus(ShareViewModelStatus.Default)

        setupCallViewModel.callLiveData.observe(this) {
            with(binding) {
                it?.let { thisCall ->
                    val path =
                        if (thisCall.isLocal) thisCall.avatarUrl else Constants.SUB_URL + thisCall.avatarUrl
                    imgAvatar.let { imgView ->
                        Glide.with(imgView)
                            .load(path)
                            .placeholder(R.drawable.img_avatar_default)
                            .error(R.drawable.img_avatar_default)
                            .into(imgView)
                    }
                    tvCallerName.text = thisCall.name.ifEmpty {
                        requireContext().getString(R.string.unknown)
                    }
                    tvPhoneNumber.text = thisCall.phone
                }
            }
        }

        setupCallViewModel.startCallingLiveData.observe(this) {
            it?.let {
                val alarmManager =
                    requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(
                    Constants.ALARM_SERVICE_ACTION,
                    null,
                    requireContext(),
                    CallingReceiver::class.java
                )

                val bundle = Bundle()
                bundle.putParcelable("call", it)
                intent.putExtras(bundle)
                if (setupCallViewModel.period.value == WaitCallPeriod.Now) {
                    requireActivity().sendBroadcast(intent)
                } else {
                    val pendingIntent = PendingIntent.getBroadcast(
                        requireContext(),
                        Random().nextInt(123123),
                        intent,
                        Utils.getPendingIntentFlags()
                    )
                    alarmManager.setExact(
                        Utils.getAlarmManagerFlags(),
                        Calendar.getInstance().timeInMillis + Converter.convertTime(
                            setupCallViewModel.period.value!!
                        ),
                        pendingIntent
                    )
                    backToHome()
                }
            }
        }
        setupCallViewModel.period.observe(this) {
            if (it == WaitCallPeriod.Now)
                binding.btnNow.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.time_button
                    )
                )
            else binding.btnNow.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.none
                )
            )
            if (it == WaitCallPeriod.FiveSeconds)
                binding.btnFiveSeconds.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.time_button
                    )
                )
            else binding.btnFiveSeconds.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.none
                )
            )
            if (it == WaitCallPeriod.ThirtySeconds)
                binding.btnThirtySeconds.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.time_button
                    )
                )
            else binding.btnThirtySeconds.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.none
                )
            )
            if (it == WaitCallPeriod.OneMinute)
                binding.btnOneMinute.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.time_button
                    )
                )
            else binding.btnOneMinute.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.none
                )
            )
        }
        setupCallViewModel.periodOfTime.observe(this) {
            binding.tvPeriod.text = it
        }
    }

    private fun backToHome() {
        val intent = Intent()
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }
    private fun confirmDelete() {
            val dialog = ConfirmDeleteDialog.create(this, setupCallViewModel.getCurrentCall()!!)
            dialog.show(childFragmentManager, "")
    }

    override fun onOkClick(call: Call) {
        setupCallViewModel.deleteCall()
        requireActivity().onBackPressed()
        Toast.makeText(
            requireContext(),
            requireActivity().resources.getString(R.string.delete_complete),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(receiver)
    }
    override fun onResume() {
        super.onResume()
        if (setupCallViewModel.getCurrentCall()?.isLocal == true) {
            sharedViewModel.setBackToMyCaller(true)
        }
        if (sharedViewModel.getCall() != null) {
            setupCallViewModel.setCall(sharedViewModel.getCall())
        }
        sharedViewModel.setCall(null)
        sharedViewModel.setStatus(ShareViewModelStatus.Default)

    }
}


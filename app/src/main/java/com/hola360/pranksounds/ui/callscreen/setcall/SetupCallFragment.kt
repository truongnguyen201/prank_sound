package com.hola360.pranksounds.ui.callscreen.setcall


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.ResultDataResponse
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.FragmentSetupCallBinding
import com.hola360.pranksounds.ui.base.BaseScreenWithViewModelFragment
import com.hola360.pranksounds.ui.callscreen.CallScreenSharedViewModel
import com.hola360.pranksounds.ui.callscreen.CallerFragmentDirections
import com.hola360.pranksounds.ui.callscreen.DeleteConfirmListener
import com.hola360.pranksounds.ui.callscreen.data.ShareViewModelStatus
import com.hola360.pranksounds.ui.callscreen.callingscreen.receiver.CallingReceiver
import com.hola360.pranksounds.ui.dialog.confirmdelete.ConfirmDeleteDialog
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import com.hola360.pranksounds.utils.listener.Converter
import java.util.*


class SetupCallFragment : BaseScreenWithViewModelFragment<FragmentSetupCallBinding>(),
    DeleteConfirmListener {
    private lateinit var setupCallViewModel: SetupCallViewModel
    private lateinit var receiver: CallingReceiver
    private val isReceiverInitialized get() = this::receiver.isInitialized

    private lateinit var sharedViewModel: CallScreenSharedViewModel
    private var mLastClickTime: Long = 0
    private lateinit var action: Any

    override fun getLayout(): Int {
        return R.layout.fragment_setup_call
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    override fun initView() {
        binding.tbSetupCallScreen.menu.findItem(R.id.delete_call).isVisible =
            setupCallViewModel.getCurrentCall()?.isLocal!!
        if (setupCallViewModel.getCurrentCall()?.isLocal!!) {
            sharedViewModel.setBackToMyCaller(true)
        }
        binding.tbSetupCallScreen.setNavigationOnClickListener {
            requireActivity().onBackPressed()
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
                } else {
                    Utils.setUpDialogGrantPermission(requireContext())
                }
            }
        }

        with(binding.tbSetupCallScreen) {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit_call -> {
                        if (SystemClock.elapsedRealtime() - mLastClickTime > resources.getInteger(R.integer.short_period)) {
                            val body = (sharedViewModel.resultLiveData.value as ResultDataResponse.ResultDataSuccess).body
                            sharedViewModel.setBackToMyCaller(false)
                            sharedViewModel.setResultData(ShareViewModelStatus.EditCall.ordinal, body)
                            action =
                                CallerFragmentDirections.actionGlobalAddCallScreenFragment()
                            findNavController().navigate(action as NavDirections)
                        }
                        mLastClickTime = SystemClock.elapsedRealtime()
                        true
                    }
                    R.id.delete_call -> {
                        if (SystemClock.elapsedRealtime() - mLastClickTime > resources.getInteger(R.integer.short_period)) {
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
        sharedViewModel = CallScreenSharedViewModel.getInstance(mainActivity.application)

        val factory = SetupCallViewModel.Factory(mainActivity.application)
        setupCallViewModel = ViewModelProvider(this, factory)[SetupCallViewModel::class.java]

        sharedViewModel.resultLiveData.observe(this){
            it?.let {
                try {
                    val body = (it as ResultDataResponse.ResultDataSuccess).body
                    setupCallViewModel.setCall(body)
                } catch (e: Exception) {
                    requireActivity().onBackPressed()
                }
            }
        }
        setDataByViewModel()
    }
    @SuppressLint("CommitPrefEdits")
    private fun setDataByViewModel() {
        if (sharedViewModel.resultLiveData.value?.resultCode == ShareViewModelStatus.SetCall.ordinal) {
            val body = (sharedViewModel.resultLiveData.value as ResultDataResponse.ResultDataSuccess).body
            setupCallViewModel.setCall(body)
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
                val pref: SharedPreferences = requireContext().getSharedPreferences(Constants.REQUEST_CODE, MODE_PRIVATE)
                val requestCode = pref.getInt(Constants.INTENT_CODE, 1000001)

                val bundle = Bundle()
                bundle.putParcelable("call", it)
                intent.putExtras(bundle)
                if (setupCallViewModel.period.value == WaitCallPeriod.Now) {
                    requireActivity().sendBroadcast(intent)
                } else {
                    alarmManager.cancel(PendingIntent.getBroadcast(
                        requireActivity(),
                        requestCode,
                        intent,
                        Utils.getPendingIntentFlags()
                    ))

                    val code = Random().nextInt(19934775)
                    pref.edit().putInt(Constants.INTENT_CODE, code).commit()


                    val pendingIntent = PendingIntent.getBroadcast(
                        requireActivity(),
                        code,
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
        val dialog = ConfirmDeleteDialog.create()
        dialog.setOnClickListener(this, setupCallViewModel.getCurrentCall()!!)
        dialog.show(childFragmentManager, "")
    }

    override fun onOkClick(call: Call) {
        setupCallViewModel.deleteCall()
        requireActivity().onBackPressed()
        mainActivity.showToast(getString(R.string.delete_complete))

    }

    override fun onDestroy() {
        super.onDestroy()
        if (isReceiverInitialized) {
            requireContext().unregisterReceiver(receiver)
        }
    }

    override fun onResume() {
        super.onResume()
//        val body = (sharedViewModel.resultLiveData.value as ResultDataResponse.ResultDataSuccess).body
    }
}


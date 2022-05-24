package com.hola360.pranksounds.ui.callscreen.setcall


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.FragmentSetupCallBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.callscreen.CallScreenFragmentDirections
import com.hola360.pranksounds.ui.callscreen.callingscreen.receiver.CallingReceiver
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import com.hola360.pranksounds.utils.listener.Converter
import java.util.*


class SetupCallFragment : BaseFragment<FragmentSetupCallBinding>() {
    lateinit var setupCallViewModel: SetupCallViewModel
    private val args: SetupCallFragmentArgs by navArgs()
    lateinit var receiver: CallingReceiver
    private lateinit var action: Any

    override fun getLayout(): Int {
        return R.layout.fragment_setup_call
    }

    override fun initView() {

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
                setupCallViewModel.startCalling()
//                backToHome()
            }
        }
        with(binding.tbSetupCallScreen) {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit_call -> {
                        action =
                            CallScreenFragmentDirections.actionGlobalAddCallScreenFragment(args.callModel)
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

        receiver = CallingReceiver()
        val intentFilter = IntentFilter(Constants.ALARM_SERVICE_ACTION)
        requireActivity().registerReceiver(receiver, intentFilter)
    }

    override fun initViewModel() {
        val factory = SetupCallViewModel.Factory(requireActivity().application, args.callModel)
        setupCallViewModel = ViewModelProvider(this, factory)[SetupCallViewModel::class.java]
        setDataByViewModel()
    }


    private fun setDataByViewModel() {
        setupCallViewModel.callLiveData.observe(this) {
            with(binding) {
                it?.let {
                    val path =
                        if (it.isLocal) it.avatarUrl else Constants.SUB_URL + it.avatarUrl
                    Log.e("xxxxx", "setDataByViewModel: update view${it.name}")
                    imgAvatar.let { imgView ->
                        Glide.with(imgView)
                            .load(path)
                            .placeholder(R.drawable.smaller_loading)
                            .error(R.drawable.img_avatar_default)
                            .into(imgView)
                    }
                    tvCallerName.text = it.name
                    tvPhoneNumber.text = it.phone
                }
            }
        }

        setupCallViewModel.startCallingLiveData.observe(this) {
            it?.let {
                val alarmManager =
                    requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(
                    Constants.ALARM_SERVICE_ACTION,
                    null,
                    requireActivity(),
                    CallingReceiver::class.java
                ).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING)
                val bundle = Bundle()
                bundle.putParcelable("call", it)
                intent.putExtras(bundle)
                if (setupCallViewModel.period.value == WaitCallPeriod.Now) {
                    requireActivity().sendBroadcast(intent)
                } else {
                    val pendingIntent = PendingIntent.getBroadcast(
                        requireActivity(),
                        1,
                        intent,
                        Utils.getPendingIntentFlags()
                    )
                    alarmManager.setExact(
                        AlarmManager.RTC,
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
                        R.color.design_color
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
                        R.color.design_color
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
                        R.color.design_color
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
                        R.color.design_color
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

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(receiver)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (data?.action == "DataFix") {
//            val fixCall: Call? = data.getParcelableExtra<Call>("CallAfterFix")
//            if (fixCall != null) {
//                setupCallViewModel.setCall(fixCall)
//                Log.e("/////", "on activity reult: update call", )
//            }
//        }
//    }


    override fun onResume() {
        super.onResume()
        if (setupCallViewModel.curCallModel?.id != 0) {
            setupCallViewModel.updateCallFromLocal()
        }
    }


}


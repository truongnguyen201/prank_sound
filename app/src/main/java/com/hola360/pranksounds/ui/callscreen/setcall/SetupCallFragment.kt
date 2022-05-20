package com.hola360.pranksounds.ui.callscreen.setcall


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.FragmentSetupCallBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.callscreen.callingscreen.receiver.CallingReceiver
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.listener.Converter
import java.util.*


class SetupCallFragment : BaseFragment<FragmentSetupCallBinding>() {
    lateinit var setupCallViewModel: SetupCallViewModel
    private var call: Call? = null
    private val args: SetupCallFragmentArgs by navArgs()
    lateinit var receiver: CallingReceiver
    override fun getLayout(): Int {
        return R.layout.fragment_setup_call
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun initView() {
        call = args.callModel
        if (call != null) {
            binding.tbSetupCallScreen.title = requireActivity().getString(R.string.set_call)
            setupCallViewModel.setCall(call!!)
            with(binding) {
                val path =
                    if (call!!.isLocal) call!!.avatarUrl else Constants.SUB_URL + call!!.avatarUrl
                imgAvatar.let { imgView ->
                    Glide.with(imgView)
                        .load(path)
                        .placeholder(R.drawable.smaller_loading)
                        .error(R.drawable.img_avatar_default)
                        .into(imgView)
                }
                tvCallerName.text = call!!.name
                tvPhoneNumber.text = call!!.phone
            }
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
                setCalling(Converter.convertTime(setupCallViewModel.period.value!!), call!!)
                backToHome()
            }
        }

        observe()

        receiver = CallingReceiver()
        val intentFilter = IntentFilter("com.hola360.pranksounds")
        requireActivity().registerReceiver(receiver, intentFilter)
    }

    override fun initViewModel() {
        val factory = SetupCallViewModel.Factory(requireActivity().application, args.callModel)
        setupCallViewModel = ViewModelProvider(this, factory)[SetupCallViewModel::class.java]
    }

    private fun observe() {
        setupCallViewModel.period.observe(requireActivity(), Observer {
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
        })

        setupCallViewModel.periodOfTime.observe(requireActivity(), Observer {
            binding.tvPeriod.text = it
        })

    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setCalling(timeInMillis: Long, callIntent: Call) {
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(
            "com.hola360.pranksounds",
            null,
            requireActivity(),
            CallingReceiver::class.java
        ).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING)

        val bundle = Bundle()
        bundle.putParcelable("call", callIntent)
        intent.putExtras(bundle)

        if (setupCallViewModel.period.value == WaitCallPeriod.Now) {
            requireActivity().sendBroadcast(intent)
        }
        else {
            val pendingIntent = PendingIntent.getBroadcast(
                requireActivity(),
                1,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExact(
                AlarmManager.RTC,
                Calendar.getInstance().timeInMillis + timeInMillis,
                pendingIntent
            )
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

}


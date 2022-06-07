package com.hola360.pranksounds.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.hola360.pranksounds.MainActivity
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentSplashBinding
import com.hola360.pranksounds.ui.base.AbsBaseFragment
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.SharedPreferenceUtils

class SplashFragment : AbsBaseFragment<FragmentSplashBinding>() {
    private val handler = Handler(Looper.myLooper()!!)
    private val runnable = Runnable {
        if (SharedPreferenceUtils.getInstance(requireContext()).getAcceptPolicy()) {
            requireActivity().startActivity(Intent(context, MainActivity::class.java))
            requireActivity().finish()
        } else {
            findNavController().navigate(SplashFragmentDirections.actionGlobalPolicyFragment())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //nothing
                }
            })
    }
    override fun onStart() {
        super.onStart()
        handler.postDelayed(runnable, Constants.SPLASH_TIMING)
    }
    override fun initView() {

    }
    override fun getLayout(): Int {
        return R.layout.fragment_splash
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //add flag to show fullscreen in splash screen
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onStop() {
        super.onStop()
        //remove flag to show normal mode when navigate to next screen from splash screen
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        super.onPause()
    }
}
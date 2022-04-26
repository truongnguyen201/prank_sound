package com.hola360.pranksounds.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.hola360.pranksounds.MainActivity
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentSplashBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.utils.Constants

class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    private lateinit var splashViewModel: SplashViewModel
    override fun initView() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (splashViewModel.action == null) {
                requireActivity().startActivity(Intent(context, MainActivity::class.java))
                requireActivity().finish()
            } else {
                findNavController().navigate(splashViewModel.action as NavDirections)
            }
        }, Constants.SPLASH_TIMING)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_splash
    }

    override fun initViewModel() {
        val factory = SplashViewModel.Factory(requireActivity().application)
        splashViewModel = ViewModelProvider(this, factory)[SplashViewModel::class.java]
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
}
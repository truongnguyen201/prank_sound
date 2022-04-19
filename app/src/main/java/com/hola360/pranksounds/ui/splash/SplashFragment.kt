package com.hola360.pranksounds.ui.splash

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentSplashBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.utils.Constants

class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    //use SharedPreferences to save state accept policy
    private lateinit var sharedPreferences: SharedPreferences
    override fun initView() {
        sharedPreferences =
            requireContext().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        lateinit var action: Any

        action = if (sharedPreferences.getBoolean("isAcceptPolicy", false)) {
            SplashFragmentDirections.actionGlobalHomeFragment()
        } else {
            SplashFragmentDirections.actionGlobalPolicyFragment()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(action as NavDirections)
        }, 3000)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_splash
    }

    override fun initViewModel() {

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
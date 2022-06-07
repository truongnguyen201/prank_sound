package com.hola360.pranksounds.ui.splash

import android.annotation.SuppressLint
import com.hola360.pranksounds.BaseActivity
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun getFragmentID(): Int {
        return R.id.navHostFragment
    }

    override fun bind() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}
package com.hola360.pranksounds

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.ui.setupWithNavController
import com.hola360.pranksounds.databinding.ActivitySplashBinding
import com.hola360.pranksounds.utils.Constants

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private var doubleBackToExitPressedOnce = false
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment -> {
                    binding.toolbar.visibility = View.GONE
                }
                R.id.policyFragment -> {
                    val navIcon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24)
                    binding.toolbar.visibility = View.VISIBLE
                    binding.toolbar.navigationIcon = navIcon
                    binding.toolbar.setNavigationOnClickListener { finish() }
                }
            }
        }
    }

    override fun getFragmentID(): Int {
        return R.id.navHostFragment
    }

    override fun bind() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    override fun actionBarSetupWithNavController() {
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, Constants.BACK_PRESSED_TIMING)
    }
}
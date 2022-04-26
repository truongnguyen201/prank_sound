package com.hola360.pranksounds

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.ui.setupWithNavController
import com.hola360.pranksounds.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.toolbar.visibility = View.GONE
                }
                else -> {
                    val navIcon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back)
                    binding.toolbar.navigationIcon = navIcon
                    binding.toolbar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getFragmentID(): Int {
        return R.id.navHostFragmentContentMain
    }

    override fun bind() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    override fun actionBarSetupWithNavController() {
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}
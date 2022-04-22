package com.hola360.pranksounds

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.hola360.pranksounds.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentContentMain) as NavHostFragment
        val navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.homeFragment -> binding.toolbar.visibility = View.GONE
                R.id.policyFragment -> {
                    val navIcon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24)
                    binding.toolbar.visibility = View.VISIBLE
                    binding.toolbar.navigationIcon = navIcon
                    binding.toolbar.setNavigationOnClickListener { finish() }
                }
                else -> {
                    val navIcon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back)
                    binding.toolbar.navigationIcon = navIcon
                    binding.toolbar.visibility = View.VISIBLE
                }
            }
        }
    }

}
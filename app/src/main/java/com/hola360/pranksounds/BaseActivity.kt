package com.hola360.pranksounds

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment
    protected lateinit var navController: NavController
    protected lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind()
        navHostFragment =
            supportFragmentManager.findFragmentById(getFragmentID()) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        actionBarSetupWithNavController()
    }

    abstract fun getFragmentID(): Int
    abstract fun bind()
    abstract fun setupActionBar()
    abstract fun actionBarSetupWithNavController()
}
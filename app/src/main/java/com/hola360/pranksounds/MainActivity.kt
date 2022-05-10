package com.hola360.pranksounds

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.hola360.pranksounds.databinding.ActivityMainBinding
import com.hola360.pranksounds.ui.callscreen.CallScreenFragmentDirections
import com.hola360.pranksounds.ui.callscreen.addcallscreen.AddCallScreenFragment

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var action: Any

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
                    binding.toolbar.visibility = View.GONE
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()

        when(item.itemId) {
            R.id.add_new_call -> {

                transaction.add(R.id.navHostFragmentContentMain, AddCallScreenFragment())
                transaction.commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
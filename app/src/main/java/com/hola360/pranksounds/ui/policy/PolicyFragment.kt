package com.hola360.pranksounds.ui.policy

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hola360.pranksounds.MainActivity
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentPolicyBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.utils.Constants

class PolicyFragment : BaseFragment<FragmentPolicyBinding>() {
    private lateinit var policyViewModel: PolicyViewModel

    override fun initView() {
        //Set isAcceptPolicy as true when click on btStart
        binding.btStart.setOnClickListener {
            policyViewModel.setAcceptPolicy()
            requireActivity().startActivity(Intent(context, MainActivity::class.java))
            requireActivity().finish()
        }

        binding.wvPolicy.loadUrl("https://www.w3.org/Protocols/HTTP/Response.html")

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

    override fun initViewModel() {
        val factory = PolicyViewModel.Factory(requireActivity().application)
        policyViewModel = ViewModelProvider(this, factory)[PolicyViewModel::class.java]
    }

    override fun getLayout(): Int {
        return R.layout.fragment_policy
    }
}
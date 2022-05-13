package com.hola360.pranksounds.ui.policy

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.MainActivity
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentPolicyBinding
import com.hola360.pranksounds.ui.base.BaseFragment

class PolicyFragment : BaseFragment<FragmentPolicyBinding>() {
    private lateinit var policyViewModel: PolicyViewModel

    override fun initView() {
        //Set isAcceptPolicy as true when click on btStart
        binding.btStart.setOnClickListener {
            policyViewModel.setAcceptPolicy()
            requireActivity().startActivity(Intent(context, MainActivity::class.java))
            requireActivity().finish()
        }

        binding.wvPolicy.loadUrl("file:///android_asset/policy.adp")

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
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
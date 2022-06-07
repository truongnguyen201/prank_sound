package com.hola360.pranksounds.ui.splash

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import com.hola360.pranksounds.MainActivity
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentPolicyBinding
import com.hola360.pranksounds.ui.base.AbsBaseFragment
import com.hola360.pranksounds.utils.SharedPreferenceUtils

class PolicyFragment : AbsBaseFragment<FragmentPolicyBinding>() {
    override fun initView() {
        binding.apply {
            toolbar.setNavigationOnClickListener{
                requireActivity().finish()
            }
            btStart.setOnClickListener {
                SharedPreferenceUtils.getInstance(requireActivity()).putAcceptPolicy(true)
                gotoMainActivity()
            }
            wvPolicy.loadUrl("file:///android_asset/policy.adp")
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

    private fun gotoMainActivity(){
        requireActivity().startActivity(Intent(context, MainActivity::class.java))
        requireActivity().finish()
    }

    override fun getLayout(): Int {
        return R.layout.fragment_policy
    }
}
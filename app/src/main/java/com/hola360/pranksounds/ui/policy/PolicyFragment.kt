package com.hola360.pranksounds.ui.policy

import android.content.Context
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.hola360.pranksounds.MainActivity
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentPolicyBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.utils.Constants

class PolicyFragment : BaseFragment<FragmentPolicyBinding>() {

    override fun initView() {
        //Set isAcceptPolicy as true when click on btStart
        binding.btStart.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences(
                Constants.PREFERENCE_NAME,
                Context.MODE_PRIVATE
            )
            val prefEditor = sharedPreferences.edit()
            prefEditor.putBoolean("isAcceptPolicy", true)
            prefEditor.apply()

            val action = PolicyFragmentDirections.actionGlobalHomeFragment()
            findNavController().navigate(action)
        }
    }

    override fun initViewModel() {

    }

    override fun getLayout(): Int {
        return R.layout.fragment_policy
    }

}
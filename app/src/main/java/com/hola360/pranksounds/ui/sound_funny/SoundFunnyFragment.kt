package com.hola360.pranksounds.ui.sound_funny

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentSoundFunnyBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.sound_funny.adapter.SoundCategoryAdapter

class SoundFunnyFragment : BaseFragment<FragmentSoundFunnyBinding>() {
    private lateinit var soundCategoryAdapter: SoundCategoryAdapter

    override fun getLayout(): Int {
        return R.layout.fragment_sound_funny
    }

    override fun initView() {
        soundCategoryAdapter = SoundCategoryAdapter {
            handleOnItemClick(it)
        }

        binding.apply {
            rvCategory.layoutManager = LinearLayoutManager(requireContext())
            rvCategory.setHasFixedSize(true)
            rvCategory.adapter = soundCategoryAdapter
        }
    }

    override fun initViewModel() {

    }

    private fun handleOnItemClick(title: String) {
        val action = SoundFunnyFragmentDirections.actionSoundFunnyFragmentToCategoryFragment()
            .setCategoryTitle(title)
        findNavController().navigate(action)
    }
}
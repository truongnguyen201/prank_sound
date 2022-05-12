package com.hola360.pranksounds.ui.sound_funny.sound_detail

import android.view.Menu
import android.view.MenuInflater
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentDetailCategoryBinding
import com.hola360.pranksounds.databinding.FragmentSoundDetailBinding
import com.hola360.pranksounds.ui.base.BaseFragment

class SoundDetailFragment : BaseFragment<FragmentSoundDetailBinding>() {
    override fun getLayout(): Int {
        return R.layout.fragment_sound_detail
    }

    override fun initView() {
        binding.apply {
            toolbar.apply {
                setNavigationOnClickListener{
                    requireActivity().onBackPressed()
                }
                title = "HAHA"
            }

        }
    }

    override fun initViewModel() {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

}
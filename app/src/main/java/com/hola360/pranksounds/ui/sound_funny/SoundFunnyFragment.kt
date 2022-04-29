package com.hola360.pranksounds.ui.sound_funny

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.SoundCategory
import com.hola360.pranksounds.databinding.FragmentSoundFunnyBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.sound_funny.adapter.SoundCategoryAdapter
import java.lang.Exception

class SoundFunnyFragment : BaseFragment<FragmentSoundFunnyBinding>() {
    private lateinit var soundCategoryAdapter: SoundCategoryAdapter
    private lateinit var soundFunnyViewModel: SoundFunnyViewModel

    override fun getLayout(): Int {
        return R.layout.fragment_sound_funny
    }

    override fun initView() {
        soundCategoryAdapter = SoundCategoryAdapter{
            handleOnItemClick(it)
        }

        binding.apply {
            rvCategory.layoutManager = LinearLayoutManager(requireContext())
            rvCategory.setHasFixedSize(true)
            rvCategory.adapter = soundCategoryAdapter
        }
        binding.viewModel = soundFunnyViewModel
    }

    override fun initViewModel() {
        val factory = SoundFunnyViewModel.Factory(requireActivity().application)
        soundFunnyViewModel = ViewModelProvider(this, factory)[SoundFunnyViewModel::class.java]
        soundFunnyViewModel.soundCategoryLiveData.observe(this) {
            it?.let {
                if (it.loadingStatus == LoadingStatus.Success) {
                    val soundCategoryList = (it as DataResponse.DataSuccess).body
                    soundCategoryAdapter.updateData(soundCategoryList)
                } else {
                    if (it.loadingStatus == LoadingStatus.Error) {
                        soundCategoryAdapter.updateData(null)
                    }
                }
            }
        }

        soundFunnyViewModel.getCategory()
    }

    private fun handleOnItemClick(category: SoundCategory) {
        val action = SoundFunnyFragmentDirections.actionSoundFunnyFragmentToCategoryFragment()
            .setCategoryTitle(category.title)
            .setCategoryId(category.categoryId)
        findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()
        soundFunnyViewModel.getCategory()
    }
}
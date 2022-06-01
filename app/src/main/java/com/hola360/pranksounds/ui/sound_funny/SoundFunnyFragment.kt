package com.hola360.pranksounds.ui.sound_funny

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ybq.android.spinkit.style.Circle
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.SoundCategory
import com.hola360.pranksounds.databinding.FragmentSoundFunnyBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.sound_funny.adapter.SoundCategoryAdapter

class SoundFunnyFragment : BaseFragment<FragmentSoundFunnyBinding>() {

    private val soundCategoryAdapter = SoundCategoryAdapter {
        handleOnItemClick(it)
    }

    private lateinit var soundFunnyViewModel: SoundFunnyViewModel

    override fun getLayout(): Int {
        return R.layout.fragment_sound_funny
    }

    override fun initView() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
            rvCategory.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = soundCategoryAdapter
            }
            noInternetLayout.btRetry.setOnClickListener { soundFunnyViewModel.retry() }
            viewModel = soundFunnyViewModel
        }
        setUpProgressBar()
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
    }

    private fun handleOnItemClick(category: SoundCategory) {
        val action = SoundFunnyFragmentDirections.actionSoundFunnyFragmentToCategoryFragment()
            .setCategoryTitle(category.title)
            .setCategoryId(category.categoryId)
        findNavController().navigate(action)
    }

    private fun setUpProgressBar() {
        val circle = Circle()
        circle.color = Color.parseColor("#F18924")
        binding.progressBar.indeterminateDrawable = circle
    }
}
package com.hola360.pranksounds.ui.home

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentHomeBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.home.adapter.PrankAdapter
import com.hola360.pranksounds.ui.home.adapter.SlideAdapter
import com.hola360.pranksounds.utils.GridSpacingItemDecoration

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private lateinit var prankAdapter: PrankAdapter
    private lateinit var slideAdapter: SlideAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var action: Any

    override fun getLayout(): Int {
        return R.layout.fragment_home
    }

    override fun initView() {
        prankAdapter = PrankAdapter {
            handleOnItemClick(it)
        }

        slideAdapter = SlideAdapter(homeViewModel.images)

        binding.apply {
            rvPranks.layoutManager = GridLayoutManager(requireContext(), 2)
            rvPranks.setHasFixedSize(true)
            val spacing = resources.getDimensionPixelSize(R.dimen.home_prank_space)
            rvPranks.addItemDecoration(
                GridSpacingItemDecoration(
                    2,
                    spacing, false, 0
                )
            )
            rvPranks.adapter = prankAdapter
            prankAdapter.setData(homeViewModel.mPrankList)
            imageSlider.setSliderAdapter(slideAdapter)
            imageSlider.startAutoCycle()
        }

    }

    override fun initViewModel() {
        val factory = HomeViewModel.Factory(requireActivity().application)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private fun handleOnItemClick(id: Int) {
        when (id) {
            1 -> {
                action = HomeFragmentDirections.actionGlobalHairCuttingFragment()
            }
            2 -> {
                action = HomeFragmentDirections.actionGlobalBrokenScreenFragment()
            }
            3 -> {
                action = HomeFragmentDirections.actionGlobalCallScreenFragment()
            }
            4 -> {
                action = HomeFragmentDirections.actionGlobalSoundFunnyFragment()
            }
            5 -> {
                action = HomeFragmentDirections.actionGlobalTaserPrankFragment()
            }
            6 -> {
                action = HomeFragmentDirections.actionGlobalSettingFragment()
            }
        }

        findNavController().navigate(action as NavDirections)
    }

}




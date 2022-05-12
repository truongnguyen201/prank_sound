package com.hola360.pranksounds.ui.home

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.github.ybq.android.spinkit.style.Circle
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentHomeBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.home.section.PrankSection
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.item_decoration.GridSpacingItemDecoration
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter

class HomeFragment : BaseFragment<FragmentHomeBinding>(), PrankSection.ClickListener {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var action: Any


    override fun getLayout(): Int {
        return R.layout.fragment_home
    }

    override fun initView() {
        val sectionAdapter = SectionedRecyclerViewAdapter()
        val sectionParameters = SectionParameters.builder()
            .itemResourceId(R.layout.item_prank)
            .headerResourceId(R.layout.section_home_prank)
            .build()

        val prankSection =
            PrankSection(sectionParameters, Constants.BANNER_IMAGE, Constants.PRANK_LIST, this)

        sectionAdapter.addSection(prankSection)

        binding.apply {
            val glm = GridLayoutManager(requireContext(), 2)
            glm.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (sectionAdapter.getSectionItemViewType(position) == SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER) {
                        2
                    } else 1
                }
            }
            rvPranks.layoutManager = glm
            rvPranks.setHasFixedSize(true)
            val spacing = resources.getDimensionPixelSize(R.dimen.home_prank_space)
            rvPranks.addItemDecoration(
                GridSpacingItemDecoration(
                    2,
                    spacing, false, 1
                )
            )
            rvPranks.adapter = sectionAdapter
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
                action = HomeFragmentDirections.actionHomeFragmentToSoundFunnyFragment()
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

    override fun onItemRootViewClicked(section: PrankSection, itemAdapterPosition: Int) {
        handleOnItemClick(itemAdapterPosition)
    }
}




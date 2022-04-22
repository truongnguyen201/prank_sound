package com.hola360.pranksounds.ui.sound_funny.category

import androidx.navigation.fragment.navArgs
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentCategoryBinding
import com.hola360.pranksounds.ui.base.BaseFragment

class CategoryFragment : BaseFragment<FragmentCategoryBinding>() {

    private val args: CategoryFragmentArgs by navArgs()

    override fun getLayout(): Int {
        return R.layout.fragment_category
    }

    override fun initView() {
        requireActivity().title = args.categoryTitle
        binding.tvTitle.text = args.categoryTitle
    }

    override fun initViewModel() {

    }

}
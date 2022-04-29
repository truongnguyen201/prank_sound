package com.hola360.pranksounds.ui.sound_funny.category

import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentDetailCategoryBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.sound_funny.category.adapter.CategoryAdapter

class DetailCategoryFragment : BaseFragment<FragmentDetailCategoryBinding>() {
    private lateinit var categoryAdapter: CategoryAdapter
    private val args: DetailCategoryFragmentArgs by navArgs()

    override fun getLayout(): Int {
        return R.layout.fragment_detail_category
    }

    override fun initView() {
        requireActivity().title = args.categoryTitle
        categoryAdapter = CategoryAdapter { handleOnItemClick(it) }
        binding.apply {
            rvSound.layoutManager = LinearLayoutManager(requireContext())
            rvSound.setHasFixedSize(true)
            rvSound.adapter = categoryAdapter
        }

    }

    override fun initViewModel() {

    }

    private fun handleOnItemClick(title: String) {

    }

}
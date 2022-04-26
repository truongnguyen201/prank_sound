package com.hola360.pranksounds.ui.sound_funny.category

import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentCategoryBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.sound_funny.category.adapter.CategoryAdapter

class CategoryFragment : BaseFragment<FragmentCategoryBinding>() {
    private lateinit var categoryAdapter: CategoryAdapter
    private val args: CategoryFragmentArgs by navArgs()

    override fun getLayout(): Int {
        return R.layout.fragment_category
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
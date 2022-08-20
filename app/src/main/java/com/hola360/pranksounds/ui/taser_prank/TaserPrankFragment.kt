package com.hola360.pranksounds.ui.taser_prank

import androidx.recyclerview.widget.GridLayoutManager
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentTaserPrankBinding
import com.hola360.pranksounds.ui.base.AbsBaseFragment
import com.hola360.pranksounds.ui.base.BaseScreenWithViewModelFragment
import com.hola360.pranksounds.ui.taser_prank.adapter.TaserPrankAdapter
import com.hola360.pranksounds.utils.Constants

class TaserPrankFragment : BaseScreenWithViewModelFragment<FragmentTaserPrankBinding>(), TaserPrankAdapter.OptionSelect{
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapter: TaserPrankAdapter
    private lateinit var action: Any
    override fun getLayout(): Int {
        return R.layout.fragment_taser_prank
    }

    override fun initView() {
        binding.apply {
            rcTaserPranks.layoutManager = layoutManager
            rcTaserPranks.adapter = adapter
            tbCallScreen.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun initViewModel() {
        layoutManager = GridLayoutManager(requireContext(), Constants.PRANK_OPTION_NUMBER_COLUMNS)
        adapter = TaserPrankAdapter(this)
        adapter.setData(Constants.TASER_LIST)

    }

    override fun onOptionSelected(position: Int) {
        when (position) {
            0 -> {
                mainActivity.showToast("0")
            }
            1 -> {
//                action = HomeFragmentDirections.actionGlobalHairCuttingFragment()
                mainActivity.showToast("1")
            }
            2 -> {
//                action = HomeFragmentDirections.actionGlobalBrokenScreenFragment()
                mainActivity.showToast("2")
            }
            3 -> {
//                action = HomeFragmentDirections.actionGlobalCallerFragment()
                mainActivity.showToast("3")
            }
            else -> {
                mainActivity.showToast("Coming soon")
                return
            }
        }
    }

}
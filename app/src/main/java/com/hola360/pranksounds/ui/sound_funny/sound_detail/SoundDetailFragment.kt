package com.hola360.pranksounds.ui.sound_funny.sound_detail

import android.content.res.Resources
import android.view.View
import android.widget.PopupWindow
import androidx.fragment.app.activityViewModels
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentSoundDetailBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.sound_funny.detail_category.SharedViewModel
import com.hola360.pranksounds.utils.Utils

class SoundDetailFragment : BaseFragment<FragmentSoundDetailBinding>() {

    private val sharedVM by activityViewModels<SharedViewModel>()
    private lateinit var popupWindow: PopupWindow
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels - 100

    override fun getLayout(): Int {
        return R.layout.fragment_sound_detail
    }

    override fun initView() {
        val setAsListener = View.OnClickListener {
            popupWindow.dismiss()
        }
        popupWindow = Utils.showPopUpSetAs(requireActivity(), setAsListener)

        binding.apply {
            toolbar.apply {
                setNavigationOnClickListener {
                    requireActivity().onBackPressed()
                }
            }
            ibSetAs.setOnClickListener {
                popupWindow.showAsDropDown(
                    toolbar, (screenWidth * 0.7).toInt(),
                    -(toolbar.height * 0.8).toInt()
                )
            }
        }
    }

    override fun initViewModel() {
        sharedVM.currentPosition.observe(this) {
            it?.let {
                if (sharedVM.soundList.value!!.size > 0) {
                    val sound = sharedVM.soundList.value!![it]
                    binding.toolbar.title = sound.title
                    if (sharedVM.favoriteList.value!!.contains(sound)) {
                        binding.cbFavorite.isChecked = true
                    }
                }
            }
        }
    }


}
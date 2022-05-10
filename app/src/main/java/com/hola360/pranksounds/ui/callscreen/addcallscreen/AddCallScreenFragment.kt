package com.hola360.pranksounds.ui.callscreen.addcallscreen

import android.R.attr
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.PhotoModel
import com.hola360.pranksounds.databinding.FragmentAddCallScreenBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.dialog.pickphoto.PickPhotoDialog
import com.hola360.pranksounds.utils.Utils
import com.yalantis.ucrop.UCrop
import java.io.File


class AddCallScreenFragment : BaseFragment<FragmentAddCallScreenBinding>(), PickPhotoDialog.OnClickListener {
    private lateinit var addCallScreenViewModel: AddCallScreenViewModel
    private lateinit var action: Any
    private val SAMPLE_CROPPED_IMG_NAME = "SampleCropImg"

    override fun getLayout(): Int {
        return R.layout.fragment_add_call_screen
    }

    override fun initView() {
        binding.viewModel = addCallScreenViewModel
        with(binding.tbAddCallScreen) {
//            setOnMenuItemClickListener {
//                when(it.itemId) {
//                    R.id.add_new_call -> {
//                        action = CallScreenFragmentDirections.actionGlobalAddCallScreenFragment()
//                        findNavController().navigate(action as NavDirections)
//                        true
//                    }
//                    else -> false
//                }
//            }

            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
        with(binding) {
            imgAvatar.setOnClickListener {
                if (Utils.storagePermissionGrant(requireContext())) {
                    setUpDialog()
                }
                else {
                    requestStoragePermission()
                }
            }
        }
    }

    override fun initViewModel() {
        val factory = AddCallScreenViewModel.Factory(requireActivity().application)
        addCallScreenViewModel = ViewModelProvider(this, factory)[AddCallScreenViewModel::class.java]


    }

    private fun requestStoragePermission() {
        resultLauncher.launch(
            Utils.getStoragePermissions()
        )
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (Utils.storagePermissionGrant(requireContext())
            ) {
                setUpDialog()
            } else {
                Utils.showAlertPermissionNotGrant(binding.root, requireActivity())
            }
        }

    private fun setUpDialog() {
        val dialog = PickPhotoDialog.create(this)
        dialog.show(parentFragmentManager, "Pick photo")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("999 TAG", "onActivityResult: photobinding", )
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val imageUri = data?.let { UCrop.getOutput(it) }
            if (imageUri != null) {
                Log.e("set uri", "onActivityResult: photobinding", )
                binding.imgAvatar.setImageURI(imageUri)
            }
        }
    }

    private fun startCrop(uri: Uri) {
        var destinationFileName = SAMPLE_CROPPED_IMG_NAME
        destinationFileName += ".jpg"
        var uCrop = UCrop.of(uri, Uri.fromFile(File(requireActivity().cacheDir, destinationFileName)))
        uCrop.withAspectRatio(1F, 1F)
        uCrop.withMaxResultSize(450, 450)
        uCrop.withOptions(getCropOptions())
        uCrop.start(requireContext(), this)
    }


    private fun getCropOptions() : UCrop.Options{
        var options = UCrop.Options()
        options.setCompressionQuality(70)

        options.setHideBottomControls(false)
        options.setFreeStyleCropEnabled(true)
        options.setStatusBarColor(requireActivity().resources.getColor(R.color.design_color))
        options.setToolbarColor(requireActivity().resources.getColor(R.color.design_color))
        options.setToolbarWidgetColor(requireActivity().resources.getColor(R.color.white))
        options.setToolbarTitle("Crop image")
        return options
    }
    override fun onPickPhoto(photoModel: PhotoModel) {
        Log.e("TAG", "on pick photo: ", )
//        startActivityForResult(Intent().setAction(
//            Intent.ACTION_GET_CONTENT)
//            .setType("image/*"), 1)/
//        val uri = Uri.parse(photoModel.file)
        startCrop(photoModel.uri)
    }

}
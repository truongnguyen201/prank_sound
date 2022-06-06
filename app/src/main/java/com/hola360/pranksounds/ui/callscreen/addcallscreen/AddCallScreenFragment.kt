package com.hola360.pranksounds.ui.callscreen.addcallscreen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.view.View
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.model.PhotoModel
import com.hola360.pranksounds.databinding.FragmentAddCallScreenBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.callscreen.CallScreenSharedViewModel
import com.hola360.pranksounds.ui.callscreen.ShareViewModelStatus
import com.hola360.pranksounds.ui.dialog.pickphoto.PickPhotoDialog
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import java.io.File
import java.util.*
import kotlin.time.Duration.Companion.seconds


class AddCallScreenFragment : BaseFragment<FragmentAddCallScreenBinding>(),
    PickPhotoDialog.OnClickListener {
    private lateinit var addCallScreenViewModel: AddCallScreenViewModel
    private val sharedViewModel by activityViewModels<CallScreenSharedViewModel>()

    override fun getLayout(): Int {
        return R.layout.fragment_add_call_screen
    }

    override fun initView() {
        binding.viewModel = addCallScreenViewModel
        with(binding.tbAddCallScreen) {
            setNavigationOnClickListener {
                if (sharedViewModel.getCall()?.isLocal == true) {
                    sharedViewModel.setBackToMyCaller(true)
                }
                else {
                    sharedViewModel.setBackToMyCaller(false)
                }
                sharedViewModel.setCall(addCallScreenViewModel.officialModel)
                requireActivity().onBackPressed()
            }
        }
        addCallScreenViewModel.getCurrentCall()?.let { setView(it) }
        with(binding) {
            imgAvatar.setOnClickListener {
                if (Utils.storagePermissionGrant(requireContext())) {
                    setUpDialog()
                } else {
                    requestStoragePermission()
                }
            }

            tvCallerName.doAfterTextChanged {
                addCallScreenViewModel.setOnNameChange(tvCallerName.text.toString())
            }

            tvPhoneNumber.doAfterTextChanged {
                addCallScreenViewModel.setOnPhoneNumberChange(tvPhoneNumber.text.toString())
            }

            btnAdd.setOnClickListener {
                addCallScreenViewModel.addCallToLocal(tvCallerName.text.toString(), tvPhoneNumber.text.toString())
                sharedViewModel.setCall(addCallScreenViewModel.getCurrentCall())
                sharedViewModel.setBackToMyCaller(true)
                Toast.makeText(requireContext(), requireContext().resources.getString(R.string.insert_success), Toast.LENGTH_LONG).show()
                requireActivity().onBackPressed()
            }

            tvDefaultImg.setOnClickListener {
                addCallScreenViewModel.setAvatarUrl("")
                imgAvatar.setImageResource(R.drawable.img_avatar_default)
            }
        }

        if (sharedViewModel.getCall() != null) {
            binding.tbAddCallScreen.title = requireActivity().getString(R.string.edit_call_screen)
        }
    }

    override fun initViewModel() {
        val factory = AddCallScreenViewModel.Factory(requireActivity().application)
        addCallScreenViewModel =
            ViewModelProvider(this, factory)[AddCallScreenViewModel::class.java]
        setDataByViewModel()
    }

    private fun setDataByViewModel() {
        when (sharedViewModel.getStatus()) {
            ShareViewModelStatus.AddCall -> {
                addCallScreenViewModel.setCall(null)
            }
            ShareViewModelStatus.EditCall -> {
                addCallScreenViewModel.setCall(sharedViewModel.getCall())
            }
            else -> {
                addCallScreenViewModel.setCall(null)
            }
        }

//        addCallScreenViewModel.callLiveData.observe(this) {
//            it?.let {
//                setView(it)
//            }
//        }

        sharedViewModel.setStatus(ShareViewModelStatus.Default)
        sharedViewModel.setCall(null)
    }

    private fun setView(call: Call) {
        with(binding) {
            val path =
                if (call.isLocal) call.avatarUrl else Constants.SUB_URL + call.avatarUrl
            if (path.isEmpty()) {
                imgAvatar.setImageResource(R.drawable.img_avatar_default)
            } else {
                imgAvatar.let { imgView ->
                    Glide.with(imgView)
                        .load(path)
                        .placeholder(R.drawable.img_avatar_default)
                        .error(R.drawable.img_avatar_default)
                        .into(imgView)
                }
            }
            tvCallerName.setText(call.name)
            tvPhoneNumber.setText(call.phone)
        }
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
        addCallScreenViewModel.setIsLocal(true)
        val dialog = PickPhotoDialog.create(this)
        dialog.show(parentFragmentManager, "Pick photo")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val imageUri = data?.let { UCrop.getOutput(it) }
            if (imageUri != null) {
                binding.imgAvatar.setImageURI(imageUri)
                addCallScreenViewModel.setAvatarUrl(imageUri.path.toString())
            }
        }
    }

    private fun startCrop(uri: Uri) {
        val date: Date = Calendar.getInstance().time
        var destinationFileName = date.time.seconds.toString()
        destinationFileName += ".jpg"
        val uCrop =
            UCrop.of(uri, Uri.fromFile(File(requireActivity().cacheDir, destinationFileName)))
        uCrop.withAspectRatio(1F, 1F)
        uCrop.withMaxResultSize(450, 450)
        uCrop.withOptions(getCropOptions())
        uCrop.start(requireContext(), this)
    }

    private fun getCropOptions(): UCrop.Options {
        val options = UCrop.Options()
        options.setCompressionQuality(70)

        options.setHideBottomControls(false)
        options.setFreeStyleCropEnabled(false)
        options.setStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.design_color))
        options.setToolbarColor(ContextCompat.getColor(requireActivity(), R.color.design_color))
        options.setToolbarWidgetColor(ContextCompat.getColor(requireActivity(), R.color.white))
        options.setToolbarTitle("Crop image")
        options.setAspectRatioOptions(0, AspectRatio("",1f, 1f))
        return options
    }

    override fun onPickPhoto(photoModel: PhotoModel) {
        startCrop(photoModel.uri)
    }
}
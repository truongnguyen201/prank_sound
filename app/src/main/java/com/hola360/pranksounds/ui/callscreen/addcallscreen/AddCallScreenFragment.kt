package com.hola360.pranksounds.ui.callscreen.addcallscreen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.ResultDataResponse
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.model.PhotoModel
import com.hola360.pranksounds.databinding.FragmentAddCallScreenBinding
import com.hola360.pranksounds.ui.base.BaseScreenWithViewModelFragment
import com.hola360.pranksounds.ui.callscreen.CallScreenSharedViewModel
import com.hola360.pranksounds.ui.callscreen.data.ShareViewModelStatus
import com.hola360.pranksounds.ui.dialog.pickphoto.PickPhotoDialog
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import java.io.File
import java.util.*
import kotlin.time.Duration.Companion.seconds


@Suppress("DEPRECATION")
class AddCallScreenFragment : BaseScreenWithViewModelFragment<FragmentAddCallScreenBinding>(),
    PickPhotoDialog.OnClickListener {
    private lateinit var addCallScreenViewModel: AddCallScreenViewModel
    private lateinit var sharedViewModel: CallScreenSharedViewModel
    private var isSubmit = false
    private var mLastClickTime: Long = 0
    private lateinit var dialog: PickPhotoDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (isBindingInitialized) {
            setCall()
            return mView
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_add_call_screen
    }

    override fun initView() {
        binding.viewModel = addCallScreenViewModel
        with(binding.tbAddCallScreen) {
            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
        setCall()
        with(binding) {
            imgAvatar.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime > resources.getInteger(R.integer.long_period)) {
                    if (Utils.storagePermissionGrant(requireContext())) {
                        setUpDialog()
                    } else {
                        requestStoragePermission()
                    }
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }

            tvCallerName.doAfterTextChanged {
                addCallScreenViewModel.setOnNameChange(tvCallerName.text.toString())
            }

            tvPhoneNumber.doAfterTextChanged {
                addCallScreenViewModel.setOnPhoneNumberChange(tvPhoneNumber.text.toString())
            }

            btnAdd.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime > resources.getInteger(R.integer.long_period)) {
                    isSubmit = true
                    addCallScreenViewModel.addCallToLocal()
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }

            tvDefaultImg.setOnClickListener {
                addCallScreenViewModel.setAvatarUrl("")
                imgAvatar.setImageResource(R.drawable.img_avatar_default)
            }
        }

        if (sharedViewModel.resultLiveData.value?.resultCode == ShareViewModelStatus.EditCall.ordinal) {
            binding.tbAddCallScreen.title = requireActivity().getString(R.string.edit_call_screen)
        }
    }

    override fun initViewModel() {
        retainInstance = true
        sharedViewModel = CallScreenSharedViewModel.getInstance(mainActivity.application)
        val factory = AddCallScreenViewModel.Factory(requireActivity().application)
        addCallScreenViewModel =
            ViewModelProvider(this, factory)[AddCallScreenViewModel::class.java]

        setDataByViewModel()
        addCallScreenViewModel.saveCallDone.observe(this) {
            it?.let {
                if (it is DataResponse.DataSuccess) {
                    val body = it.body
                    sharedViewModel.setResultData(ShareViewModelStatus.SetCall.ordinal, body)
                    sharedViewModel.setBackToMyCaller(true)
                    mainActivity.showToast(getString(R.string.insert_success))
                    requireActivity().onBackPressed()
                }
            }
        }
        retainInstance = true
    }

    fun setCall() {
        addCallScreenViewModel.getCurrentCall()?.let {
            sharedViewModel.setBackToMyCaller(it.isLocal)
            setView(it)
        }
    }

    private fun setDataByViewModel() {
        when (sharedViewModel.resultLiveData.value?.resultCode) {
            ShareViewModelStatus.AddCall.ordinal -> {
                addCallScreenViewModel.setCall(null)
            }
            ShareViewModelStatus.EditCall.ordinal -> {
                val body =
                    (sharedViewModel.resultLiveData.value as ResultDataResponse.ResultDataSuccess).body
                addCallScreenViewModel.setCall(body.copy())
            }
            else -> {
                addCallScreenViewModel.setCall(null)
            }
        }
    }

    private fun setView(call: Call) {
        with(binding) {
            val path =
                if (call.isLocal) call.avatarUrl else Constants.SUB_URL + call.avatarUrl
            if (path.isEmpty()) {
                imgAvatar.setImageResource(R.drawable.img_avatar_default)
            } else {
                imgAvatar.let { imgView ->
                    Glide.with(requireContext())
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

    private fun setUpBackPress() {
        if (sharedViewModel.getCall()?.isLocal == true) {
            sharedViewModel.setBackToMyCaller(true)
        } else {
            sharedViewModel.setBackToMyCaller(false)
        }
    }

    @Suppress("DEPRECATION")
    private fun setUpDialog() {
        addCallScreenViewModel.setIsLocal(true)
        dialog = PickPhotoDialog.create()
        dialog.setOnClickListener(this)
        dialog.retainInstance = true
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
        options.setAspectRatioOptions(0, AspectRatio("", 1f, 1f))
        return options
    }

    override fun onPickPhoto(photoModel: PhotoModel) {
        startCrop(photoModel.uri)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (!isSubmit)
                setUpBackPress()
            findNavController().navigateUp()
        }
    }
}
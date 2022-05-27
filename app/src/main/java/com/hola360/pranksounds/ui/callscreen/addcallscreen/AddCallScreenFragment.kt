package com.hola360.pranksounds.ui.callscreen.addcallscreen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.view.View
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
    private lateinit var action: Any
    private var call: Call? = null
    private val args: AddCallScreenFragmentArgs by navArgs()
    private val sharedViewModel by activityViewModels<CallScreenSharedViewModel>()

    override fun getLayout(): Int {
        return R.layout.fragment_add_call_screen
    }

    override fun initView() {
        //args.callModel null, #null id >
        binding.viewModel = addCallScreenViewModel
        with(binding.tbAddCallScreen) {
            setNavigationOnClickListener {
                if (sharedViewModel.getCall()?.isLocal == true) {
                    sharedViewModel.setBackToMyCaller(true)
                }
                else {
                    sharedViewModel.setBackToMyCaller(false)
                }
                requireActivity().onBackPressed()
            }
        }

        with(binding) {
            imgAvatar.setOnClickListener {
                if (Utils.storagePermissionGrant(requireContext())) {
                    setUpDialog()
                } else {
                    requestStoragePermission()
                }
            }

            tvCallerName.doAfterTextChanged {
                viewModel!!.setOnNameChange(tvCallerName.text.toString())
            }

            tvPhoneNumber.doAfterTextChanged {
                viewModel!!.setOnPhoneNumberChange(tvPhoneNumber.text.toString())
            }

            btnAdd.setOnClickListener {
                viewModel!!.addCallToLocal()
                sharedViewModel.setCall(viewModel!!.getCurrentCall())
                sharedViewModel.setBackToMyCaller(true)
                Toast.makeText(requireContext(), requireContext().resources.getString(R.string.insert_success), Toast.LENGTH_LONG).show()
                requireActivity().onBackPressed()
            }

            tvDefaultImg.setOnClickListener {
                viewModel!!.setAvatarDefault()
                imgAvatar.setImageResource(R.drawable.img_avatar_default)
            }
        }

        call = args.callModel
        if (call != null) {
            binding.tbAddCallScreen.title = requireActivity().getString(R.string.edit_call_screen)
            call?.let {
                setView(it)
                sharedViewModel.setCall(it)
            }
        }
//        addCallScreenViewModel.setCall(call)
    }

    override fun initViewModel() {
        val factory = AddCallScreenViewModel.Factory(requireActivity().application, args.callModel)
        addCallScreenViewModel =
            ViewModelProvider(this, factory)[AddCallScreenViewModel::class.java]
//        sharedViewModel.setCall(args.callModel)
        setDataByViewModel()
    }

    private fun setDataByViewModel() {
        sharedViewModel.myCall.observe(this) {
            addCallScreenViewModel.setCall(it)
        }
//        addCallScreenViewModel.setCall(sharedViewModel.myCall.value)
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
        val dialog = PickPhotoDialog.create(this)
        dialog.show(parentFragmentManager, "Pick photo")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val imageUri = data?.let { UCrop.getOutput(it) }
            if (imageUri != null) {
                binding.imgAvatar.setImageURI(imageUri)
                binding.viewModel!!.curCallModel!!.avatarUrl = imageUri.path.toString()
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
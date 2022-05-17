package com.hola360.pranksounds.ui.callscreen.addcallscreen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.model.PhotoModel
import com.hola360.pranksounds.databinding.FragmentAddCallScreenBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.callscreen.CallScreenFragmentDirections
import com.hola360.pranksounds.ui.dialog.pickphoto.PickPhotoDialog
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*
import kotlin.time.Duration.Companion.seconds


class AddCallScreenFragment : BaseFragment<FragmentAddCallScreenBinding>(),
    PickPhotoDialog.OnClickListener {
    private lateinit var addCallScreenViewModel: AddCallScreenViewModel
    private lateinit var action: Any
    private var call: Call? = null
    private val args: AddCallScreenFragmentArgs by navArgs();

    override fun getLayout(): Int {
        return R.layout.fragment_add_call_screen
    }

    override fun initView() {
        //args.callModel null, #null id >0
        binding.viewModel = addCallScreenViewModel
        with(binding.tbAddCallScreen) {

            setNavigationOnClickListener {
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
                Toast.makeText(requireContext(), "Insert success!", Toast.LENGTH_LONG).show()
                requireActivity().onBackPressed()
//                action = CallScreenFragmentDirections.actionGlobalCallScreenFragment()
//                findNavController().navigate(action as NavDirections)
            }
        }

        call = args.callModel
        if (call != null) {
            addCallScreenViewModel.setCall(call!!)
            with(binding) {
                call?.let {
                    val path =
                        if (call!!.isLocal) call!!.avatarUrl else Constants.SUB_URL + it.avatarUrl
                    imgAvatar.setImageURI(null)
                    imgAvatar.let { imgView ->
                        Glide.with(imgView)
                            .load(path)
                            .placeholder(R.drawable.smaller_loading)
                            .error(R.drawable.img_avatar_default)
                            .into(imgView)
                    }
                    imgAvatar.setImageURI(Uri.parse(it.avatarUrl))
                    tvCallerName.setText(it.name)
                    tvPhoneNumber.setText(it.phone)
                }
            }

        }
    }

    override fun initViewModel() {
        val factory = AddCallScreenViewModel.Factory(requireActivity().application,args.callModel)
        addCallScreenViewModel =
            ViewModelProvider(this, factory)[AddCallScreenViewModel::class.java]

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
//            binding.viewModel!!.imageUri.value = imageUri
        }
    }

    private fun startCrop(uri: Uri) {
        val date: Date = Calendar.getInstance().time
        var destinationFileName = date.time.seconds.toString()
        destinationFileName += ".jpg"
        var uCrop =
            UCrop.of(uri, Uri.fromFile(File(requireActivity().cacheDir, destinationFileName)))
        uCrop.withAspectRatio(1F, 1F)
        uCrop.withMaxResultSize(450, 450)
        uCrop.withOptions(getCropOptions())
        uCrop.start(requireContext(), this)
    }


    private fun getCropOptions(): UCrop.Options {
        var options = UCrop.Options()
        options.setCompressionQuality(70)

        options.setHideBottomControls(false)
        options.setFreeStyleCropEnabled(true)
        options.setStatusBarColor( ContextCompat.getColor(requireActivity(),R.color.design_color))
        options.setToolbarColor(requireActivity().resources.getColor(R.color.design_color))
        options.setToolbarWidgetColor(requireActivity().resources.getColor(R.color.white))
        options.setToolbarTitle("Crop image")
        return options
    }


    override fun onPickPhoto(photoModel: PhotoModel) {
        Log.e("TAG", "on pick photo: ")
        startCrop(photoModel.uri)
    }

}
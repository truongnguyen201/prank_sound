package com.hola360.pranksounds.ui.callscreen.callingscreen

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.ActivityCallingBinding
import com.hola360.pranksounds.utils.Constants


class CallingActivity : AppCompatActivity() {
    lateinit var binding: ActivityCallingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        var call = Call()
        val args = intent?.getParcelableExtra<Call>("call")
        if (args != null)
            call =  args as Call
        Log.e("call activity", ": ${call.avatarUrl}", )
        with(binding) {
            val path =
                if (call!!.isLocal) call!!.avatarUrl else Constants.SUB_URL + call!!.avatarUrl
            imgAvatar.let { imgView ->
                Glide.with(imgView)
                    .load(path)
                    .placeholder(R.drawable.smaller_loading)
                    .error(R.drawable.img_avatar_default)
                    .into(imgView)
            }
            tvCallerName.text = call!!.name
            tvPhoneNumber.text = call!!.phone
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }
}
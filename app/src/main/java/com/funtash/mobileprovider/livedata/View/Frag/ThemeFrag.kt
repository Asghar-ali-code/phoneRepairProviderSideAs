package com.funtash.mobileprovider.livedata.View.Frag

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.funtash.mobileprovider.Api.RetrofitClient
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.Utils.FileUtils
import com.funtash.mobileprovider.Utils.NoConnectivityException
import com.funtash.mobileprovider.Utils.Utility
import com.funtash.mobileprovider.databinding.ProfileFragBinding
import com.funtash.mobileprovider.databinding.ThemeFragBinding
import com.funtash.mobileprovider.response.LoginClass
import com.funtash.mobilerepairinguserapp.Api.ApiInterface
import com.github.dhaval2404.imagepicker.ImagePicker
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ThemeFrag : Fragment() {

    var ctx: Context? = null
    private var api_token: String? = null
    private var alertDialog: LottieAlertDialog? = null
    private lateinit var binding: ThemeFragBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ThemeFragBinding.inflate(inflater,container,false)

        initUI()
        initdialog()
        clicks()
        return binding.root
    }

    private fun initUI() {
        try {
            api_token = Prefs.getString("api_token", "")

        } catch (e: Exception) {
            Log.e("exp", "error" + e.message)
        }

    }

    private fun initdialog() {
        alertDialog = LottieAlertDialog.Builder(requireActivity(), DialogTypes.TYPE_LOADING)
            .setTitle("Language")
            .setDescription("Please wait a moment!")
            .build()
        alertDialog!!.setCancelable(false)
    }

    private fun clicks() {



    }
}
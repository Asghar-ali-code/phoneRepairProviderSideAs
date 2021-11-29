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

class ProfileFragment : Fragment() {

    var ctx: Context? = null
    private var fileUri: Uri? = null
    private var filePart: MultipartBody.Part? = null
    private var filePart2: MultipartBody.Part? = null
    private var api_token: String? = null
    private var imgCode: Int = 1
    private var alertDialog: LottieAlertDialog? = null
    private lateinit var binding: ProfileFragBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragBinding.inflate(inflater,container,false)

        initUI()
        initdialog()
        clicks()
        return binding.root
    }

    private fun initUI() {
        try {
            api_token = Prefs.getString("api_token", "")
            val name = Prefs.getString("name", "")
            val email = Prefs.getString("email", "")
            val profie_pic = Prefs.getString("profile_pic", "")
            val profie_pic2 = Prefs.getString("profile_pic2", "")
            Glide.with(this)
                .load(profie_pic) //.placeholder(R.drawable.banner)
                .into(binding.ivFront)

            binding.edtname.setText(name)
            binding.edtemail.setText(email)

            Glide.with(this)
                .load(profie_pic2) //.placeholder(R.drawable.banner)
                .into(binding.ivCover)

        } catch (e: Exception) {
            Log.e("exp", "error" + e.message)
        }

    }

    private fun initdialog() {
        alertDialog = LottieAlertDialog.Builder(requireActivity(), DialogTypes.TYPE_LOADING)
            .setTitle("Profile Updation")
            .setDescription("Please wait a moment!")
            .build()
        alertDialog!!.setCancelable(false)
    }

    private fun clicks() {

        binding.llFront.setOnClickListener {
            imgCode = 1
            checkRunTimePermission()
        }

        binding.llCover.setOnClickListener {
            imgCode = 2
            checkRunTimePermission()
        }

        binding.btnsave.setOnClickListener {
            updateProfile()
        }

    }

    private fun checkRunTimePermission() {
        Permissions.check(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            null,
            object : PermissionHandler() {
                override fun onGranted() {
                    openGallery()
                }
            })

    }

    private fun openGallery() {
        activity?.let {
            ImagePicker.with(it)
                .galleryOnly()
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                var file = data?.data!!

                fileUri = file
                Log.e("fileUri", fileUri.toString())
                if (imgCode == 1) {
                    filePart = prepareFilePart(
                        "profile_pic",
                        FileUtils.getFile(
                            activity, fileUri
                        )
                    )
                    binding.ivFront.setImageURI(fileUri)
                } else {
                    filePart2 = prepareFilePart(
                        "profile_pic2",
                        FileUtils.getFile(
                            activity, fileUri
                        )
                    )
                    binding.ivCover.setImageURI(fileUri)
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
            } else {
            }
        }

    private fun updateProfile() {
        when {
            TextUtils.isEmpty(binding.edtname.text.toString()) -> {
                YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(binding.edtname)
                binding.edtname.error = "Enter Name!"
                binding.edtname.requestFocus()
            }
            TextUtils.isEmpty(binding.edtmobile.text.toString()) -> {
                YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(binding.edtmobile)
                binding.edtmobile.error = "Enter Phone Number!"
                binding.edtmobile.requestFocus()
            }
            binding.edtmobile.text.toString().length < 11 -> {
                YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(binding.edtmobile)
                binding.edtmobile.error = "Enter Correct phone Number!"
                binding.edtmobile.requestFocus()
            }
            !binding.edtpass.text.toString().equals("") -> {
                if (binding.edtpass.text.length < 6) {
                    YoYo.with(Techniques.BounceIn)
                        .duration(700)
                        .playOn(binding.edtpass)
                    binding.edtpass.error = "Password Must be 6 character long!"
                    binding.edtpass.requestFocus()
                }
            }
            else -> {
                alertDialog!!.show()
                val map: HashMap<String, RequestBody> = HashMap()
                map["name"] = prepareDataPart(binding.edtname.text.toString())
                map["phone_number"] = prepareDataPart(binding.edtmobile.text.toString())
                map["password"] = prepareDataPart(binding.edtpass.text.toString())
                Log.e("post", "Data: $map")
                val apiService = RetrofitClient.getClientRetro(ctx).create(ApiInterface::class.java)
                val call =
                    apiService?.update_profile(api_token.toString(), map, filePart, filePart2)
                call?.enqueue(object : Callback<LoginClass> {
                    override fun onResponse(
                        call: Call<LoginClass>,
                        response: Response<LoginClass>
                    ) {
                        try {
                            Log.e("response", "" + response.body())
                            if (response.body()?.success == true) {
                                try {
                                    alertDialog!!.dismiss()
                                    var successDlg: LottieAlertDialog =
                                        LottieAlertDialog.Builder(ctx, DialogTypes.TYPE_SUCCESS)
                                            .setTitle("Profile!")
                                            .setDescription(response.body()?.message.toString())
                                            .setPositiveText("OK")
                                            .setPositiveButtonColor(
                                                ContextCompat.getColor(
                                                    requireActivity(),
                                                    R.color.blue
                                                )
                                            )
                                            .setPositiveTextColor(
                                                ContextCompat.getColor(
                                                    requireActivity(),
                                                    R.color.white
                                                )
                                            )
                                            // Error View
                                            .setPositiveListener(object : ClickListener {
                                                override fun onClick(dialog: LottieAlertDialog) {
                                                    // This is the usage same instance of view
                                                    Prefs.putString(
                                                        "api_token",
                                                        response.body()?.data?.api_token.toString()
                                                    )
                                                    Prefs.putString(
                                                        "name",
                                                        response.body()?.data?.name.toString()
                                                    )
                                                    Prefs.putString(
                                                        "email",
                                                        response.body()?.data?.email.toString()
                                                    )
                                                    Prefs.putString(
                                                        "phone",
                                                        response.body()?.data?.phone_number.toString()
                                                    )
                                                    Prefs.putString(
                                                        "profile_pic",
                                                        response.body()?.data?.profile_pic.toString()
                                                    )
                                                    Prefs.putString(
                                                        "profile_pic2",
                                                        response.body()?.data?.profile_pic2.toString()
                                                    )
                                                    dialog.dismiss()
                                                    activity?.finish()
                                                }
                                            })
                                            .build()
                                    successDlg.show()
                                } catch (e: Exception) {
                                }
                            } else {
                                alertDialog!!.dismiss()
                                try {
                                    var successDlg: LottieAlertDialog = LottieAlertDialog.Builder(
                                        requireActivity(),
                                        DialogTypes.TYPE_WARNING
                                    )
                                        .setTitle("Profile!")
                                        .setDescription(response.body()?.message.toString())
                                        .setPositiveText("OK")
                                        .setPositiveButtonColor(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                R.color.blue
                                            )
                                        )
                                        .setPositiveTextColor(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                R.color.white
                                            )
                                        )
                                        // Error View
                                        .setPositiveListener(object : ClickListener {
                                            override fun onClick(dialog: LottieAlertDialog) {
                                                // This is the usage same instance of view
                                                dialog.dismiss()
                                            }
                                        })
                                        .build()
                                    successDlg.show()
                                    Log.e("failed", response.errorBody()?.string().toString())

                                } catch (e: Exception) {
                                }
                            }
                        } catch (e: Exception) {
                            alertDialog!!.dismiss()
                            try {
                                Utility.warningSnackBar(
                                    binding.root, e.message.toString(),
                                    ctx!!
                                )
                            } catch (e: Exception) {
                                Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG)
                                    .show()
                            }

                        }
                    }

                    override fun onFailure(call: Call<LoginClass>, t: Throwable) {
                        alertDialog!!.dismiss()
                        try {
                            var message = ""
                            if (t is NoConnectivityException) {
                                message = "No Internet connection!"
                            } else {
                                message = t.message.toString()
                            }
                            System.out.println("Failure...!" + t.stackTrace);
                            Log.e("error", t.message.toString())
                            var successDlg: LottieAlertDialog
                            successDlg = LottieAlertDialog.Builder(ctx, DialogTypes.TYPE_ERROR)
                                .setTitle("Error")
                                .setDescription(message)
                                .setPositiveText("OK")
                                .setPositiveButtonColor(
                                    ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.blue
                                    )
                                )
                                .setPositiveTextColor(
                                    ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.white
                                    )
                                )
                                // Error View
                                .setPositiveListener(object : ClickListener {
                                    override fun onClick(dialog: LottieAlertDialog) {
                                        // This is the usage same instance of view
                                        dialog.dismiss()
                                    }
                                })
                                .build()
                            successDlg.show()

                        } catch (e: Exception) {
                            Toast.makeText(requireActivity(), e.toString(), Toast.LENGTH_LONG)
                                .show()
                        }

                    }
                })
            }
        }
    }

    @NonNull
    private fun prepareDataPart(name: String?): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), name!!)
    }

    @NonNull
    private fun prepareFilePart(name: String?, file: File?): MultipartBody.Part? {
        val requestFile =
            file?.let { RequestBody.create(FileUtils.getMimeType(file).toMediaTypeOrNull(), it) }
        return requestFile?.let { MultipartBody.Part.createFormData(name!!, file.name, it) }
    }
}
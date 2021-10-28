package com.funtash.mobileprovider.livedata.View.Activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.funtash.mobileprovider.Api.RetrofitClient
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.Utils.FileUtils
import com.funtash.mobileprovider.Utils.NoConnectivityException
import com.funtash.mobileprovider.Utils.Utility
import com.funtash.mobileprovider.databinding.ActivitySignupBinding
import com.funtash.mobileprovider.response.MessageClass
import com.funtash.mobileprovider.response.message
import com.funtash.mobilerepairinguserapp.Api.ApiInterface
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ActivitySignup : AppCompatActivity() {

    private var toggle_status: Boolean = false
    private var fileUri: Uri? = null
    private var filePart: MultipartBody.Part? = null
    private var alertDialog : LottieAlertDialog?=null
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UI()
        initdialog()
        clicks()

    }


    private fun UI() {

    }

    private fun initdialog() {
        alertDialog  = LottieAlertDialog.Builder(this, DialogTypes.TYPE_LOADING)
            .setTitle("Account Creation!")
            .setDescription("Please wait while we are creating your account.")
            .build()
        alertDialog!!.setCancelable(false)
    }

    private fun clicks() {

        binding.login.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,
            ActivityLogin::class.java)) })

        binding.ivEye.setOnClickListener {
            if (!toggle_status) {
                toggle_status = true
                binding.edtpass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.ivEye.setImageDrawable(resources.getDrawable(R.drawable.ic_eye_24))
            } else {
                toggle_status = false
                binding.edtpass.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.ivEye.setImageDrawable(resources.getDrawable(R.drawable.ic_eye_24))
            }
        }
        binding.imageView.setOnClickListener {
            checkRunTimePermission()
        }

        binding.btnsignin.setOnClickListener {
            createAccount()
        }
    }
    private fun checkRunTimePermission() {
        Permissions.check(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            null,
            object : PermissionHandler() {
                override fun onGranted() {
                    openGallery()
                }
            })

    }

    private fun openGallery() {
            ImagePicker.with(this)
                .galleryOnly()
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
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
                filePart = prepareFilePart(
                    "profile_pic",
                    FileUtils.getFile(
                        this, fileUri
                    )
                )
                binding.ivUser.setImageURI(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
            } else {
            }
        }

    private fun createAccount() {
        //get email and password
        val name = binding.edtname.text.toString()
        val email = binding.edtemail.text.toString()
        val password = binding.edtpass.text.toString()
        val firebaseToken = FirebaseInstanceId.getInstance().token
        when {
            TextUtils.isEmpty(binding.edtname.text.toString()) -> {
                YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(binding.edtname)
                binding.edtname.error = "Enter Your Name!"
                binding.edtname.requestFocus()
            }
            TextUtils.isEmpty(binding.edtemail.text.toString()) -> {
                YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(binding.edtemail)
                binding.edtemail.error = "Enter Email!"
                binding.edtemail.requestFocus()
            }
            !Patterns.EMAIL_ADDRESS.matcher(binding.edtemail.text.toString()).matches() -> {
                YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(binding.edtemail)
                binding.edtemail.error = "Enter correct Email!"
                binding.edtemail.requestFocus()
            }
            TextUtils.isEmpty(binding.edtpass.text.toString()) -> {
                YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(binding.edtpass)
                binding.edtpass.error = "Enter Password!"
                binding.edtpass.requestFocus()
            }
            binding.edtpass.text.toString().length < 6 -> {
                YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(binding.edtpass)
                binding.edtpass.error = "Password Must be 6 character long!"
                binding.edtpass.requestFocus()
            }
            filePart==null -> {
                YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(binding.imageView)
                Utility.displaySnackBar(binding.root, "Choose your Image!".toString(),
                    this@ActivitySignup)
            }
            else -> {
                alertDialog!!.show()
                val map: HashMap<String, RequestBody> = HashMap()
                map["name"] = prepareDataPart(name)
                map["email"] = prepareDataPart(email)
                map["password"] = prepareDataPart(password)
                map["device_token"] = prepareDataPart(firebaseToken)
                Log.e("post", "Data: $map")

                val apiService = RetrofitClient.getClientRetro(this).create(ApiInterface::class.java)
                val call = apiService?.provider_register(map, filePart!!)
                call?.enqueue(
                    object : Callback<MessageClass> {
                    override fun onResponse(
                        call: Call<MessageClass>,
                        response: Response<MessageClass>
                    ) {
                        try {
                            Log.e("response", "" + response.body())
                            if (response.body()?.success==true) {
                                try {
                                    alertDialog!!.dismiss()
                                    var  successDlg : LottieAlertDialog =
                                        LottieAlertDialog.Builder(this@ActivitySignup, DialogTypes.TYPE_SUCCESS)
                                            .setTitle("Congratulations")
                                            .setDescription(response.body()?.message.toString())
                                            .setPositiveText("OK")
                                            .setPositiveButtonColor(ContextCompat.getColor(this@ActivitySignup, R.color.blue))
                                            .setPositiveTextColor(ContextCompat.getColor(this@ActivitySignup, R.color.white))
                                            // Error View
                                            .setPositiveListener(object: ClickListener {
                                                override fun onClick(dialog: LottieAlertDialog) {
                                                    // This is the usage same instance of view
                                                    dialog.dismiss()
                                                }
                                            })
                                            .build()
                                    successDlg.show()
                                } catch (e: Exception) {
                                }
                            } else {
                                alertDialog!!.dismiss()
                                try {
                                    var msg= response.body()?.message
                                    var  successDlg : LottieAlertDialog =
                                        LottieAlertDialog.Builder(this@ActivitySignup, DialogTypes.TYPE_WARNING)
                                            .setTitle("Registration")
                                            .setDescription(msg.toString())
                                            .setPositiveText("OK")
                                            .setPositiveButtonColor(Color.parseColor("#f44242"))
                                            .setPositiveTextColor(Color.parseColor("#ffeaea"))
                                            // Error View
                                            .setPositiveListener(object: ClickListener {
                                                override fun onClick(dialog: LottieAlertDialog) {
                                                    // This is the usage same instance of view
                                                    dialog.dismiss()
                                                }
                                            })
                                            .build()
                                    successDlg.show()
                                    Log.e("failed","ress"+ response.body()?.message.toString())

                                } catch (e: Exception) {
                                }
                            }
                        } catch (e: Exception) {
                            alertDialog!!.dismiss()
                            try {
                                Utility.warningSnackBar(binding.root, e.message.toString(),
                                    this@ActivitySignup)
                            } catch (e: Exception) {
                                Toast.makeText(this@ActivitySignup, e.toString(), Toast.LENGTH_LONG)
                                    .show()
                            }

                        }
                    }

                    override fun onFailure(call: Call<MessageClass>, t: Throwable) {
                        alertDialog!!.dismiss()
                        try {
                            var message = ""
                            if (t is NoConnectivityException) {
                                message = "No Internet connection!"
                            } else {
                                message = t.message.toString()
                            }
                            System.out.println("Failure...!" + t.stackTrace);
                            Log.e("error",t.message.toString())
                            var  successDlg : LottieAlertDialog =
                                LottieAlertDialog.Builder(this@ActivitySignup, DialogTypes.TYPE_ERROR)
                                .setTitle("Error")
                                .setDescription(message)
                                .setNegativeText("OK")
                                .setNegativeListener(object: ClickListener {
                                    override fun onClick(dialog: LottieAlertDialog) {
                                        // This is the usage same instance of view
                                        dialog.dismiss()
                                    }
                                })
                                .build()
                            successDlg.show()

                        } catch (e: Exception) {
                            Toast.makeText(this@ActivitySignup, e.toString(), Toast.LENGTH_LONG).show()
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
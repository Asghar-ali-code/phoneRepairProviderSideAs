package com.funtash.mobileprovider.livedata.View.Activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.funtash.mobileprovider.Api.RetrofitClient
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.Utils.NoConnectivityException
import com.funtash.mobileprovider.Utils.Utility
import com.funtash.mobileprovider.Utils.Utility.alertDialog
import com.funtash.mobileprovider.databinding.ActivityLoginBinding
import com.funtash.mobileprovider.response.LoginClass
import com.funtash.mobileprovider.response.MessageClass
import com.funtash.mobilerepairinguserapp.Api.ApiInterface
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivityLogin : AppCompatActivity() {

    private var toggle_status: Boolean = false
    private var alertDialog : LottieAlertDialog?=null
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        Prefs.initPrefs(this)
        FirebaseApp.initializeApp(this);

        initPrgDlg()
        handleClick()
    }

    private fun initPrgDlg() {
        alertDialog  = LottieAlertDialog.Builder(this, DialogTypes.TYPE_LOADING)
            .setTitle("Logging In")
            .setDescription("Please wait while we are logging you in.")
            .build()
        alertDialog!!.setCancelable(false)

    }

    private fun handleClick(){

        binding.register.setOnClickListener {
            startActivity(Intent(this, ActivitySignup::class.java))
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            finish()
        }

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
        //on click login button
        binding.btnlogin.setOnClickListener {
            login()
        }

        binding.tvforget.setOnClickListener {
            val email=binding.edtemail.text.toString()

            if(TextUtils.isEmpty(email)){
                YoYo.with(Techniques.Tada)
                    .duration(700)
                    .playOn(binding.edtemail)
                binding.edtemail.error = "Enter Email!"
                binding.edtemail.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(binding.edtemail.text.toString()).matches()){
                binding.edtemail.error = "Enter correct Email!"
                binding.edtemail.requestFocus()
                return@setOnClickListener
            }
            forgetPassword(email)

        }

    }

    private fun forgetPassword(email: String) {
        alertDialog!!.show()
        val apiService = RetrofitClient.getClientRetro(this).create(ApiInterface::class.java)
        val call = apiService?.verifyemaillink(email)
        call?.enqueue(object : Callback<MessageClass> {
            override fun onResponse(
                call: Call<MessageClass>,
                response: Response<MessageClass>
            ) {
                try {
                    System.out.println("response code" + response.code());
                    Log.e("response", "" + response.body())
                    if (response.body()?.success==true) {
                        try {
                            var  successDlg : LottieAlertDialog =
                                LottieAlertDialog.Builder(this@ActivityLogin, DialogTypes.TYPE_SUCCESS)
                                .setTitle("Forget Password")
                                .setDescription(response.body()?.message.toString())
                                .setPositiveText("OK")
                                // Error View
                                .setPositiveListener(object: ClickListener {
                                    override fun onClick(dialog: LottieAlertDialog) {
                                        // This is the usage same instance of view
                                        alertDialog!!.dismiss()
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
                            var  successDlg : LottieAlertDialog =
                                LottieAlertDialog.Builder(this@ActivityLogin, DialogTypes.TYPE_WARNING)
                                .setTitle("Forget Password")
                                .setDescription(response.body()?.message.toString())
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
                            Log.e("failed", response.errorBody()?.string().toString())

                        } catch (e: Exception) {
                        }
                    }
                } catch (e: Exception) {
                    alertDialog!!.dismiss()
                    try {
                        Utility.warningSnackBar(binding.root, e.message.toString(),
                            this@ActivityLogin)
                    } catch (e: Exception) {
                        Toast.makeText(this@ActivityLogin, e.toString(), Toast.LENGTH_LONG)
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
                    var  successDlg : LottieAlertDialog
                    successDlg = LottieAlertDialog.Builder(this@ActivityLogin, DialogTypes.TYPE_ERROR)
                        .setTitle("Error")
                        .setDescription(message)
                        .setPositiveText("OK")
                        .setPositiveButtonColor(ContextCompat.getColor(this@ActivityLogin, R.color.blue))
                        .setPositiveTextColor(ContextCompat.getColor(this@ActivityLogin, R.color.white))
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
                    Toast.makeText(this@ActivityLogin, e.toString(), Toast.LENGTH_LONG).show()
                }

            }
        })
    }

    private fun login() {
        //get email and password
        val email = binding.edtemail.text.toString()
        val password = binding.edtpass.text.toString()
        when {
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
            else -> {
                alertDialog!!.show()
                val firebaseToken = FirebaseInstanceId.getInstance().token
                Log.e("token",firebaseToken.toString())
                val apiService = RetrofitClient.getClientRetro(this).create(ApiInterface::class.java)
                val call = apiService?.loginUser(email,password,firebaseToken.toString())
                call?.enqueue(object : Callback<LoginClass> {
                    override fun onResponse(
                        call: Call<LoginClass>,
                        response: Response<LoginClass>
                    ) {
                        try {
                            Log.e("response", "" + response.body())
                            if (response.body()?.success==true) {
                                try {
                                    alertDialog!!.dismiss()
                                    Prefs.putString("api_token",response.body()?.data?.api_token.toString())
                                    Prefs.putString("name",response.body()?.data?.name.toString())
                                    Prefs.putString("email",response.body()?.data?.email.toString())
                                    Prefs.putString("phone",response.body()?.data?.phone_number.toString())
                                    Prefs.putString("profile_pic",response.body()?.data?.profile_pic.toString())
                                    startActivity(
                                            Intent(
                                                this@ActivityLogin,
                                                ActivityHome::class.java
                                            )
                                        )
                                        overridePendingTransition(
                                            R.anim.slide_out,
                                            R.anim.slide_in
                                        )
                                        finish()
                                } catch (e: Exception) {
                                }
                            } else {
                                alertDialog!!.dismiss()
                                try {
                                    var  successDlg : LottieAlertDialog
                                    successDlg =
                                        LottieAlertDialog.Builder(this@ActivityLogin, DialogTypes.TYPE_WARNING)
                                        .setTitle("Logging In")
                                        .setDescription(response.body()?.message.toString())
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
                                    Log.e("failed", response.errorBody()?.string().toString())

                                } catch (e: Exception) {
                                }
                            }
                        } catch (e: Exception) {
                            alertDialog!!.dismiss()
                            try {
                                Utility.warningSnackBar(binding.root, e.message.toString(),
                                    this@ActivityLogin)
                            } catch (e: Exception) {
                                Toast.makeText(this@ActivityLogin, e.toString(), Toast.LENGTH_LONG)
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
                            Log.e("error",t.message.toString())
                            var  successDlg : LottieAlertDialog
                            successDlg = LottieAlertDialog.Builder(this@ActivityLogin, DialogTypes.TYPE_ERROR)
                                .setTitle("Error")
                                .setDescription(message)
                                .setPositiveText("OK")
                                .setPositiveButtonColor(ContextCompat.getColor(this@ActivityLogin, R.color.blue))
                                .setPositiveTextColor(ContextCompat.getColor(this@ActivityLogin, R.color.white))
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
                            Toast.makeText(this@ActivityLogin, e.toString(), Toast.LENGTH_LONG).show()
                        }

                    }
                })
            }
        }

    }




}
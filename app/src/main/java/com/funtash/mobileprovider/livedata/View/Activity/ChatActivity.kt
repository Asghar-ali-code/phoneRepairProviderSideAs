package com.funtash.mobileprovider.livedata.View.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.funtash.mobileprovider.Api.RetrofitClient
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.Utils.CustomLoader
import com.funtash.mobileprovider.Utils.NoConnectivityException
import com.funtash.mobileprovider.Utils.Resource
import com.funtash.mobileprovider.Utils.Utility
import com.funtash.mobileprovider.databinding.ActivityChatBinding
import com.funtash.mobileprovider.livedata.View.Adapter.MessageAdapter
import com.funtash.mobileprovider.livedata.ViewModel.ViewModelLiveData
import com.funtash.mobileprovider.response.MessageClass
import com.funtash.mobilerepairinguserapp.Api.ApiClient
import com.funtash.mobilerepairinguserapp.Api.ApiHelper
import com.funtash.mobilerepairinguserapp.Api.ApiInterface
import com.funtash.mobilerepairinguserapp.livedata.ViewModel.ViewModelFactoryLiveData
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException

class ChatActivity : AppCompatActivity() {
    private var o_id:String?=null
    private var p_id:String?=null
    private var p_img:String?=null
    private var api_token:String?=null
    private val dlg= CustomLoader
    private lateinit var viewModelLiveData: ViewModelLiveData

    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initView()
        setupViewModel()
        handleClick()
        loadData()
    }

    private fun initView() {
        Prefs.initPrefs(this)
        val window: Window = this.window

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)


        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)

        api_token= Prefs.getString("api_token","")
        o_id=intent.getStringExtra("o_id")
        p_id=intent.getStringExtra("p_id")
        p_img=intent.getStringExtra("p_img")

        Glide.with(this)
            .load(p_img) //.placeholder(R.drawable.banner)
            .into(binding.userImg)

        dlg.CustomDlg(this,"Loading, please wait...")
        binding.rvChat.setHasFixedSize(true)
        binding.rvChat.layoutManager= LinearLayoutManager(this)
    }

    private fun handleClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnsent.setOnClickListener {
            if(binding.edtchat.text.toString().equals("")){
                YoYo.with(Techniques.Tada)
                    .duration(700)
                    .playOn(binding.edtchat)
                binding.edtchat.error = "Enter Message !"
                binding.edtchat.requestFocus()
                return@setOnClickListener
            }
            else {
                dlg.showDlg(this)
                sendMessageApi(binding.edtchat.text.toString())
                binding.edtchat.setText("")
            }
        }
    }

    private fun sendMessageApi(msg: String) {
        val apiService = RetrofitClient.getClientRetro(this).create(ApiInterface::class.java)
        val call = apiService?.post_chat(api_token.toString(),p_id.toString(),msg.toString(),o_id.toString())
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
                            loadData()
                            binding.edtchat.setText("")
                        } catch (e: Exception) {
                            dlg.hideDlg(this@ChatActivity)
                        }
                    } else {
                        Utility.warningSnackBar(binding.root, response.body()!!.message.toString(),
                            this@ChatActivity)
                        dlg.hideDlg(this@ChatActivity)
                    }
                } catch (e: Exception) {
                    dlg.hideDlg(this@ChatActivity)
                    try {
                        Utility.warningSnackBar(binding.root, e.message.toString(),
                            this@ChatActivity)
                    } catch (e: Exception) {
                        Toast.makeText(this@ChatActivity, e.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }

            override fun onFailure(call: Call<MessageClass>, t: Throwable) {
                try {
                    dlg.hideDlg(this@ChatActivity)
                    var message = ""
                    if (t is NoConnectivityException) {
                        message = "No Internet connection!"
                    } else {
                        message = t.message.toString()
                    }
                    System.out.println("Failure...!" + t.stackTrace);
                    Log.e("error",t.message.toString())
                    Utility.warningSnackBar(binding.root, message.toString(),
                        this@ChatActivity)

                } catch (e: Exception) {
                    Toast.makeText(this@ChatActivity, e.toString(), Toast.LENGTH_LONG).show()
                }

            }
        })
    }

    private fun setupViewModel() {
        viewModelLiveData = ViewModelProviders.of(
            this,
            ViewModelFactoryLiveData(ApiHelper(ApiClient.getApi(this)!!))
        ).get(ViewModelLiveData::class.java)
    }


    private fun loadData() {
        viewModelLiveData.chatlist(api_token.toString(),o_id.toString())
        viewModelLiveData.chatlist.observe(this, Observer {

            when (it.status) {

                Resource.Status.SUCCESS -> {
                    if (it.data?.success == true) {
                        try {
                            val adapter= MessageAdapter(this,it.data)
                            binding.rvChat.adapter=adapter
                            if(it.data.data.size >0)
                                binding.rvChat.smoothScrollToPosition(it.data.data.size -1);
                            adapter.notifyDataSetChanged()
                            dlg.hideDlg(this)
                        } catch (e: UnsupportedEncodingException) {
                            e.printStackTrace()
                            dlg.hideDlg(this)
                        }
                    } else if (it.data?.success == false) {
                        dlg.hideDlg(this)
                    }
                }
                Resource.Status.ERROR -> {
                    Log.e("error", it.message.toString())
                    Utility.displaySnackBar(
                        binding.root,
                        it.message ?: "",
                        applicationContext
                    )
                    dlg.hideDlg(this)
                }

                Resource.Status.LOADING -> {
                }

                Resource.Status.FAILURE -> {
                    Utility.displaySnackBar(
                        binding.root,
                        it.message ?: "",
                        applicationContext
                    )
                    dlg.hideDlg(this)
                }
            }

        })
    }
}
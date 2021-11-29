package com.funtash.mobileprovider.livedata.View.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.funtash.mobileprovider.Api.RetrofitClient
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.Utils.*
import com.funtash.mobileprovider.databinding.ActivityNotifactionBinding
import com.funtash.mobileprovider.livedata.View.Adapter.NotificationAdapter
import com.funtash.mobileprovider.livedata.View.Adapter.OrderAdapter
import com.funtash.mobileprovider.livedata.View.Frag.HomeFragment
import com.funtash.mobileprovider.livedata.ViewModel.ViewModelLiveData
import com.funtash.mobileprovider.response.MessageClass
import com.funtash.mobileprovider.response.NotificationClass
import com.funtash.mobilerepairinguserapp.Api.ApiClient
import com.funtash.mobilerepairinguserapp.Api.ApiHelper
import com.funtash.mobilerepairinguserapp.Api.ApiInterface
import com.funtash.mobilerepairinguserapp.livedata.ViewModel.ViewModelFactoryLiveData
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActivityNotifaction : AppCompatActivity() {

    private var api_token: String? = null
    private var list:NotificationClass?=null
    private lateinit var viewModelLiveData: ViewModelLiveData
    private var alertDialog: LottieAlertDialog? = null
    private val dlg= CustomLoader
    private  lateinit var binding : ActivityNotifactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNotifactionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        UI()
        setupViewModel()
        loadnoti()
        swipeHelper()
    }




    private fun UI() {
        Prefs.initPrefs(this)
        api_token = Prefs.getString("api_token", "")
        Log.e("api_token", Prefs.getString("api_token", ""))

        dlg.CustomDlg(this,"Loading, please wait...")

        binding.rvnoti.setHasFixedSize(true)
        binding.rvnoti.layoutManager=LinearLayoutManager(this)

        alertDialog  = LottieAlertDialog.Builder(this, DialogTypes.TYPE_LOADING)
            .setTitle("Loading")
            .setDescription("Please wait a moment!")
            .build()
        alertDialog!!.setCancelable(false)

        binding.ivback.setOnClickListener {
            finish()
        }
    }

    private fun setupViewModel() {

        viewModelLiveData = ViewModelProviders.of(
            this,
            ViewModelFactoryLiveData(ApiHelper(ApiClient.getApi(this)!!))
        ).get(ViewModelLiveData::class.java)

    }

    private fun loadnoti() {
        viewModelLiveData.getnoti(api_token.toString())
        viewModelLiveData.noti.observe(this, Observer {

            when (it.status) {

                Resource.Status.SUCCESS -> {
                    if (it.data?.success == true) {
                        try {
                            Collections.reverse(it.data.data)
                            list=it.data
                              val adapter = NotificationAdapter(this, it.data)
                              binding.rvnoti.adapter = adapter
                            Log.e("size", ":" + it.data.data.size)
                            dlg.hideDlg(this)
                        } catch (e: Exception) {
                            dlg.hideDlg(this)
                        }
                        //data class LoginResponse(
                        // Log.e("error",it.data.message.toString())
                    } else if (it.data?.success == false) {
                        Utility.displaySnackBar(
                            binding.root, it.data.message,
                            this
                        )
                        dlg.hideDlg(this)
                    }
                }
                Resource.Status.ERROR -> {
                    Log.e("error", it.message.toString())
                    Utility.displaySnackBar(
                        binding.root,
                        it.message ?: "",
                        this
                    )
                    dlg.hideDlg(this)
                }

                Resource.Status.LOADING -> {
                }

                Resource.Status.FAILURE -> {
                    alertDialog!!.dismiss()
                    Utility.displaySnackBar(
                        binding.root,
                        it.message ?: "",
                        this
                    )
                    dlg.hideDlg(this)
                }
            }

        })

    }

    private fun swipeHelper() {
        val swipeHelper: SwipeHelper = object : SwipeHelper(this, binding.rvnoti) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder,
                underlayButtons: MutableList<UnderlayButton>,
                pos:Int
            ) {
                underlayButtons.add(UnderlayButton(
                    "Delete",
                    Color.parseColor("#C1272D")) {
                    Log.e("list:id", list?.data?.get(it)?.id.toString())
                    deletenotification(list?.data?.get(it)?.id.toString())
                   /* val alert: AlertDialog.Builder = AlertDialog.Builder(this@ActivityNotifaction)
                    alert.setTitle("Delete")
                    alert.setMessage("Are you sure you want to delete this TimeSlot?")
                        .setCancelable(true)
                        .setPositiveButton("Yes",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.dismiss()
                                deletenotification(list?.data?.get(it)?.id.toString())
                            })
                        .setNegativeButton("No", null)
                    alert.show()*/

                })
            }

        }
    }

    private fun deletenotification(nid: String) {
            alertDialog!!.show()
            val apiService = RetrofitClient.getClientRetro(this).create(ApiInterface::class.java)
            val call = apiService?.delete_notifications(api_token.toString(),nid)
            call?.enqueue(object : Callback<MessageClass> {
                override fun onResponse(
                    call: Call<MessageClass>,
                    response: Response<MessageClass>
                ) {
                    try {
                        Log.e("response", "" + response.body())
                        if (response.body()?.success==true) {
                            try {
                                loadnoti()
                                alertDialog!!.dismiss()
                                /*var  successDlg : LottieAlertDialog =
                                    LottieAlertDialog.Builder(this@ActivityNotifaction, DialogTypes.TYPE_SUCCESS)
                                        .setTitle("Notification")
                                        .setDescription(response.body()?.message.toString())
                                        .setPositiveText("OK")
                                        .setPositiveButtonColor(ContextCompat.getColor(this@ActivityNotifaction, R.color.blue))
                                        .setPositiveTextColor(ContextCompat.getColor(this@ActivityNotifaction, R.color.white))
                                        // Error View
                                        .setPositiveListener(object: ClickListener {
                                            override fun onClick(dialog: LottieAlertDialog) {
                                                // This is the usage same instance of view
                                                dialog.dismiss()
                                                loadnoti()
                                            }
                                        })
                                        .build()
                                successDlg.show()*/
                            } catch (e: Exception) {
                            }
                        } else {
                            alertDialog!!.dismiss()
                            try {
                                var  successDlg : LottieAlertDialog =
                                    LottieAlertDialog.Builder(this@ActivityNotifaction, DialogTypes.TYPE_WARNING)
                                        .setTitle("Failed!")
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
                                this@ActivityNotifaction!!
                            )
                        } catch (e: Exception) {
                            Toast.makeText(this@ActivityNotifaction, e.toString(), Toast.LENGTH_LONG)
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
                        var  successDlg : LottieAlertDialog = LottieAlertDialog.Builder(this@ActivityNotifaction, DialogTypes.TYPE_ERROR)
                            .setTitle("Error")
                            .setDescription(message)
                            .setPositiveText("OK")
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
                        Toast.makeText(this@ActivityNotifaction, e.toString(), Toast.LENGTH_LONG).show()
                    }

                }
            })
    }
}
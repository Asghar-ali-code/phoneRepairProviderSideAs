package com.funtash.mobileprovider.livedata.View.Frag

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.funtash.mobileprovider.Api.RetrofitClient
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.Utils.*
import com.funtash.mobileprovider.databinding.ReschuldeFragBinding
import com.funtash.mobileprovider.livedata.View.Activity.ActivityLogin
import com.funtash.mobileprovider.livedata.View.Adapter.ScheduleAdapter
import com.funtash.mobileprovider.livedata.View.Adapter.TimeAdapter
import com.funtash.mobileprovider.livedata.ViewModel.ViewModelLiveData
import com.funtash.mobileprovider.response.MessageClass
import com.funtash.mobileprovider.response.ScheduleList
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
import kotlin.collections.ArrayList

class RescheduleFragment : Fragment() {
    var ctx: Context? = null
    private var api_token: String? = null
    private var o_id: String? = null
    private var alertDialog: LottieAlertDialog? = null
    private var list:ScheduleList?=null
    private val dlg= CustomLoader
    private lateinit var viewModelLiveData: ViewModelLiveData
    private  lateinit var binding :ReschuldeFragBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx=context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding= ReschuldeFragBinding.inflate(inflater,container,false)

        UI()
        clicks()
        setupViewModel()
        loadschedules()
        swipeHelper()

       return binding.root
    }



    private fun UI() {
        Prefs.initPrefs(ctx)
        api_token = Prefs.getString("api_token", "")
        o_id = Prefs.getString("o_id", "")

        alertDialog  = LottieAlertDialog.Builder(context, DialogTypes.TYPE_LOADING)
            .setTitle("Loading")
            .setDescription("Please wait a moment!")
            .build()
        alertDialog!!.setCancelable(false)

        dlg.CustomDlg(ctx!!,"Loading, please wait...")


        binding.rvSlots.setHasFixedSize(true)
        binding.rvSlots.layoutManager= LinearLayoutManager(ctx)

        binding.rvTimes.setHasFixedSize(true)
        binding.rvTimes.layoutManager= LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false)

    }

    private fun clicks() {
        binding.cvAdd.setOnClickListener {
            try {
                val Rdialog = Dialog(ctx!!)
                Rdialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                Rdialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                Rdialog.setContentView(R.layout.availibilty_dialog)

                Rdialog.setCancelable(true)
                val tvtime=Rdialog.findViewById<TextView>(R.id.tvtime)
                val sp_day=Rdialog.findViewById<Spinner>(R.id.sp_day)
                val btnadd = Rdialog.findViewById<Button>(R.id.btndone)

                val arrayDay=ArrayList<String>()
                arrayDay.add("Select Day")
                arrayDay.add("Monday")
                arrayDay.add("Tuesday")
                arrayDay.add("Wednesday")
                arrayDay.add("Thursday")
                arrayDay.add("Friday")
                arrayDay.add("Saturday")
                arrayDay.add("Sunday")

                val adapter=ArrayAdapter<String>(ctx!!,R.layout.support_simple_spinner_dropdown_item,arrayDay)
                sp_day.adapter=adapter

                var day=""
                var time=""
                tvtime.setOnClickListener {
                    val cal = Calendar.getInstance()
                    val hour = cal[Calendar.HOUR_OF_DAY]
                    val min = cal[Calendar.MINUTE]

                    val sTimePickerDialog = TimePickerDialog(ctx,
                        { view, hourOfDay, minute ->
                            var min=minute.toString()
                            if(minute<9)
                                min= "0$minute"
                            time="$hourOfDay:$min"
                            tvtime.text=time

                        }, hour, min, DateFormat.is24HourFormat(ctx))
                    sTimePickerDialog.show()
                }
                sp_day.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        var data=sp_day.selectedItem.toString()
                        if(!data.equals("Select Day")) {
                            day=data
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                }

                btnadd.setOnClickListener {
                    if(time.equals("")) {
                        Utility.displaySnackBar(
                            binding.root, "Add Time",
                            ctx!!
                        )
                        YoYo.with(Techniques.Tada)
                            .duration(700)
                            .playOn(tvtime)
                        return@setOnClickListener
                    }
                    if(day.equals("")){
                        Utility.displaySnackBar(
                            binding.root, "Select Day",
                            ctx!!
                        )
                        YoYo.with(Techniques.Tada)
                            .duration(700)
                            .playOn(sp_day)
                        return@setOnClickListener
                    }
                    Rdialog.dismiss()
                    addSchedule(tvtime.text.toString(),day.toString())
                }
                Rdialog.show()

            }catch (e:Exception){}
        }

    }

    private fun addSchedule(time: String, day: String) {

        alertDialog!!.show()
        val apiService = RetrofitClient.getClientRetro(ctx).create(ApiInterface::class.java)
        val call = apiService?.provider_sechedule(api_token.toString(),day,time)
        call?.enqueue(object : Callback<MessageClass> {
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
                                LottieAlertDialog.Builder(ctx, DialogTypes.TYPE_SUCCESS)
                                    .setTitle("Time Slot")
                                    .setDescription(response.body()?.message.toString())
                                    .setPositiveText("OK")
                                    .setPositiveButtonColor(ContextCompat.getColor(ctx!!, R.color.blue))
                                    .setPositiveTextColor(ContextCompat.getColor(ctx!!, R.color.white))
                                    // Error View
                                    .setPositiveListener(object: ClickListener {
                                        override fun onClick(dialog: LottieAlertDialog) {
                                            // This is the usage same instance of view
                                            dialog.dismiss()
                                            loadschedules()
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
                                LottieAlertDialog.Builder(ctx, DialogTypes.TYPE_WARNING)
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
                            ctx!!
                        )
                    } catch (e: Exception) {
                        Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG)
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
                    var  successDlg : LottieAlertDialog = LottieAlertDialog.Builder(ctx, DialogTypes.TYPE_ERROR)
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
                    Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG).show()
                }

            }
        })

    }

    private fun loadschedules() {
        viewModelLiveData.getschedule(api_token.toString())
        activity?.let {
            viewModelLiveData.schedule.observe(it, Observer {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        if (it.data?.success == true) {
                            ctx?.let { it1 ->
                                try {
                                    list=it.data
                                    val adapter = ScheduleAdapter(ctx, it.data)
                                    binding.rvSlots.adapter = adapter
                                    val adapter2=TimeAdapter(ctx,it.data)
                                    binding.rvTimes.adapter=adapter2
                                    dlg.hideDlg(ctx!!)
                                } catch (e: Exception) {
                                    dlg.hideDlg(ctx!!)
                                }
                            }
                            //data class LoginResponse(
                            // Log.e("error",it.data.message.toString())
                        } else if (it.data?.success == false) {
                            ctx?.let { it1 ->
                                Utility.displaySnackBar(
                                    binding.root, it.data.message,
                                    it1
                                )
                            }
                            dlg.hideDlg(ctx!!)
                        }
                    }
                    Resource.Status.ERROR -> {
                        Log.e("error", it.message.toString())
                        /*ctx?.let { it1 ->
                            Utility.displaySnackBar(
                                binding.root,
                                it.message ?: "",
                                it1
                            )
                        }*/
                        dlg.hideDlg(ctx!!)

                    }

                    Resource.Status.LOADING -> {
                    }

                    Resource.Status.FAILURE -> {
                        ctx?.let { it1 ->
                            Utility.displaySnackBar(
                                binding.root,
                                it.message ?: "",
                                it1
                            )
                        }
                        dlg.hideDlg(ctx!!)
                    }
                }

            })
        }
    }


    private fun setupViewModel() {

        viewModelLiveData = ViewModelProviders.of(
            this,
            ViewModelFactoryLiveData(ApiHelper(ApiClient.getApi(requireActivity())!!))
        ).get(ViewModelLiveData::class.java)

    }

    private fun swipeHelper() {
        val swipeHelper: SwipeHelper = object : SwipeHelper(ctx, binding.rvSlots) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder,
                underlayButtons: MutableList<UnderlayButton>,
                pos:Int
            ) {
                    underlayButtons.add(UnderlayButton(
                        "Delete",
                        Color.parseColor("#C1272D")) {
                        Log.e("list:id", list?.data?.get(it)?.id.toString())
                        val alert: AlertDialog.Builder = AlertDialog.Builder(ctx)
                        alert.setTitle("Delete")
                        alert.setMessage("Are you sure you want to delete this TimeSlot?")
                            .setCancelable(true)
                            .setPositiveButton("Yes",
                                DialogInterface.OnClickListener { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                   deleteSchedule(list?.data?.get(it)?.id.toString())
                                })
                            .setNegativeButton("No", null)
                        alert.show()

                    })
            }

        }
    }

    private fun deleteSchedule(sid: String) {
        alertDialog!!.show()
        val apiService = RetrofitClient.getClientRetro(ctx).create(ApiInterface::class.java)
        val call = apiService?.delete_sechedule(api_token.toString(),sid)
        call?.enqueue(object : Callback<MessageClass> {
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
                                LottieAlertDialog.Builder(ctx, DialogTypes.TYPE_SUCCESS)
                                    .setTitle("Time Slot")
                                    .setDescription(response.body()?.message.toString())
                                    .setPositiveText("OK")
                                    .setPositiveButtonColor(ContextCompat.getColor(ctx!!, R.color.blue))
                                    .setPositiveTextColor(ContextCompat.getColor(ctx!!, R.color.white))
                                    // Error View
                                    .setPositiveListener(object: ClickListener {
                                        override fun onClick(dialog: LottieAlertDialog) {
                                            // This is the usage same instance of view
                                            dialog.dismiss()
                                            loadschedules()
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
                                LottieAlertDialog.Builder(ctx, DialogTypes.TYPE_WARNING)
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
                            ctx!!
                        )
                    } catch (e: Exception) {
                        Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG)
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
                    var  successDlg : LottieAlertDialog = LottieAlertDialog.Builder(ctx, DialogTypes.TYPE_ERROR)
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
                    Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG).show()
                }

            }
        })


    }

}
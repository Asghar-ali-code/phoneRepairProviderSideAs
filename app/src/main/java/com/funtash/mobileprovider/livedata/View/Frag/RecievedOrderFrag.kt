package com.funtash.mobileprovider.livedata.View.Frag

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.bumptech.glide.Glide
import com.funtash.mobileprovider.Api.RetrofitClient
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.Utils.CustomLoader
import com.funtash.mobileprovider.Utils.NoConnectivityException
import com.funtash.mobileprovider.Utils.Resource
import com.funtash.mobileprovider.Utils.Utility
import com.funtash.mobileprovider.databinding.RecievedorderFragBinding
import com.funtash.mobileprovider.livedata.View.Activity.ActivityOnMap
import com.funtash.mobileprovider.livedata.View.Adapter.OrderAdapter
import com.funtash.mobileprovider.livedata.ViewModel.ViewModelLiveData
import com.funtash.mobileprovider.response.MessageClass
import com.funtash.mobileprovider.response.OrderData
import com.funtash.mobilerepairinguserapp.Api.ApiClient
import com.funtash.mobilerepairinguserapp.Api.ApiHelper
import com.funtash.mobilerepairinguserapp.Api.ApiInterface
import com.funtash.mobilerepairinguserapp.livedata.ViewModel.ViewModelFactoryLiveData
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.pixplicity.easyprefs.library.Prefs
import de.hdodenhof.circleimageview.CircleImageView
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class RecievedOrderFrag : Fragment() {


    var ctx: Context? = null
    private var api_token: String? = null
    private var status: String? = "current"
    private var view: String? = "1"
    private lateinit var viewModelLiveData: ViewModelLiveData
    private var alertDialog: LottieAlertDialog? = null
    private val dlg=CustomLoader
    private lateinit var binding: RecievedorderFragBinding


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        setupViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadOrders()
        //dlg.hideDlg(ctx!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RecievedorderFragBinding.inflate(inflater,container,false)



        UI()
        return binding.root
    }

    private fun UI() {

        Prefs.initPrefs(ctx)
        api_token = Prefs.getString("api_token", "")
        Log.e("api_token", Prefs.getString("api_token", ""))
        binding.rvOrder.setHasFixedSize(true)
        binding.rvOrder.layoutManager = LinearLayoutManager(ctx)

        alertDialog  = LottieAlertDialog.Builder(ctx, DialogTypes.TYPE_LOADING)
            .setTitle("Loading")
            .setDescription("Please wait a moment!")
            .build()
        alertDialog!!.setCancelable(false)



        dlg.CustomDlg(ctx!!,"Loading, please wait...")

    }

    private fun loadOrders() {
        viewModelLiveData.getorder(api_token.toString(), status.toString())
        activity?.let {
            viewModelLiveData.order.observe(it, Observer {

                when (it.status) {

                    Resource.Status.SUCCESS -> {
                        if (it.data?.success == true) {
                            ctx?.let { it1 ->
                                try {
                                    Collections.sort(it.data.data)
                                    { lhs, rhs -> lhs.times.compareTo(rhs.times) }

                                    val adapter = OrderAdapter(RecievedOrderFrag(), ctx, it.data, status)
                                    binding.rvOrder.adapter = adapter
                                    Log.e("size", ":" + it.data.data.size)
                                    dlg.hideDlg(ctx!!)
                                } catch (e: Exception) {
                                }
                            }
                            //data class LoginResponse(
                            // Log.e("error",it.data.message.toString())
                        } else if (it.data?.success == false) {
                            dlg.hideDlg(ctx!!)
                            ctx?.let { it1 ->
                                Utility.displaySnackBar(
                                    binding.rvOrder, it.data.message,
                                    it1
                                )
                            }

                        }
                    }
                    Resource.Status.ERROR -> {
                        dlg.hideDlg(ctx!!)
                        Log.e("error", it.message.toString())
                       /* ctx?.let { it1 ->
                            Utility.displaySnackBar(
                                binding.rvOrder,
                                it.message ?: "",
                                it1
                            )
                        }*/

                    }

                    Resource.Status.LOADING -> {
                    }

                    Resource.Status.FAILURE -> {
                        dlg.hideDlg(ctx!!)
                        alertDialog!!.dismiss()
                        ctx?.let { it1 ->
                            Utility.displaySnackBar(
                                binding.rvOrder,
                                it.message ?: "",
                                it1
                            )
                        }

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

    fun orderDialog(context: Context, orderData: OrderData) {

        try {
            val Rdialog = Dialog(context)
            Rdialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            Rdialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            Rdialog?.setContentView(R.layout.acceptorder_dialog)

            Rdialog?.setCancelable(true)

            val name = Rdialog.findViewById<TextView>(R.id.tvname)
            val issue = Rdialog.findViewById<TextView>(R.id.tvissue1)
            val detail = Rdialog.findViewById<TextView>(R.id.tvdetails1)
            val address = Rdialog.findViewById<TextView>(R.id.tvaddress1)
            val price = Rdialog.findViewById<TextView>(R.id.tvprice1)
            val userimg = Rdialog.findViewById<CircleImageView>(R.id.iv_user)
            val spdate = Rdialog.findViewById<Spinner>(R.id.sp_date)

            val btnaccept = Rdialog.findViewById<Button>(R.id.btnaccept)
            val btnreject = Rdialog.findViewById<Button>(R.id.btnreject)

            var arraydate = ArrayList<String>()
            var date = ""
            var times = orderData.times.split(",")
            if (times.size == 1) {
                arraydate.add(orderData.times.toString())
            } else {
                arraydate = orderData.times.split(",") as ArrayList<String>
            }
            val adapter = ArrayAdapter<String>(context, R.layout.spinner_item, arraydate)
            spdate.adapter = adapter

            spdate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    date = spdate.selectedItem.toString()
                    Log.e("date",date)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            alertDialog = LottieAlertDialog.Builder(context, DialogTypes.TYPE_LOADING)
                .setTitle("Loading")
                .setDescription("Please wait a moment!")
                .build()
            alertDialog!!.setCancelable(false)



            name.text = orderData.user.name.toString()
            issue.text = orderData.service[0].name.en.toString()
            detail.text = Jsoup.parse(orderData.service[0].description.en.toString()).text()
            address.text = orderData.address.toString()
            if(orderData.payment==null){
                price.text = "Pending"
            }
            else
                price.text = orderData.service[0].discount_price.toString()+" ( "+orderData.payment.amount+" paid in andvance)"

            btnaccept.setOnClickListener {
                var  successDlg : LottieAlertDialog =
                    LottieAlertDialog.Builder(context, DialogTypes.TYPE_QUESTION)
                        .setTitle("Confirmation?")
                        .setDescription("Are you sure you want to accept this Order?")
                        .setPositiveText("Yes")
                        .setPositiveButtonColor(ContextCompat.getColor(context, R.color.blue))
                        .setPositiveTextColor(ContextCompat.getColor(context, R.color.white))
                        .setNegativeText("No")
                        // Error View
                        .setPositiveListener(object: ClickListener {
                            override fun onClick(dialog: LottieAlertDialog) {
                                // This is the usage same instance of view
                                dialog.dismiss()
                                Rdialog.dismiss()
                                orderstatusApi(orderData.id.toString(),"accepted",date.toString(),context)
                            }
                        })
                        .setNegativeListener(object :ClickListener{
                            override fun onClick(dialog: LottieAlertDialog) {
                                dialog.dismiss()
                            }
                        })
                        .build()
                successDlg.show()
            }
            btnreject.setOnClickListener {
                var  successDlg : LottieAlertDialog =
                    LottieAlertDialog.Builder(context, DialogTypes.TYPE_QUESTION)
                        .setTitle("Confirmation?")
                        .setDescription("Are you sure you want to reject this Order?")
                        .setPositiveText("Yes")
                        .setNegativeText("No")
                        .setPositiveButtonColor(ContextCompat.getColor(context, R.color.red))
                        .setPositiveTextColor(ContextCompat.getColor(context, R.color.white))
                        // Error View
                        .setPositiveListener(object: ClickListener {
                            override fun onClick(dialog: LottieAlertDialog) {
                                // This is the usage same instance of view
                                dialog.dismiss()
                                Rdialog.dismiss()
                                orderstatusApi(
                                    orderData.id.toString(),
                                    "cancelled",
                                    "",
                                    context
                                )
                                
                            }
                        })
                        .setNegativeListener(object :ClickListener{
                            override fun onClick(dialog: LottieAlertDialog) {
                                dialog.dismiss()
                            }
                        })

                        .build()
                successDlg.show()
            }
            if(orderData.user.profile_pic!=null){
                Glide.with(context)
                    .load(orderData.user.profile_pic) //.placeholder(R.drawable.banner)
                    .into(userimg)
            }

            Rdialog.show()
        }catch (e:Exception){
            Log.e("exp",e.message.toString())
        }

    }

    private fun orderstatusApi(oid: String, status: String, date: String, context: Context) {
        alertDialog?.show()
        val apiService = RetrofitClient.getClientRetro(context).create(ApiInterface::class.java)
        val call = apiService?.accept_reject(oid,status,date)
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
                            val successDlg : LottieAlertDialog =
                                LottieAlertDialog.Builder(context, DialogTypes.TYPE_SUCCESS)
                                .setTitle("Order")
                                .setDescription(response.body()?.message.toString())
                                .setPositiveText("OK")
                                .setPositiveButtonColor(ContextCompat.getColor(context, R.color.blue))
                                .setPositiveTextColor(ContextCompat.getColor(context, R.color.white))
                                // Error View
                                .setPositiveListener(object: ClickListener {
                                    override fun onClick(dialog: LottieAlertDialog) {
                                        // This is the usage same instance of view
                                        val intent= Intent(context,ActivityOnMap::class.java)
                                        intent.putExtra("o_id",oid)
                                        context.startActivity(intent)
                                        dialog.dismiss()
                                        alertDialog?.dismiss()
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
                                LottieAlertDialog.Builder(context, DialogTypes.TYPE_WARNING)
                                    .setTitle("Order")
                                    .setDescription(response.body()?.message.toString())
                                    .setPositiveText("OK")
                                    .setPositiveButtonColor(ContextCompat.getColor(context, R.color.blue))
                                    .setPositiveTextColor(ContextCompat.getColor(context, R.color.white))
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
                            context
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG)
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
                    var  successDlg : LottieAlertDialog = LottieAlertDialog.Builder(context, DialogTypes.TYPE_ERROR)
                        .setTitle("Error")
                        .setDescription(message)
                        .setPositiveText("OK")
                        .setPositiveButtonColor(ContextCompat.getColor(context, R.color.blue))
                        .setPositiveTextColor(ContextCompat.getColor(context, R.color.white))
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
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                }

            }
        })
    }

    private fun reloadFrag() {
        // Reload current fragment
       /* var frg: Fragment? = RecievedOrderFrag()
        frg = fragmentManager?.findFragmentById(R.id.ordercontainer)
        val ft: androidx.fragment.app.FragmentTransaction? = fragmentManager?.beginTransaction()
        ft?.detach(frg!!)
        ft?.attach(frg!!)
        ft?.commit()
        alertDialog?.dismiss()*/

        /*(parentFragment as HomeFragment).acceptedFrag()*/
    }

}



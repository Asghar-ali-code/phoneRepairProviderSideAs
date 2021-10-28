package com.funtash.mobileprovider.livedata.View.Frag

import android.app.Dialog
import android.app.FragmentTransaction
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.funtash.mobileprovider.Api.RetrofitClient
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.Utils.GpsTracker
import com.funtash.mobileprovider.Utils.NoConnectivityException
import com.funtash.mobileprovider.Utils.Resource
import com.funtash.mobileprovider.Utils.Utility
import com.funtash.mobileprovider.databinding.FragmentHomeBinding
import com.funtash.mobileprovider.databinding.RecievedorderFragBinding
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AcceptedOrderFrag : Fragment() {


    var ctx: Context? = null
    private var api_token: String? = null
    private var status: String? = "accepted"
    private var view: String? = "1"
    private lateinit var viewModelLiveData: ViewModelLiveData
    private var alertDialog: LottieAlertDialog? = null
    private lateinit var binding: RecievedorderFragBinding


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        setupViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RecievedorderFragBinding.inflate(inflater)



        UI()
        loadOrders()
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
                                    val adapter = OrderAdapter(AcceptedOrderFrag(), ctx, it.data, status)
                                    binding.rvOrder.adapter = adapter
                                    Log.e("size", ":" + it.data.data.size)
                                } catch (e: Exception) {
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

                        }
                    }
                    Resource.Status.ERROR -> {
                        Log.e("error", it.message.toString())
                        ctx?.let { it1 ->
                            Utility.displaySnackBar(
                                binding.rvOrder,
                                it.message ?: "",
                                it1
                            )
                        }

                    }

                    Resource.Status.LOADING -> {
                    }

                    Resource.Status.FAILURE -> {
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


}



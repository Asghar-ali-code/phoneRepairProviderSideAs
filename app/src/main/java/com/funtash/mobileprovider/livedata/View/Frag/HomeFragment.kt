package com.funtash.mobileprovider.livedata.View.Frag

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.funtash.mobileprovider.Api.RetrofitClient
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.Utils.GpsTracker
import com.funtash.mobileprovider.Utils.NoConnectivityException
import com.funtash.mobileprovider.Utils.Resource
import com.funtash.mobileprovider.Utils.Utility
import com.funtash.mobileprovider.databinding.FragmentHomeBinding
import com.funtash.mobileprovider.livedata.ViewModel.ViewModelLiveData
import com.funtash.mobileprovider.response.MessageClass
import com.funtash.mobilerepairinguserapp.Api.ApiClient
import com.funtash.mobilerepairinguserapp.Api.ApiHelper
import com.funtash.mobilerepairinguserapp.Api.ApiInterface
import com.funtash.mobilerepairinguserapp.livedata.ViewModel.ViewModelFactoryLiveData
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.pixplicity.easyprefs.library.Prefs
import pl.droidsonroids.gif.GifDrawable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class HomeFragment : Fragment() {


    var ctx: Context? = null
    private var api_token: String? = null
    private var status: String? = "current"
    private var view: String? = "1"
    private lateinit var viewModelLiveData: ViewModelLiveData
    private var alertDialog: LottieAlertDialog? = null
    private lateinit var binding: FragmentHomeBinding


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
        binding = FragmentHomeBinding.inflate(inflater)



        UI()
        click()
        loadEarning()
        //loadOrders()
        return binding.root
    }

    private fun UI() {
        Prefs.initPrefs(ctx)
        api_token = Prefs.getString("api_token", "")
        Log.e("api_token", Prefs.getString("api_token", ""))

        alertDialog  = LottieAlertDialog.Builder(ctx, DialogTypes.TYPE_LOADING)
            .setTitle("Loading")
            .setDescription("Please wait a moment!")
            .build()
        alertDialog!!.setCancelable(false)

        //resource (drawable or raw)
        //resource (drawable or raw)
        //val gifFromResource = GifDrawable(resources, R.drawable.app_logo2)

        try {
            // check if GPS enabled

            // check if GPS enabled
            val gpsTracker = GpsTracker(ctx)

            if (gpsTracker.isGPSTrackingEnabled) {
                gpsTracker.onLocationChanged(gpsTracker.location)
                val lat = gpsTracker.latitude.toString()
                val lng = gpsTracker.longitude.toString()
                val geocoder = Geocoder(ctx, Locale.getDefault())
                val addresses =
                    geocoder.getFromLocation(gpsTracker.latitude, gpsTracker.longitude, 1)
                if (addresses.isNotEmpty()) {
                    if (addresses.size > 0) {
                        val address = addresses[0].getAddressLine(0)
                        updatelatlng(lat, lng, address)
                        Log.e("update", ":$address")
                    }
                }

                Log.e("lat", lat + "" + lng)

            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                Log.e("lat", "null")
                gpsTracker.showSettingsAlert()
            }
        }catch (e:Exception){
            Log.e("exp",e.message.toString())
        }


            parentFragmentManager.beginTransaction().replace(
                R.id.ordercontainer,
                RecievedOrderFrag()
            ).commit()
    }

    private fun click() {
        var selectedFragment: Fragment? = null

        binding.cvRecieved.setOnClickListener {
            status = "current"
            selectedFragment=RecievedOrderFrag()
            selectedFragment?.let {
                parentFragmentManager.beginTransaction().replace(
                    R.id.ordercontainer,
                    it
                ).commit()
            }
            when {
                view.equals("3") -> {
                    binding.tvOngoing.setTextColor(Color.parseColor("#000000"))
                    binding.tvOngoing.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
                view.equals("2") -> {
                    binding.tvAccepted.setTextColor(Color.parseColor("#000000"))
                    binding.tvAccepted.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
                view.equals("4") -> {
                    binding.tvPhonecheck.setTextColor(Color.parseColor("#000000"))
                    binding.tvPhonecheck.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
            }
            binding.tvRecieved.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.tvRecieved.setBackgroundColor(Color.parseColor("#055C9D"))
            view = "1"
        }


        binding.cvAccepted.setOnClickListener {
            status = "accepted"
            selectedFragment=AcceptedOrderFrag()
            selectedFragment?.let {
                parentFragmentManager.beginTransaction().replace(
                    R.id.ordercontainer,
                    it
                ).commit()
            }
            when {
                view.equals("1") -> {
                    binding.tvRecieved.setTextColor(Color.parseColor("#000000"))
                    binding.tvRecieved.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
                view.equals("3") -> {
                    binding.tvOngoing.setTextColor(Color.parseColor("#000000"))
                    binding.tvOngoing.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
                view.equals("4") -> {
                    binding.tvPhonecheck.setTextColor(Color.parseColor("#000000"))
                    binding.tvPhonecheck.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
            }
            binding.tvAccepted.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.tvAccepted.setBackgroundColor(Color.parseColor("#055C9D"))
            view = "2"
        }


        binding.cvOngoing.setOnClickListener {
            status = "ongoing"
            selectedFragment=OnGoingOrderFrag()
            selectedFragment?.let {
                parentFragmentManager.beginTransaction().replace(
                    R.id.ordercontainer,
                    it
                ).commit()
            }
            when {
                view.equals("1") -> {
                    binding.tvRecieved.setTextColor(Color.parseColor("#000000"))
                    binding.tvRecieved.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
                view.equals("2") -> {
                    binding.tvAccepted.setTextColor(Color.parseColor("#000000"))
                    binding.tvAccepted.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
                view.equals("4") -> {
                    binding.tvPhonecheck.setTextColor(Color.parseColor("#000000"))
                    binding.tvPhonecheck.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
            }
            binding.tvOngoing.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.tvOngoing.setBackgroundColor(Color.parseColor("#055C9D"))
            view = "3"
        }

        binding.cvPhonecheck.setOnClickListener {
            status = "completed"
            selectedFragment=PhoneCheckOrderFrag()
            selectedFragment?.let {
                parentFragmentManager.beginTransaction().replace(
                    R.id.ordercontainer,
                    it
                ).commit()
            }
            when {
                view.equals("1") -> {
                    binding.tvRecieved.setTextColor(Color.parseColor("#000000"))
                    binding.tvRecieved.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
                view.equals("2") -> {
                    binding.tvAccepted.setTextColor(Color.parseColor("#000000"))
                    binding.tvAccepted.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
                view.equals("3") -> {
                    binding.tvOngoing.setTextColor(Color.parseColor("#000000"))
                    binding.tvOngoing.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
            }
            binding.tvPhonecheck.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.tvPhonecheck.setBackgroundColor(Color.parseColor("#055C9D"))
            view = "4"
        }

    }


    private fun loadEarning() {
        viewModelLiveData.getEarning(api_token.toString())
        activity?.let {
            viewModelLiveData.earning.observe(it, Observer {

                when (it.status) {

                    Resource.Status.SUCCESS -> {
                        if (it.data?.success == true) {
                            ctx?.let { it1 ->
                                try {
                                    binding.tvbooking.text = it.data.data.totalBookings.toString()
                                    binding.tvearning.text = it.data.data.totalEarnings.toString()
                                    binding.tvcustomer.text =
                                        it.data.data.totalCustomers.toString()

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
                                binding.root,
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
                                binding.root,
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




    private fun updatelatlng(lat: String, lng: String, address: String) {
        val apiService = RetrofitClient.getClientRetro(ctx).create(ApiInterface::class.java)
        val call = apiService?.updateAddress(api_token.toString(),lat,lng,address)
        call?.enqueue(object : Callback<MessageClass> {
            override fun onResponse(
                call: Call<MessageClass>,
                response: Response<MessageClass>
            ) {
                try {
                    if(response.body()?.success==true)
                        Log.e("res", response.body()!!.message.toString())
                }catch (e:Exception){}
            }
            override fun onFailure(call: Call<MessageClass>, t: Throwable) {
                try {
                    var message = ""
                    if (t is NoConnectivityException) {
                        message = "No Internet connection!"
                    } else {
                        message = t.message.toString()
                    }


                } catch (e: Exception) {
                }

            }
        })
    }
}



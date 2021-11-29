package com.funtash.mobileprovider.livedata.View.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.funtash.mobileprovider.Utils.Resource
import com.funtash.mobileprovider.Utils.Utility
import com.funtash.mobileprovider.databinding.ActivityServiceBinding
import com.funtash.mobileprovider.livedata.View.Adapter.ServicesAdapter
import com.funtash.mobileprovider.livedata.ViewModel.ViewModelLiveData
import com.funtash.mobilerepairinguserapp.Api.ApiClient
import com.funtash.mobilerepairinguserapp.Api.ApiHelper
import com.funtash.mobilerepairinguserapp.livedata.ViewModel.ViewModelFactoryLiveData
import com.google.firebase.FirebaseApp
import com.pixplicity.easyprefs.library.Prefs

class ServiceActivity : AppCompatActivity() {
    private var c_id:String?=null
    private var p_cid:String?=null
    private lateinit var viewModelLiveData: ViewModelLiveData
    private lateinit var binding: ActivityServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initView()
    }

    private fun initView() {

        binding.rvIssues.setHasFixedSize(true)
        binding.rvIssues.layoutManager= GridLayoutManager(this, 3)
        binding.rvIssues.showShimmerAdapter()

        FirebaseApp.initializeApp(this)

        c_id=intent.getStringExtra("c_id")
        var name=intent.getStringExtra("name")
        if(name!=null) {
            binding.tvDevice.text = "Select Issue With Your " + name
            //loadsubSubcategories()
        }

        setupViewModel()
        handleClick()
        laodServices()
    }


    private fun handleClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupViewModel() {
        viewModelLiveData = ViewModelProviders.of(
            this,
            ViewModelFactoryLiveData(ApiHelper(ApiClient.getApi(this)!!))
        ).get(ViewModelLiveData::class.java)
    }

    private fun laodServices() {
        viewModelLiveData.getservices(c_id.toString())
        viewModelLiveData.service.observe(this, Observer {

            when (it.status) {

                Resource.Status.SUCCESS -> {
                    if (it.data?.success == true) {
                        val adapter= ServicesAdapter(this,it.data)
                        if(it.data.data.size!=0) {
                            binding.rvIssues.adapter = adapter
                            binding.rvIssues.hideShimmerAdapter()
                            // binding.mainLayout.stopLoading()
                        }
                        else
                            Utility.displaySnackBar(
                                binding.root,
                                "No Record Found!",
                                applicationContext
                            )
                        // binding.mainLayout.stopLoading()
                    } else if (it.data?.success == false) {


                    }
                }
                Resource.Status.ERROR -> {
                    Log.e("error", it.message.toString())
                    Utility.displaySnackBar(
                        binding.root,
                        it.message ?: "",
                        applicationContext
                    )

                }

                Resource.Status.LOADING -> {
                }

                Resource.Status.FAILURE -> {
                    Utility.displaySnackBar(
                        binding.root,
                        it.message ?: "",
                        applicationContext
                    )

                }
            }

        })
    }
}
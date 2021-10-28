package com.funtash.mobileprovider.livedata.View.Frag

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.funtash.mobileprovider.Utils.Resource
import com.funtash.mobileprovider.Utils.Utility
import com.funtash.mobileprovider.databinding.ReviewsFragBinding
import com.funtash.mobileprovider.livedata.View.Adapter.NotificationAdapter
import com.funtash.mobileprovider.livedata.View.Adapter.ReviewAdapter
import com.funtash.mobileprovider.livedata.ViewModel.ViewModelLiveData
import com.funtash.mobilerepairinguserapp.Api.ApiClient
import com.funtash.mobilerepairinguserapp.Api.ApiHelper
import com.funtash.mobilerepairinguserapp.livedata.ViewModel.ViewModelFactoryLiveData
import com.pixplicity.easyprefs.library.Prefs

class ReviewFragment : Fragment() {
    var ctx: Context? = null
    private var api_token: String? = null
    private lateinit var viewModelLiveData: ViewModelLiveData

    private  lateinit var binding :ReviewsFragBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx=context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding= ReviewsFragBinding.inflate(inflater)

        UI()
        setupViewModel()
        loadreview()

        return binding.root
    }



    private fun UI() {
        Prefs.initPrefs(ctx)
        api_token = Prefs.getString("api_token", "")

        binding.rvRating.setHasFixedSize(true)
        binding.rvRating.layoutManager= LinearLayoutManager(ctx)
    }

    private fun loadreview() {
        viewModelLiveData.getreview(api_token.toString())
        activity?.let {
            viewModelLiveData.review.observe(it, Observer {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        if (it.data?.success == true) {
                            ctx?.let { it1 ->
                                try {
                                    binding.reviewScore.text=it.data.data.review_avg.toString()
                                    binding.ratingBar.rating=it.data.data.review_avg.toFloat()
                                    binding.tvReview.text="Total Reviews("+it.data.data.revie_list.size+")"

                                    val adapter = ReviewAdapter(ctx, it.data)
                                    binding.rvRating.adapter = adapter
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
}
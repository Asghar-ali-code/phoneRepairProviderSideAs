package com.funtash.mobileprovider.livedata.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funtash.mobileprovider.Utils.Resource
import com.funtash.mobileprovider.livedata.repo.RepoLiveData
import com.funtash.mobileprovider.response.*

import kotlinx.coroutines.launch


class ViewModelLiveData(private val loginRepoLiveData: RepoLiveData) : ViewModel() {

    //cached
    private val _earning = MutableLiveData<Resource<EarningClass>>()
    private val _order = MutableLiveData<Resource<OrderClass>>()
    private val _noti = MutableLiveData<Resource<NotificationClass>>()
    private val _review = MutableLiveData<Resource<ReviewClass>>()
    private val _schedule = MutableLiveData<Resource<ScheduleList>>()
    private val _bookingdetails = MutableLiveData<Resource<OrderDetail>>()


    //public
    val earning : LiveData<Resource<EarningClass>> get() =  _earning
    val order : LiveData<Resource<OrderClass>> get() =  _order
    val noti : LiveData<Resource<NotificationClass>> get() =  _noti
    val review : LiveData<Resource<ReviewClass>> get() =  _review
    val schedule : LiveData<Resource<ScheduleList>> get() =  _schedule
    val bookingdetails : LiveData<Resource<OrderDetail>> get() =  _bookingdetails




    //load earning
    fun getEarning(apitoken:String) =
        viewModelScope.launch {
            try {
                _earning.value = loginRepoLiveData.provider_totalearnings(apitoken)
            }
            catch (exception: Exception){

            }
        }

    //load orders
    fun getorder(apitoken:String,status:String) =
        viewModelScope.launch {
            try {
                _order.value = loginRepoLiveData.provider_orders(apitoken, status)
            }
            catch (exception: Exception){

            }
        }
    //load notifications
    fun getnoti(apitoken:String) =
        viewModelScope.launch {
            try {
                _noti.value = loginRepoLiveData.getnotification(apitoken)
            }
            catch (exception: Exception){

            }
        }

    //load reviewa
    fun getreview(apitoken:String) =
        viewModelScope.launch {
            try {
                _review.value = loginRepoLiveData.getreview(apitoken)
            }
            catch (exception: Exception){

            }
        }

    //load schedule
    fun getschedule(apitoken:String) =
        viewModelScope.launch {
            try {
                _schedule.value = loginRepoLiveData.getschedule(apitoken)
            }
            catch (exception: Exception){

            }
        }

    //load bookingdetails
    fun getbooking_details(api_token:String,url:String) =
        viewModelScope.launch {
            try {
                _bookingdetails.value = loginRepoLiveData.getbooking_details(api_token,url)
            }
            catch (exception: Exception){

            }
        }
}
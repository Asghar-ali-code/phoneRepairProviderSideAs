package com.funtash.mobilerepairinguserapp.livedata.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.funtash.mobileprovider.livedata.ViewModel.ViewModelLiveData
import com.funtash.mobilerepairinguserapp.Api.ApiHelper
import com.funtash.mobileprovider.livedata.repo.RepoLiveData


class ViewModelFactoryLiveData(private val apiHelper: ApiHelper): ViewModelProvider.Factory  {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelLiveData::class.java)) {
            return ViewModelLiveData(RepoLiveData(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}
package com.funtash.mobileprovider.Utils

import android.util.Log


data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

        enum class Status {
            SUCCESS,
            ERROR,
            LOADING,
            FAILURE
        }

        companion object {

            fun <T> success(data: T): Resource<T> {
                return Resource(Status.SUCCESS, data, null)
            }

            fun <T> error(message: String, data: T? = null): Resource<T> {
                if(message == "Network call has failed for a following reason: No Internet Connection")
                    return Resource(Status.ERROR, data, "No Internet Connection!!!")
                else
                    return Resource(Status.ERROR, data, message)
            }

            fun <T> loading(data: T?): Resource<T> {
                return Resource(Status.LOADING, data, null)
            }

            fun <T> failed(message: String, data: T? = null): Resource<T> {
                return Resource(Status.FAILURE, data, message)
            }

        }
}

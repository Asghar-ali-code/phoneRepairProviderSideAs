package com.funtash.mobileprovider.Utils.Route


interface DirectionFinderListener {
    fun onDirectionFinderStart()
    fun onDirectionFinderSuccess(route: ArrayList<Route>?)
}
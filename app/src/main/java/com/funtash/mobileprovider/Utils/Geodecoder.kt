package com.funtash.mobileprovider.Utils


import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.util.ArrayList

class Geodecoder {
    companion object {
        fun FromAddress(context: Context, addressStr: String): LatLng? {
            val coder = Geocoder(context)
            var address: ArrayList<Address>? = ArrayList()
            var location: Address? = null
            var latLng: LatLng? = null
            if (Geocoder.isPresent()) {
                try {
                    address = coder.getFromLocationName(addressStr, 1) as ArrayList<Address>
                    if (address.size > 0) {
                        location = address[0]
                        latLng = LatLng(location.getLatitude(), location.getLongitude())
                        Log.i("latLng",""+latLng)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            return latLng
        }

        fun FromLocation(mContext: Context, lat: Double, lng: Double): String {
            var address = "Start"
            var locality = "Start"
            var adminarea = "Start"
            var country = "Location"
            var featureName = "Location"
            val geocoder = Geocoder(mContext)
            val addresses: ArrayList<Address>
            if (Geocoder.isPresent()) {
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1) as ArrayList<Address>
                    if (addresses.size > 0) {
                        address = addresses[0].getAddressLine(0)
                        locality = addresses[0].locality
                        adminarea = addresses[0].adminArea
                        country = addresses[0].countryName
                        featureName = addresses[0].featureName
                        Log.i("locality",""+addresses[0].locality)
                        Log.i("adminArea",""+addresses[0].adminArea)
                        Log.i("subAdminArea",""+addresses[0].subAdminArea)
                        Log.i("subLocality",""+addresses[0].subLocality)


                    }
                } catch (e: Exception) {
                }
            }
            return address+","+locality+","+adminarea+","+country+","+featureName
        }
    }
}
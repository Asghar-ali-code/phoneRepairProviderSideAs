package com.funtash.mobileprovider.livedata.View.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.Utils.Resource
import com.funtash.mobileprovider.Utils.Route.DirectionFinder
import com.funtash.mobileprovider.Utils.Route.DirectionFinderListener
import com.funtash.mobileprovider.Utils.Route.Route
import com.funtash.mobileprovider.Utils.Utility
import com.funtash.mobileprovider.databinding.ActivityTrackOrderBinding
import com.funtash.mobileprovider.livedata.ViewModel.ViewModelLiveData
import com.funtash.mobilerepairinguserapp.Api.ApiClient
import com.funtash.mobilerepairinguserapp.Api.ApiHelper
import com.funtash.mobilerepairinguserapp.livedata.ViewModel.ViewModelFactoryLiveData
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.pixplicity.easyprefs.library.Prefs
import java.io.UnsupportedEncodingException

class TrackOrderActivity : AppCompatActivity() , OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    DirectionFinderListener {

    private var o_id:String?=null
    private var api_token:String?=null
    private val REQUEST_PERMISSION_LOCATION = 991
    private var gMap: GoogleMap? = null
    private var googleApiClient: GoogleApiClient? = null
    private var lastKnownLocation: Location? = null
    private var originMarkers: List<Marker> = ArrayList()
    private var destinationMarkers: List<Marker> = ArrayList()
    private var polylinePaths: List<Polyline> = ArrayList()

    private lateinit var viewModelLiveData: ViewModelLiveData
    private lateinit var binding: ActivityTrackOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initView()
        setupViewModel()
        handleClick()
        setupGoogleApiClient()
        loadData()
    }
        private fun initView() {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.google_map) as SupportMapFragment?
            mapFragment!!.getMapAsync(this)

            api_token = Prefs.getString("api_token", "")
            o_id = intent.getStringExtra("o_id")
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

        private fun loadData() {
            viewModelLiveData.getbooking_details(api_token.toString(), o_id.toString())
            viewModelLiveData.bookingdetails.observe(this, Observer {

                when (it.status) {

                    Resource.Status.SUCCESS -> {
                        if (it.data?.success == true) {
                            try {
                                DirectionFinder(
                                    this, it.data.data.address.toString(),
                                    it.data.data.provider.address.toString()
                                ).execute()

                                binding.tvtitle.text = it.data.data.booking_status.toString()

                            } catch (e: UnsupportedEncodingException) {
                                e.printStackTrace()
                            }
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

        override fun onStart() {
            super.onStart()
            googleApiClient!!.connect()
        }

        override fun onStop() {
            super.onStop()
            googleApiClient!!.disconnect()
        }

        private fun setupGoogleApiClient() {
            if (googleApiClient == null) {
                googleApiClient = GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build()
            }
        }

        private fun updateLastLocation() {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSION_LOCATION
                    )
                    return
                }
                lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient
                )

                gMap?.isMyLocationEnabled = true
                if (lastKnownLocation != null) {
                    gMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude
                            ), 15f
                        )
                    )
                    gMap?.animateCamera(CameraUpdateFactory.zoomTo(15f))
                    Log.e("err", "3")
                }
            } catch (e: Exception) {
                Log.e("googleexp", e.message.toString())
            }
        }


        override fun onMapReady(googleMap: GoogleMap) {
            gMap = googleMap
            gMap!!.uiSettings.isMyLocationButtonEnabled = true
            //updateLastLocation()

        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == REQUEST_PERMISSION_LOCATION) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLastLocation()
                } else {
                    // TODO: 24/10/2021 Tell user to use GPS
                }
            }
        }

        override fun onConnected(bundle: Bundle?) {
            updateLastLocation()
        }

        override fun onConnectionSuspended(i: Int) {}

        override fun onConnectionFailed(connectionResult: ConnectionResult) {}

        override fun onDirectionFinderStart() {
            if (originMarkers != null) {
                for (marker in originMarkers) {
                    marker.remove()
                }
            }
            if (destinationMarkers != null) {
                for (marker in destinationMarkers) {
                    marker.remove()
                }
            }
            if (polylinePaths != null) {
                for (polyline in polylinePaths) {
                    polyline.remove()
                }
            }
        }


        override fun onDirectionFinderSuccess(routes: ArrayList<Route>?) {
            polylinePaths = ArrayList()
            originMarkers = ArrayList()
            destinationMarkers = ArrayList()
            for (route in routes!!) {
                gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 15f))
                /*(findViewById<View>(R.id.tvDuration) as TextView).setText(route.duration?.text)
                (findViewById<View>(R.id.tvDistance) as TextView).setText(route.distance.text)*/
                (originMarkers as ArrayList<Marker>).add(
                    gMap?.addMarker(
                        MarkerOptions()
                              .icon(BitmapDescriptorFactory.fromBitmap(changeMakerSize(R.drawable.user_icon,70,70)))
                            .title(route.startAddress)
                            .position(route.startLocation!!)
                    )!!
                )
                (destinationMarkers as ArrayList<Marker>).add(
                    gMap?.addMarker(
                        MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(changeMakerSize(R.drawable.biker_icon,70,70)))
                            .title(route.endAddress)
                            .position(route.endLocation!!)
                    )!!
                )
                val polylineOptions =
                    PolylineOptions().geodesic(true).color(Color.parseColor("#0F91DC")).width(10f)
                for (i in 0 until (route.points!!.size)) polylineOptions.add(route.points?.get(i))
                (polylinePaths as ArrayList<Polyline>).add(gMap!!.addPolyline(polylineOptions))
            }
        }

    fun changeMakerSize(icon: Int, height: Int, width: Int): Bitmap? {
        val b = BitmapFactory.decodeResource(resources, icon)
        return Bitmap.createScaledBitmap(b, width, height, false)
    }

}
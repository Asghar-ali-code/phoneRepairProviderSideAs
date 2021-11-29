package com.funtash.mobileprovider.livedata.View.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.funtash.mobileprovider.Api.RetrofitClient
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.Utils.*
import com.funtash.mobileprovider.Utils.Route.DirectionFinder
import com.funtash.mobileprovider.Utils.Route.DirectionFinderListener
import com.funtash.mobileprovider.Utils.Route.Route
import com.funtash.mobileprovider.databinding.ActivityOnMapBinding
import com.funtash.mobileprovider.livedata.ViewModel.ViewModelLiveData
import com.funtash.mobileprovider.response.OrderDetail
import com.funtash.mobileprovider.response.OrderStatus
import com.funtash.mobilerepairinguserapp.Api.ApiClient
import com.funtash.mobilerepairinguserapp.Api.ApiHelper
import com.funtash.mobilerepairinguserapp.Api.ApiInterface
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
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ActivityOnMap : AppCompatActivity(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    DirectionFinderListener {

    private var o_id:String?=null
    private var u_id:String?=null
    private var p_img:String?=null
    private var status: String? = "on the way"
    private var api_token: String? = null
    private var address: String? = null

    private val REQUEST_PERMISSION_LOCATION = 991
    private var gMap: GoogleMap? = null
    private var googleApiClient: GoogleApiClient? = null
    private var lastKnownLocation: Location? = null
    private var originMarkers: List<Marker> = ArrayList()
    private var destinationMarkers: List<Marker> = ArrayList()
    private var polylinePaths: List<Polyline> = ArrayList()

    private val dlg= CustomLoader
    private var alertDialog: LottieAlertDialog? = null

    private lateinit var viewModelLiveData: ViewModelLiveData
    private lateinit var binding: ActivityOnMapBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initView() {
        Prefs.initPrefs(this)
        o_id = intent.getStringExtra("o_id")

        initPrgDlg()
        handleClick()
        setupGoogleApiClient()
        setupViewModel()
        loadData()
    }

    private fun setupViewModel() {
        viewModelLiveData = ViewModelProviders.of(
            this,
            ViewModelFactoryLiveData(ApiHelper(ApiClient.getApi(this)!!))
        ).get(ViewModelLiveData::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadData() {
        viewModelLiveData.getbooking_details(api_token.toString(), o_id.toString())
        viewModelLiveData.bookingdetails.observe(this, Observer {

            when (it.status) {

                Resource.Status.SUCCESS -> {
                    if (it.data?.success == true) {
                        try {
                            showData(it.data)

                        } catch (e: UnsupportedEncodingException) {
                            e.printStackTrace()
                            dlg.hideDlg(this)
                        }
                    } else if (it.data?.success == false) {
                        dlg.hideDlg(this)
                    }
                }
                Resource.Status.ERROR -> {
                    Log.e("error", it.message.toString())
                    dlg.hideDlg(this)

                }

                Resource.Status.LOADING -> {
                }

                Resource.Status.FAILURE -> {
                    dlg.hideDlg(this)
                    Utility.displaySnackBar(
                        binding.root,
                        it.message ?: "",
                        applicationContext
                    )

                }
            }

        })
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun showData(data: OrderDetail) {
        try {
            DirectionFinder(
                this, data.data.address.toString(),address.toString()
            ).execute()

            var price=0
            var sname=""
            for (i in data.data.service) {
                if (sname == "")
                    sname = i.name.en.toString()
                else
                    sname = sname + "," + i.name.en.toString()
                price += i.discount_price
            }

            binding.tvname.text = data.data.user.name.toString()
            binding.tvservice.text =sname
            binding.tvaddress.text = data.data.address.toString()
            binding.tvno.text=data.data.user.phone_number.toString()

            if(data.data.payment==null && data.data.booking_status.equals("accepted")) {
                binding.tvPstatus.text = "Pending: $$price"
            }
            else {
                binding.tvPstatus.text = "Paid: $ "+data.data.payment.amount
            }

            u_id = data.data.user.id.toString()
            p_img = data.data.user.profile_pic.toString()
            binding.tvorderno.text = "#$o_id"
            binding.tvstatus.text = data.data.booking_status.toString()

            if (binding.tvstatus.text.toString() == "accepted") {
                status = "on the way"
                binding.btnaccept.text = status
            } else if (binding.tvstatus.text.toString() == "on the way") {
                status = "arrived"
                binding.btnaccept.text = status
            } else if (binding.tvstatus.text.toString() == "arrived") {
                status = "start"
                binding.btnaccept.text = status
            } else if (binding.tvstatus.text.toString() == "start") {
                status = "completed"
                binding.btnaccept.text = status
            }
            else{
                binding.btnaccept.visibility= View.GONE
                if(binding.tvPstatus.text.toString().equals("completed",true))
                    binding.btnpayment.visibility=View.VISIBLE
            }

            val strdate = parseDateToddMMyyyy(data.data.times.toString())
            if (strdate != null) {
                binding.tvdate.text = strdate.split("-")[1]
                binding.tvtime.text = strdate.split("-")[0]
            }
            dlg.hideDlg(this)
        } catch (e: Exception) {
            Log.e("exp",e.message.toString())
            dlg.hideDlg(this)
        }
    }

    private fun initPrgDlg() {
        dlg.CustomDlg(this,"Loading, please wait...")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.google_map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        api_token = Prefs.getString("api_token", "")
        alertDialog = LottieAlertDialog.Builder(this, DialogTypes.TYPE_LOADING)
            .setTitle("Order Status:$status")
            .setDescription("Please wait a moment...")
            .build()
        alertDialog!!.setCancelable(false)

        try {
            // check if GPS enabled

            // check if GPS enabled
            val gpsTracker = GpsTracker(this)

            if (gpsTracker.isGPSTrackingEnabled) {
                gpsTracker.onLocationChanged(gpsTracker.location)
                val lat = gpsTracker.latitude.toString()
                val lng = gpsTracker.longitude.toString()
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses =
                    geocoder.getFromLocation(gpsTracker.latitude, gpsTracker.longitude, 1)
                if (addresses.isNotEmpty()) {
                    if (addresses.size > 0) {
                        address = addresses[0].getAddressLine(0)
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


    }

    private fun handleClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnpayment.setOnClickListener {

        }


        binding.imgmessage.setOnClickListener {
            val intent=Intent(this,ChatActivity::class.java)
            intent.putExtra("o_id",o_id.toString())
            intent.putExtra("u_id",u_id.toString())
            intent.putExtra("p_img",p_img.toString())
            startActivity(intent)
        }

        binding.imgphone.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+binding.tvno.text.toString())
                startActivity(intent)
            }catch (e:Exception){
                Log.e("exp",e.message.toString())
            }
        }
        binding.btnaccept.setOnClickListener {
            if(status.equals("on the way") && binding.tvPstatus.text.toString().equals("Un Paid")
                || binding.tvPstatus.text.toString().equals("Pending")){
                Utility.displaySnackBar(
                    binding.btnaccept, "Booking Payment Pending!",
                    this@ActivityOnMap)
            }
            else
                updateOrderStatus()
        }

        binding.tvMap.setOnClickListener {
            val intent = Intent(this, TrackOrderActivity::class.java)
            intent.putExtra("o_id", o_id.toString())
            startActivity(intent)
        }



    }

    private fun updateOrderStatus() {
        initPrgDlg()
        alertDialog!!.show()
        val apiService = RetrofitClient.getClientRetro(this).create(ApiInterface::class.java)
        val call = apiService?.order_status(o_id.toString(), status.toString())
        call?.enqueue(object : Callback<OrderStatus> {
            override fun onResponse(
                call: Call<OrderStatus>,
                response: Response<OrderStatus>
            ) {
                try {
                    Log.e("response", "" + response.body())
                    if (response.body()?.success == true) {
                        try {
                            alertDialog!!.dismiss()
                            dlg.hideDlg(this@ActivityOnMap)
                            val successDlg: LottieAlertDialog =
                                LottieAlertDialog.Builder(
                                    this@ActivityOnMap,
                                    DialogTypes.TYPE_SUCCESS
                                )
                                    .setTitle("Order : $status")
                                    .setDescription(response.body()?.message.toString())
                                    .setPositiveText("OK")
                                    .setPositiveButtonColor(
                                        ContextCompat.getColor(
                                            this@ActivityOnMap,
                                            R.color.blue
                                        )
                                    )
                                    .setPositiveTextColor(
                                        ContextCompat.getColor(
                                            this@ActivityOnMap,
                                            R.color.white
                                        )
                                    )
                                    // Error View
                                    .setPositiveListener(object : ClickListener {
                                        override fun onClick(dialog: LottieAlertDialog) {
                                            // This is the usage same instance of view
                                            dialog.dismiss()
                                            binding.tvstatus.text =
                                                response.body()!!.data.toString()
                                            if (binding.tvstatus.text.toString() == "accepted") {
                                                status = "on the way"
                                                binding.btnaccept.text = status
                                            } else if (binding.tvstatus.text.toString() == "on the way") {
                                                status = "arrived"
                                                binding.btnaccept.text = status
                                            } else if (binding.tvstatus.text.toString() == "arrived") {
                                                status = "start"
                                                binding.btnaccept.text = status
                                            } else if (binding.tvstatus.text.toString() == "start") {
                                                status = "completed"
                                                binding.btnaccept.text = status
                                            } else
                                                finish()
                                        }
                                    })
                                    .build()
                            successDlg.show()
                        } catch (e: Exception) {
                            dlg.hideDlg(this@ActivityOnMap)
                        }
                    } else {
                        alertDialog!!.dismiss()
                        dlg.hideDlg(this@ActivityOnMap)
                        try {
                            var successDlg: LottieAlertDialog =
                                LottieAlertDialog.Builder(
                                    this@ActivityOnMap,
                                    DialogTypes.TYPE_WARNING
                                )
                                    .setTitle("Failed!")
                                    .setDescription(response.body()?.message.toString())
                                    .setPositiveText("OK")
                                    .setPositiveButtonColor(Color.parseColor("#f44242"))
                                    .setPositiveTextColor(Color.parseColor("#ffeaea"))
                                    // Error View
                                    .setPositiveListener(object : ClickListener {
                                        override fun onClick(dialog: LottieAlertDialog) {
                                            // This is the usage same instance of view
                                            dialog.dismiss()
                                        }
                                    })
                                    .build()
                            successDlg.show()
                            Log.e("failed", response.errorBody()?.string().toString())

                        } catch (e: Exception) {
                            dlg.hideDlg(this@ActivityOnMap)
                        }
                    }
                } catch (e: Exception) {
                    alertDialog!!.dismiss()
                    dlg.hideDlg(this@ActivityOnMap)
                    try {
                        Utility.warningSnackBar(
                            binding.btnaccept, e.message.toString(),
                            this@ActivityOnMap
                        )
                    } catch (e: Exception) {
                        Toast.makeText(this@ActivityOnMap, e.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }

            override fun onFailure(call: Call<OrderStatus>, t: Throwable) {
                alertDialog!!.dismiss()
                dlg.hideDlg(this@ActivityOnMap)
                try {
                    var message = ""
                    if (t is NoConnectivityException) {
                        message = "No Internet connection!"
                    } else {
                        message = t.message.toString()
                    }
                    System.out.println("Failure...!" + t.stackTrace);
                    Log.e("error", t.message.toString())
                    val successDlg: LottieAlertDialog =
                        LottieAlertDialog.Builder(this@ActivityOnMap, DialogTypes.TYPE_ERROR)
                            .setTitle("Error")
                            .setDescription(message)
                            .setPositiveText("OK")
                            .setPositiveButtonColor(
                                ContextCompat.getColor(
                                    this@ActivityOnMap,
                                    R.color.blue
                                )
                            )
                            .setPositiveTextColor(
                                ContextCompat.getColor(
                                    this@ActivityOnMap,
                                    R.color.white
                                )
                            )
                            // Error View
                            .setPositiveListener(object : ClickListener {
                                override fun onClick(dialog: LottieAlertDialog) {
                                    // This is the usage same instance of view
                                    dialog.dismiss()
                                }
                            })
                            .build()
                    successDlg.show()

                } catch (e: Exception) {
                    Toast.makeText(this@ActivityOnMap, e.toString(), Toast.LENGTH_LONG).show()
                }

            }
        })
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun parseDateToddMMyyyy(time: String?): String? {
        return try {
            //val inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"
            val inputPattern = "yyyy-MM-dd HH:mm"
            val outputPattern = "HH:mm-dd MMM"
            var inputFormat: SimpleDateFormat? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                inputFormat = SimpleDateFormat(inputPattern)
            }
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                assert(inputFormat != null)
                date = inputFormat?.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            str
        } catch (e: java.lang.Exception) {
            ""
        }
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
                        .icon(BitmapDescriptorFactory.fromBitmap(changeMakerSize(R.drawable.user_icon,
                            70,70)))
                        .title(route.startAddress)
                        .position(route.startLocation!!)
                )!!
            )
            val bmp=changeMakerSize(R.drawable.biker_icon,70,70)
            (destinationMarkers as ArrayList<Marker>).add(
                gMap?.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .title(route.endAddress)
                        .position(route.endLocation!!)
                )!!
            )
            val polylineOptions =
                PolylineOptions().geodesic(true).color(Color.parseColor("#0F91DC")).width(15f)
            for (i in 0 until (route.points!!.size)) polylineOptions.add(route.points?.get(i))
            (polylinePaths as ArrayList<Polyline>).add(gMap!!.addPolyline(polylineOptions))
        }
        dlg.hideDlg(this)
    }

    fun changeMakerSize(icon: Int, height: Int, width: Int): Bitmap? {
        val b = BitmapFactory.decodeResource(resources, icon)
        return Bitmap.createScaledBitmap(b, width, height, false)
    }
}
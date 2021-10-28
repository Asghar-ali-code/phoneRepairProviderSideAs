package com.funtash.mobileprovider.Utils.Route

import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder


/**
 * Created by Umer Shahzad on 24/10/2021.
 */
class DirectionFinder(listener: DirectionFinderListener, origin: String, destination: String) {
    private val listener: DirectionFinderListener = listener
    private val origin: String
    private val destination: String
    @Throws(UnsupportedEncodingException::class)
    fun execute() {
        listener.onDirectionFinderStart()
        DownloadRawData().execute(createUrl())
    }

    private fun createUrl(): String {
        val urlOrigin = URLEncoder.encode(origin, "utf-8")
        val urlDestination = URLEncoder.encode(destination, "utf-8")
        Log.e("url",
            DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY
        )
        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY
    }

    private inner class DownloadRawData :
        AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String? {
            val link = params[0]
            try {

                var data: String? = ""

                try {
                    data = downloadUrl(link)
                } catch (e: java.lang.Exception) {
                    Log.d("Background Task", e.toString())
                }
                return data
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
            e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(res: String) {
            try {
                parseJSon(res)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String? {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: java.lang.Exception) {
            Log.d("Exception", e.toString())
        } finally {
            if (iStream != null) {
                iStream.close()
            }
            if (urlConnection != null) {
                urlConnection.disconnect()
            }
        }
        return data
    }
    private fun parseJSon(data: String) {
        if (data == null) return
        try {
            val routes: ArrayList<Route> = ArrayList()
            val jsonData = JSONObject(data)
            Log.e("legs","gs"+jsonData.toString())
            val jsonRoutes = jsonData.getJSONArray("routes")
            for (i in 0 until jsonRoutes.length()) {
                Log.e("legs0","gs"+jsonRoutes.length())
                val jsonRoute = jsonRoutes.getJSONObject(i)
                val route = Route()
                val overview_polylineJson = jsonRoute.getJSONObject("overview_polyline")
                val jsonLegs = jsonRoute.getJSONArray("legs")
                Log.e("legs1", "gs$jsonLegs")
                val jsonLeg = jsonLegs.getJSONObject(0)
                val jsonDistance = jsonLeg.getJSONObject("distance")
                val jsonDuration = jsonLeg.getJSONObject("duration")
                val jsonEndLocation = jsonLeg.getJSONObject("end_location")
                val jsonStartLocation = jsonLeg.getJSONObject("start_location")
                Log.e("legs2","fv")
                route.distance =
                    Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"))
                route.duration =
                    Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"))
                route.endAddress = jsonLeg.getString("end_address")
                route.startAddress = jsonLeg.getString("start_address")
                route.startLocation =
                    LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"))
                route.endLocation =
                    LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"))
                route.points = decodePolyLine(overview_polylineJson.getString("points"))
                routes.add(route)
            }
            listener.onDirectionFinderSuccess(routes)
        }catch (e:Exception){
            Log.e("exp",e.message.toString())
        }
    }

    private fun decodePolyLine(poly: String): List<LatLng> {
        val len = poly.length
        var index = 0
        val decoded: MutableList<LatLng> = ArrayList()
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = poly[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = poly[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            decoded.add(
                LatLng(
                    lat / 100000.0, lng / 100000.0
                )
            )
        }
        return decoded
    }

    companion object {
        private const val DIRECTION_URL_API =
            "https://maps.googleapis.com/maps/api/directions/json?"
        private const val GOOGLE_API_KEY = "AIzaSyDZqQYU8GvFR2-AEqGnpPlMKA9DZv_gyoc"
    }

    init {
        this.origin = origin
        this.destination = destination
    }
}
package com.curb.curbuserapplication

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class GeoTask(var mContext: Context) :
    AsyncTask<String?, Void?, String?>() {
    var duration: String? = null
    var geo1: Geo
    var finalValue = JSONObject()
    var finalValueString :String? =  null

    //This function is executed before before "doInBackground(String...params)" is executed to dispaly the progress dialog
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result != null) {
            geo1.setDouble(result)
        }
    }

    interface Geo {
        fun setDouble(min: String?)
        fun setJsonBody(jsonResponse:String)
        fun showPath(latLngList: List<LatLng>)
    }

    //constructor is used to get the context.
    init {
        geo1 = mContext as Geo
    }

    override fun doInBackground(vararg params: String?): String? {
        try {
            val url = URL(params[0])
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.connect()
            val statuscode = con.responseCode
            if (statuscode == HttpURLConnection.HTTP_OK) {
                val br = BufferedReader(InputStreamReader(con.inputStream))
                val sb = StringBuilder()
                var line = br.readLine()
                while (line != null) {
                    sb.append(line)
                    line = br.readLine()
                }
                val json = sb.toString()
                Log.d("JSON", json)
                val root = JSONObject(json)
                val array_rows = root.getJSONArray("rows")
                Log.d("JSON", "array_rows:$array_rows")
                val object_rows = array_rows.getJSONObject(0)
                duration = json
                Log.d("JSON", "object_rows:$object_rows")
                // finalListValue = object_rows
                val array_elements = object_rows.getJSONArray("elements")
                Log.d("JSON", "array_elements:$array_elements")
                val object_elements = array_elements.getJSONObject(0)
                Log.d("JSON", "object_elements:$object_elements")
                val object_duration = object_elements.getJSONObject("duration")
                val object_distance = object_elements.getJSONObject("distance")
                val object_fare = object_elements.getJSONObject("fareEstimate")
                Log.d("JSON", "object_duration:$object_duration")
                return duration.toString()
                //   return object_duration.getString("value") + "," + object_distance.getString("value") + "," + object_fare.getString("fareEstimates")
            }
        } catch (e: MalformedURLException) {
            Log.d("error", "error1")
        } catch (e: IOException) {
            Log.d("error", "error2")
        } catch (e: JSONException) {
            Log.d("error", "error3")
        }
        return duration.toString()
    }
}
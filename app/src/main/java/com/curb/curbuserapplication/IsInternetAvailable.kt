package com.curb.curbuserapplication

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData

class IsInternetAvailable(private val context: Context): LiveData<Boolean>() {
    private var connectivtyMAnager : ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private lateinit var networkCallback : ConnectivityManager.NetworkCallback

    override fun onActive() {
        super.onActive()
        updateConnection()
        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->{
                connectivtyMAnager.registerDefaultNetworkCallback(connectivityManagerCallback())
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->{
                lollipopNetworkRequest()
            }
            else ->{
                context.registerReceiver(
                    networkReceiver,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
        // connectivtyMAnager.unregisterNetworkCallback(connectivityManagerCallback())
        try {
            context.unregisterReceiver(networkReceiver)
        }
        catch (e:Exception){

        }
//        }else{
//            context.unregisterReceiver(networkReceiver)
//        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun lollipopNetworkRequest(){
        val requestBuilder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        connectivtyMAnager.registerNetworkCallback(requestBuilder.build(),
            connectivityManagerCallback())
    }
    private fun connectivityManagerCallback(): ConnectivityManager.NetworkCallback{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            networkCallback = object : ConnectivityManager.NetworkCallback(){
                override fun onLost(network: Network) {
                    super.onLost(network)
                    postValue(false)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    try {
                        val command = "ping -c 1 google.com"
                        Runtime.getRuntime().exec(command).waitFor() == 0
                        postValue(true)
                    } catch (e:Exception) {
                        postValue(false)
                    }
                }
            }
            return networkCallback
        }
        else{
            throw IllegalAccessError("Error")
        }
    }
    private val networkReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConnection()
        }
    }

    private fun updateConnection(){
        val activeNetwork : NetworkInfo? = connectivtyMAnager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)
    }
}
package com.isayevapps.blackbeaty2

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import tej.wifitoolslib.DevicesFinder
import tej.wifitoolslib.interfaces.OnDeviceFindListener
import tej.wifitoolslib.models.DeviceItem
import java.net.URL
import kotlin.concurrent.thread


class Model(private val context: Context) {

    private lateinit var deviceFinder: DevicesFinder
    private var deviceAddress = ""

    fun setDeviceFinder(activityContext: Context, deviceCallback: DeviceCallback) {
        deviceFinder = DevicesFinder(activityContext, object : OnDeviceFindListener {
            override fun onStart() {
            }

            override fun onDeviceFound(device: DeviceItem) {
                thread {
                    try {
                        val response =
                            URL("http://${device.ipAddress}/hello").readText()
                        if (response == "hi") {
                            deviceAddress = device.ipAddress
                        }
                    } catch (_: Exception) {
                    }
                }
            }

            override fun onComplete(deviceItems: List<DeviceItem>) {
                if (deviceAddress == "" && hasWifi()) {
                    deviceCallback.onNotFound()
                }
                if (deviceAddress != "") {
                    deviceCallback.onFound()
                }
            }

            override fun onFailed(errorCode: Int) {
            }
        })
    }

    fun hasWifi(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
        return false
    }

    fun registerNetworkChanges(networkChangesCallback: NetworkChangesCallback) {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                networkChangesCallback.onAvailable()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                networkChangesCallback.onLost()
            }
        }
        val connectivityManager =
            getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    fun searchDevice() {
        try {
            Log.d("MyTag", "searching")
            if (!deviceFinder.isRunning) {
                Log.d("MyTag", "started new searching")
                deviceFinder.setTimeout(5000).start()
            }
        } catch (_: Exception) {
        }

    }
}
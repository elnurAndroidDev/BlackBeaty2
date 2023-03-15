package com.isayevapps.blackbeaty2.models

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat.getSystemService
import com.isayevapps.blackbeaty2.callbacks.DeviceCallback
import com.isayevapps.blackbeaty2.callbacks.NetworkChangesCallback
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
                deviceAddress = ""
            }

            override fun onDeviceFound(device: DeviceItem) {
                thread {
                    try {
                        val c = Command(11, 0, 0)
                        val commandString = c.toString()
                        val response =
                            URL("http://${device.ipAddress}/$commandString").readText()
                        if (response == "200") {
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
                if (deviceAddress != "" && hasWifi()) {
                    val sharedPref =
                        context.getSharedPreferences("APP_SHARED_PREF", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("LAST_DEVICE_ADDRESS", deviceAddress)
                        apply()
                    }
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

    fun searchDevice(deviceCallback: DeviceCallback) {
        thread {
            try {
                val sharedPref =
                    context.getSharedPreferences("APP_SHARED_PREF", Context.MODE_PRIVATE)
                val deviceAddress = sharedPref.getString("LAST_DEVICE_ADDRESS", "")
                var response = ""
                if (deviceAddress != "")
                    response = URL("http://$deviceAddress/hello").readText()
                if (response == "hi" && hasWifi()) {
                    deviceCallback.onFound()
                } else {
                    if (!deviceFinder.isRunning) {
                        deviceFinder.setTimeout(5000).start()
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun sendCommand(command: Command) {
        thread {
            try {
                val commandString = command.toString()
                URL("http://$deviceAddress/$commandString").readText()
            } catch (_: Exception) {
            }
        }
    }
}
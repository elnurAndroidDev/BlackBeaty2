package com.isayevapps.blackbeaty2.models

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.isayevapps.blackbeaty2.callbacks.DeviceCallback
import com.isayevapps.blackbeaty2.callbacks.NetworkChangesCallback
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import tej.wifitoolslib.DevicesFinder
import tej.wifitoolslib.interfaces.OnDeviceFindListener
import tej.wifitoolslib.models.DeviceItem
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class Model(private val context: Context) {

    private lateinit var deviceFinder: DevicesFinder
    private var deviceAddress = ""
    private var flagSeat = false
    private var seatID = 0
    private var seatOP = 0
    private var seatVal = 0


    private val client =
        OkHttpClient.Builder().readTimeout(1, TimeUnit.SECONDS).connectTimeout(1, TimeUnit.SECONDS)
            .build()

    init {
        sendSeatCommand()
    }

    fun setDeviceFinder(activityContext: Context, deviceCallback: DeviceCallback) {
        deviceFinder = DevicesFinder(activityContext, object : OnDeviceFindListener {
            override fun onStart() {
                deviceAddress = ""
                Log.d("MyTag", "starting searching")
            }

            override fun onDeviceFound(device: DeviceItem) {
                thread {
                    try {
                        val (commandString, expectedResponse) = checkingRequest()
                        //Log.d("MyTag", "$commandString $expectedResponse")
                        val response = URL("http://${device.ipAddress}/$commandString").readText()
                        if (response == expectedResponse.toString()) {
                            deviceAddress = device.ipAddress
                        }
                        //Log.d("MyTag", response)
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
        val networkRequest =
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
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
            val sharedPref =
                context.getSharedPreferences("APP_SHARED_PREF", Context.MODE_PRIVATE)
            val _deviceAddress = sharedPref.getString("LAST_DEVICE_ADDRESS", "")
            val (commandString, expectedResponse) = checkingRequest()
            val request = Request.Builder()
                .url("http://$_deviceAddress/$commandString")
                .build()
            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        if (response.body!!.string() == expectedResponse.toString()) {
                            deviceAddress = _deviceAddress!!
                            deviceCallback.onFound()
                        }
                    }
                }
            } catch (_: Exception) {
                if (!deviceFinder.isRunning) {
                    deviceFinder.setTimeout(5000).start()
                }
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

    fun checkingRequest(): Pair<String, Int> {
        val randomID = (20..100).random()
        val randomValue = (1..10000).random()
        val randomOP = (0..255).random()
        val c = Command(randomID, randomOP, randomValue)
        val expectedResponse =
            (((randomID * 255) + randomOP + 9852) * 1.658 + randomValue).toInt()
        val commandString = c.toString()
        return Pair(commandString, expectedResponse)
    }

    fun startIsAliveChecking(deviceCallback: DeviceCallback) {
        thread {
            while (true) {
                Log.d("MyTag", "isAlive")
                val (commandString, expectedResponse) = checkingRequest()
                val request = Request.Builder()
                    .url("http://$deviceAddress/$commandString")
                    .build()
                try {
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            if (response.body!!.string() == expectedResponse.toString()) {
                                Log.d("MyTag", "Alive")
                            }
                        }
                    }
                } catch (_: Exception) {
                    Log.d("MyTag", "not alive3")
                    if (hasWifi())
                        deviceCallback.onNotFound()
                    return@thread
                }
                Thread.sleep(5000)
            }
        }
    }

    private fun sendSeatCommand() {
        thread {
            while (true) {
                if (flagSeat) {
                    Log.d("MyTag", "sending")
                    val c = Command(seatID, seatOP, seatVal)
                    val commandString = c.toString()
                    val request = Request.Builder()
                        .url("http://$deviceAddress/$commandString")
                        .build()
                    try {
                        client.newCall(request).execute().use { response ->
                            if (response.isSuccessful) {
                                Log.d("MyTag", "sent seat command")
                            }
                        }
                    } catch (_: Exception) {
                        Log.d("MyTag", "not sent seat commannd")
                    }
                }
                Thread.sleep(500)
            }
        }
    }

    fun stopSendingSeatCommand() {
        flagSeat = false
    }

    fun setSeatCommand(id: Int, op: Int, value: Int) {
        seatID = id
        seatOP = op
        seatVal = value
        flagSeat = true
    }
}
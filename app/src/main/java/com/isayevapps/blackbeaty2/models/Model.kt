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
import okhttp3.OkHttpClient
import okhttp3.Request
import tej.wifitoolslib.DevicesFinder
import tej.wifitoolslib.interfaces.OnDeviceFindListener
import tej.wifitoolslib.models.DeviceItem
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class Model(private val context: Context) {

    private lateinit var deviceFinder: DevicesFinder
    private var deviceAddress = ""
    private var flag = false
    private var ID = 0
    private var OP = 0
    private var VALUE = 0

    private val client =
        OkHttpClient.Builder().readTimeout(500, TimeUnit.MILLISECONDS).connectTimeout(500, TimeUnit.MILLISECONDS)
            .build()

    init {
        sendingLongCommand()
    }

    fun setDeviceFinder(activityContext: Context, deviceCallback: DeviceCallback) {
        deviceFinder = DevicesFinder(activityContext, object : OnDeviceFindListener {
            override fun onStart() {
                deviceAddress = ""
                Log.d("MyTag", "starting searching")
            }

            override fun onDeviceFound(device: DeviceItem) {
                thread {
                    val (commandString, expectedResponse) = checkingRequest()
                    try {
                        val request = Request.Builder()
                            .url("http://${device.ipAddress}/$commandString")
                            .build()
                        client.newCall(request).execute().use { response ->
                            if (response.isSuccessful) {
                                if (response.body!!.string() == expectedResponse && hasWifi()) {
                                    deviceAddress = device.ipAddress
                                    saveIPToMemory(deviceAddress)
                                    deviceCallback.onFound()
                                }
                            }
                        }
                    } catch (_: Exception) {
                    }
                }
            }

            override fun onComplete(deviceItems: List<DeviceItem>) {
                if (deviceAddress == "" && hasWifi()) {
                    deviceCallback.onNotFound()
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
            val (commandString, expectedResponse) = checkingRequest()
            val ipAddressFromMemory = getIPFromMemory()
            try {
                if(!deviceFinder.isRunning) {
                    val request = Request.Builder()
                        .url("http://$ipAddressFromMemory/$commandString")
                        .build()
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            if (response.body!!.string() == expectedResponse) {
                                deviceAddress = ipAddressFromMemory
                                deviceCallback.onFound()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (!deviceFinder.isRunning) {
                    deviceFinder.setTimeout(5000).start()
                }
            }
        }
    }

    fun checkingRequest(): Pair<String, String> {
        val randomID = (20..100).random()
        val randomValue = (1..10000).random()
        val randomOP = (0..255).random()
        val c = Command(randomID, randomOP, randomValue)
        val expectedResponse =
            (((randomID * 255) + randomOP + 9852) * 1.658 + randomValue).toInt().toString()
        val commandString = c.toString()
        return Pair(commandString, expectedResponse)
    }

    fun startIsAliveChecking(deviceCallback: DeviceCallback) {
        thread {
            while (true) {
                if (!flag) {
                    Log.d("MyTag", "isAlive")
                    val (commandString, expectedResponse) = checkingRequest()
                    try {
                        val request = Request.Builder()
                            .url("http://$deviceAddress/$commandString")
                            .build()
                        client.newCall(request).execute().use { response ->
                            if (response.isSuccessful) {
                                if (response.body!!.string() == expectedResponse) {
                                    Log.d("MyTag", "Alive")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("MyTag", "not alive ${e.message.toString()}")
                        if (hasWifi())
                            deviceCallback.onNotFound()
                        return@thread
                    }
                    Thread.sleep(5000)
                }
            }
        }
    }

    fun sendCommand(command: Command) {
        thread {
            val commandString = command.toString()
            try {
                val request = Request.Builder()
                    .url("http://$deviceAddress/$commandString")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Log.d("MyTag", "sent command $commandString")
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun startLongCommand(id: Int, op: Int, value: Int) {
        ID = id
        OP = op
        VALUE = value
        flag = true
    }

    private fun sendingLongCommand() {
        thread {
            while (true) {
                if (flag) {
                    Log.d("MyTag", "sending")
                    val c = Command(ID, OP, VALUE)
                    val commandString = c.toString()
                    try {
                        val request = Request.Builder()
                            .url("http://$deviceAddress/$commandString")
                            .build()
                        client.newCall(request).execute().use { response ->
                            if (response.isSuccessful) {
                                Log.d("MyTag", "sent command")
                            }
                        }
                    } catch (_: Exception) {
                        Log.d("MyTag", "not sent commannd")
                    }
                }
            }
        }
    }

    fun stopSendingLongCommand() {
        flag = false
    }

    private fun saveIPToMemory(ipAddress: String) {
        val sharedPref =
            context.getSharedPreferences(
                "APP_SHARED_PREF",
                Context.MODE_PRIVATE
            )
        with(sharedPref.edit()) {
            putString("LAST_DEVICE_ADDRESS", ipAddress)
            apply()
        }
    }

    private fun getIPFromMemory(): String {
        val sharedPref =
            context.getSharedPreferences("APP_SHARED_PREF", Context.MODE_PRIVATE)
        val ipAddress = sharedPref.getString("LAST_DEVICE_ADDRESS", "")
        return ipAddress ?: ""
    }
}
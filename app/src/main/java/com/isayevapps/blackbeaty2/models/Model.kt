package com.isayevapps.blackbeaty2.models

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.isayevapps.blackbeaty2.callbacks.DeviceCallback
import com.isayevapps.blackbeaty2.callbacks.LightObjectUpdater
import com.isayevapps.blackbeaty2.callbacks.NetworkChangesCallback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import tej.wifitoolslib.DevicesFinder
import tej.wifitoolslib.interfaces.OnDeviceFindListener
import tej.wifitoolslib.models.DeviceItem
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class Model(private val context: Context) {

    private lateinit var deviceFinder: DevicesFinder
    private lateinit var led1: LightObjectUpdater
    private lateinit var led2: LightObjectUpdater
    private lateinit var light: LightObjectUpdater
    private lateinit var starSky: LightObjectUpdater
    private var deviceAddress = ""
    private var failCount = 0
    private var buttonPressed = false
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private val client =
        OkHttpClient.Builder().readTimeout(2, TimeUnit.SECONDS)
            .connectTimeout(2, TimeUnit.SECONDS)
            .build()


    fun registerUpdaters(
        _led1: LightObjectUpdater,
        _led2: LightObjectUpdater,
        _light: LightObjectUpdater,
        _starSky: LightObjectUpdater
    ) {
        led1 = _led1
        led2 = _led2
        light = _light
        starSky = _starSky
    }

    fun setDeviceFinder(activityContext: Context, deviceCallback: DeviceCallback) {
        deviceFinder = DevicesFinder(activityContext, object : OnDeviceFindListener {
            override fun onStart() {
                deviceAddress = ""
                //Log.d("MyLog", "started searching")
            }

            override fun onDeviceFound(device: DeviceItem) {
                thread {
                    //Log.d("MyLog", "device: ${device.ipAddress}")
                    val (commandString, expectedResponse) = checkingRequest()
                    try {
                        val request = Request.Builder()
                            .url("http://${device.ipAddress}/$commandString")
                            .build()
                        client.newCall(request).execute().use { response ->
                            if (response.isSuccessful) {
                                val res = response.body!!.string()
                                //Log.d("MyLog", res)
                                val obj = Gson().fromJson(res, ServerObject::class.java)
                               // Log.d("MyLog", "parsed")
                                if (hasWifi()) {
                                    deviceAddress = device.ipAddress
                                    saveIPToMemory(deviceAddress)
                                    deviceCallback.onFound()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        //Log.d("MyLog", e.toString())
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
            //Log.d("MyLog", "ip from memory $ipAddressFromMemory")
            try {
                if (!deviceFinder.isRunning) {
                    val request = Request.Builder()
                        .url("http://$ipAddressFromMemory/$commandString")
                        .build()
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val res = response.body!!.string()
                           // Log.d("MyLog", "From memory")
                            val obj = Gson().fromJson(res, ServerObject::class.java)
                            if (hasWifi()) {
                                deviceAddress = ipAddressFromMemory
                                deviceCallback.onFound()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
               // Log.d("MyLog", e.toString())
                if (!deviceFinder.isRunning) {
                    deviceFinder.setTimeout(5000).start()
                }
            }
        }
    }

    fun startIsAliveChecking(deviceCallback: DeviceCallback) {
        thread {
            while (true) {
                val (commandString, expectedResponse) = checkingRequest()
                try {
                    val request = Request.Builder()
                        .url("http://$deviceAddress/$commandString")
                        .build()
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            failCount = 0
                            val responseString = response.body!!.string()
                            handleResponse(responseString)
                            //Log.d("MyLog", "isAlive")
                        }
                    }
                } catch (_: Exception) {
                    failCount++
                    if (hasWifi() && failCount == 5) {
                        failCount = 0
                        deviceCallback.onNotFound()
                        return@thread
                    }
                }
                Thread.sleep(1000)
            }
        }
    }

    private fun handleResponse(response: String) {
        val obj = Gson().fromJson(response, ServerObject::class.java)

        if (obj.vib == 1 && buttonPressed) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    10000,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }

        led1.updateOnOff(obj.led1[0])
        led1.updateBrightness(obj.led1[1])
        led1.updateColor(obj.led1[2])
        led1.updateEffect(obj.led1[3])

        led2.updateOnOff(obj.led2[0])
        led2.updateBrightness(obj.led2[1])
        led2.updateColor(obj.led2[2])
        led2.updateEffect(obj.led2[3])

        light.updateOnOff(obj.led[0])
        light.updateBrightness(obj.led[1])

        starSky.updateOnOff(obj.ledz[0])
        starSky.updateColor(obj.ledz[1])
    }

    fun buttonActionDown() {
        buttonPressed = true
    }

    fun buttonActionUp() {
        buttonPressed = false
        vibrator.cancel()
    }

    fun sendCommand(command: Command) {
        thread {
            val commandString = command.toString()
            val expectedResponse =
                (((command.getId() * 255) + command.getOp() + 9852) * 1.658 + command.getValue()).toInt()
            var successfull = false
            try {
                val request = Request.Builder()
                    .url("http://$deviceAddress/$commandString")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val res = response.body!!.string()
                        if (res.toInt() == expectedResponse)
                            successfull = true
                    }
                }
            } catch (_: Exception) {
            }
            if (!successfull) {
                try {
                    val request = Request.Builder()
                        .url("http://$deviceAddress/$commandString")
                        .build()
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val res = response.body!!.string()
                            if (res.toInt() == expectedResponse)
                                successfull = true
                        }
                    }
                } catch (_: Exception) {
                }
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
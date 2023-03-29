package com.isayevapps.blackbeaty2.models

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.VibrationEffect
import android.os.Vibrator
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
    private var failCount = 0
    private var buttonPressed = false
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private val client =
        OkHttpClient.Builder().readTimeout(2, TimeUnit.SECONDS)
            .connectTimeout(2, TimeUnit.SECONDS)
            .build()

    fun setDeviceFinder(activityContext: Context, deviceCallback: DeviceCallback) {
        deviceFinder = DevicesFinder(activityContext, object : OnDeviceFindListener {
            override fun onStart() {
                deviceAddress = ""
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
                                if (response.body!!.string() == expectedResponse.toString() && hasWifi()) {
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
                if (!deviceFinder.isRunning) {
                    val request = Request.Builder()
                        .url("http://$ipAddressFromMemory/$commandString")
                        .build()
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            if (response.body!!.string() == expectedResponse.toString()) {
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
                            val responseCode = response.body!!.string().toInt()
                            if ((responseCode == (expectedResponse + 1)) && buttonPressed) {
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        10000,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            }
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
            try {
                val request = Request.Builder()
                    .url("http://$deviceAddress/$commandString")
                    .build()
                client.newCall(request).execute().use {}
            } catch (_: Exception) {
            }
        }
    }

    fun sendRGBOnOffCommand(setRGBOnOff: (Int) -> Unit) {
        thread {
            val command = Command(Command.RGB_ID, 0, 0).toString()
            val expectedRGBOnOffResponse = (((11 * 255) + 0 + 9852) * 1.658 + 0).toInt()
            try {
                val request = Request.Builder()
                    .url("http://$deviceAddress/$command")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseInt = response.body!!.string().toInt()
                        setRGBOnOff(responseInt - expectedRGBOnOffResponse)
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun sendStarSkyOnOffCommand(setStarSkyOnOff: (Int) -> Unit) {
        thread {
            val command = Command(Command.STAR_SKY_ID, 0, 0).toString()
            val expectedStarSkyOnOffResponse = (((12 * 255) + 0 + 9852) * 1.658 + 0).toInt()
            try {
                val request = Request.Builder()
                    .url("http://$deviceAddress/$command")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseInt = response.body!!.string().toInt()
                        setStarSkyOnOff(responseInt - expectedStarSkyOnOffResponse)
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun sendLedBrightness(value: Int, setLightOnOff: (Int) -> Unit) {
        thread {
            val command = Command(Command.LIGHT_ID, 1, value).toString()
            val expectedLightOnOffResponse = (((10 * 255) + 1 + 9852) * 1.658 + value).toInt()
            try {
                val request = Request.Builder()
                    .url("http://$deviceAddress/$command")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseInt = response.body!!.string().toInt()
                        setLightOnOff(responseInt - expectedLightOnOffResponse)
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun sendLightOnOffCommand(setLightOnOff: (Int) -> Unit) {
        thread {
            val command = Command(Command.LIGHT_ID, 0, 0).toString()
            val expectedLightOnOffResponse = (((10 * 255) + 0 + 9852) * 1.658 + 0).toInt()
            try {
                val request = Request.Builder()
                    .url("http://$deviceAddress/$command")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseInt = response.body!!.string().toInt()
                        setLightOnOff(responseInt - expectedLightOnOffResponse)
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun getBrightnessAndOnOff(setBrightness: (Int) -> Unit, setRGBOnOffColor: (Int) -> Unit) {
        thread {
            val getBrightnessRequest = Command(10, 5, 0).toString()
            val expectedBrightnessResponse = (((10 * 255) + 5 + 9852) * 1.658 + 0).toInt()
            val getRGBBrightnessRequest = Command(11, 5, 0).toString()
            val expectedRGBBrightnessResponse = (((11 * 255) + 5 + 9852) * 1.658 + 0).toInt()
            try {
                var request = Request.Builder()
                    .url("http://$deviceAddress/$getBrightnessRequest")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseInt = response.body!!.string().toInt()
                        setBrightness(responseInt - expectedBrightnessResponse)
                    }
                }
                request = Request.Builder()
                    .url("http://$deviceAddress/$getRGBBrightnessRequest")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseInt = response.body!!.string().toInt()
                        setRGBOnOffColor(responseInt - expectedRGBBrightnessResponse)
                    }
                }
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
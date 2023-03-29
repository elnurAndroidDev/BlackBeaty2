package com.isayevapps.blackbeaty2.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isayevapps.blackbeaty2.callbacks.DeviceCallback
import com.isayevapps.blackbeaty2.callbacks.NetworkChangesCallback
import com.isayevapps.blackbeaty2.models.Command
import com.isayevapps.blackbeaty2.models.Model

class ViewModel(private val model: Model) {

    private val _states = MutableLiveData<States>()
    val states = _states as LiveData<States>

    val ledBrightness = MutableLiveData<Int>()
    val rgbOnOff = MutableLiveData<Int>()
    val starSkyOnOff = MutableLiveData<Int>()

    private var seatPos = 0
    private var seatPart = 0

    fun getSeatPos() = seatPos
    fun setSeatPos(pos: Int) {
        seatPos = pos
    }

    fun getSeatPart() = seatPart
    fun setSeatPart(part: Int) {
        seatPart = part
    }

    private val networkChangesCallback = object : NetworkChangesCallback {
        override fun onAvailable() {
            _states.postValue(States.Connection)
        }

        override fun onLost() {
            _states.postValue(States.WaitForConnection)
        }
    }

    private val deviceCallback = object : DeviceCallback {
        override fun onFound() {
            getBrightnessAndOnOff()
            startIsAliveChecking()
            _states.postValue(States.Connected)
        }

        override fun onNotFound() {
            _states.postValue(States.Connection)
        }
    }

    fun init(activityContext: Context, isInitial: Boolean) {
        if (isInitial) {
            model.registerNetworkChanges(networkChangesCallback)
            model.setDeviceFinder(activityContext, deviceCallback)
        }
    }

    fun startIsAliveChecking() {
        model.startIsAliveChecking(deviceCallback)
    }

    fun searchDevice() {
        model.searchDevice(deviceCallback)
    }

    fun getBrightnessAndOnOff() {
        model.getBrightnessAndOnOff(
            { value -> ledBrightness.postValue(value) },
            { value -> rgbOnOff.postValue(value) })
    }

    fun onOffLight() {
        model.sendLightOnOffCommand { value -> ledBrightness.postValue(value) }
    }

    fun sendLightBrightness(value: Int) {
        model.sendLedBrightness(value) { v -> ledBrightness.postValue(v) }
    }

    fun sendRGBColor(value: Int) {
        model.sendCommand(Command(Command.RGB_ID, 1, value))
    }

    fun sendStarSkyColor(value: Int) {
        model.sendCommand(Command(Command.STAR_SKY_ID, 1, value))
    }

    fun onOffStarSky() {
        model.sendStarSkyOnOffCommand { value -> starSkyOnOff.postValue(value) }
    }

    fun onOffRGB() {
        model.sendRGBOnOffCommand { value -> rgbOnOff.postValue(value) }
    }

    fun openCloseBar(value: Int) {
        model.sendCommand(Command(Command.BAR_ID, 0, value))
    }

    fun sendCurtainCommand(id: Int, value: Int) {
        model.sendCommand(Command(id, 0, value))
    }

    fun sendSeatCommand(value: Int) {
        model.sendCommand(Command(seatPos, seatPart, value))
    }

    fun buttonActionDown() {
        model.buttonActionDown()
    }

    fun buttonActionUp() {
        model.buttonActionUp()
    }

    fun hexColorToInt(hex: String): Int {
        var result = 0
        try {
            result = if (hex[0] == '#') {
                hex.substring(1).toInt(radix = 16)
            } else {
                hex.toInt(radix = 16)
            }
        } catch (_: Exception) {
        }
        return result
    }
}
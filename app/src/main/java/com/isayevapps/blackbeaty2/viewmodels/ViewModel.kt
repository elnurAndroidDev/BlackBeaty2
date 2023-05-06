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

    val lightBrightness = MutableLiveData<Int>()
    val led1OnOff = MutableLiveData<Int>()
    val led2OnOff = MutableLiveData<Int>()
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
            getLightBrightness()
            getLed1OnOff()
            getLed2OnOff()
            getStarSkyOnOff()
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

    fun getLightBrightness() {
        model.getState(
            Command(Command.LIGHT_ID, 10, 0)
        )
        { value -> lightBrightness.postValue(value) }
    }

    fun getLed1OnOff() {
        model.getState(
            Command(Command.RGB_UP_ID, 10, 0)
        )
        { value -> led1OnOff.postValue(value) }
    }

    fun getLed2OnOff() {
        model.getState(
            Command(Command.RGB_DOWN_ID, 10, 0)
        )
        { value -> led2OnOff.postValue(value) }
    }

    fun getStarSkyOnOff() {
        model.getState(
            Command(Command.STAR_SKY_ID, 10, 0)
        )
        { value -> starSkyOnOff.postValue(value) }
    }

    fun sendLightBrightness(value: Int) {
        model.sendCommandWithCallback(
            Command(
                Command.LIGHT_ID,
                1,
                value
            )
        ) { _value -> lightBrightness.postValue(_value) }
    }

    fun sendLed1Brightness(value: Int) {
        model.sendCommandWithCallback(
            Command(
                Command.RGB_UP_ID,
                2,
                value
            )
        ) { _value -> led1OnOff.postValue(_value) }
    }

    fun sendLed2Brightness(value: Int) {
        model.sendCommandWithCallback(
            Command(
                Command.RGB_DOWN_ID,
                2,
                value
            )
        ) { _value -> led2OnOff.postValue(_value) }
    }

    fun sendColor(value: Int) {
        model.sendCommand(Command(Command.COLOR_ID, 0, value))
    }

    fun onOffStarSky() {
        model.sendCommandWithCallback(
            Command(
                Command.STAR_SKY_ID,
                0,
                0
            )
        ) { value -> starSkyOnOff.postValue(value) }
    }

    fun onOffLed1() {
        model.sendCommandWithCallback(Command(Command.RGB_UP_ID, 0, 0)) { value ->
            led1OnOff.postValue(value)
        }
    }

    fun onOffLed2() {
        model.sendCommandWithCallback(Command(Command.RGB_DOWN_ID, 0, 0)) { value ->
            led2OnOff.postValue(value)
        }
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

    fun sendEffect(ledId: Int, op: Int) {
        model.sendCommand(Command(ledId, op, 0))
    }
}
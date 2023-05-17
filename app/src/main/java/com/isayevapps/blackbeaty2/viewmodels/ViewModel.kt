package com.isayevapps.blackbeaty2.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isayevapps.blackbeaty2.callbacks.DeviceCallback
import com.isayevapps.blackbeaty2.callbacks.LightObjectUpdater
import com.isayevapps.blackbeaty2.callbacks.NetworkChangesCallback
import com.isayevapps.blackbeaty2.models.Command
import com.isayevapps.blackbeaty2.models.Model
import kotlin.concurrent.thread

class ViewModel(private val model: Model) {

    private val _states = MutableLiveData<States>()
    val states = _states as LiveData<States>

    val lightOnOff = MutableLiveData<Int>()
    val lightBrightness = MutableLiveData<Int>()
    val led1OnOff = MutableLiveData<Int>()
    val led1Brightness = MutableLiveData<Int>()
    val led1Effect = MutableLiveData<Int>()
    val led1Color = MutableLiveData<Int>()
    val led2OnOff = MutableLiveData<Int>()
    val led2Brightness = MutableLiveData<Int>()
    val led2Effect = MutableLiveData<Int>()
    val led2Color = MutableLiveData<Int>()
    val starSkyOnOff = MutableLiveData<Int>()
    val starSkyColor = MutableLiveData<Int>()

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
            startIsAliveChecking()
            _states.postValue(States.Connected)
        }

        override fun onNotFound() {
            _states.postValue(States.Connection)
        }
    }

    private val led1Updater = object : LightObjectUpdater {
        override fun updateOnOff(value: Int) {
            led1OnOff.postValue(value)
        }

        override fun updateEffect(value: Int) {
            led1Effect.postValue(value)
        }

        override fun updateBrightness(value: Int) {
            led1Brightness.postValue(value)
        }

        override fun updateColor(value: Int) {
            led1Color.postValue(value)
        }

    }

    private val led2Updater = object : LightObjectUpdater {
        override fun updateOnOff(value: Int) {
            led2OnOff.postValue(value)
        }

        override fun updateEffect(value: Int) {
            led2Effect.postValue(value)
        }

        override fun updateBrightness(value: Int) {
            led2Brightness.postValue(value)
        }

        override fun updateColor(value: Int) {
            led2Color.postValue(value)
        }

    }

    private val lightUpdater = object : LightObjectUpdater {
        override fun updateOnOff(value: Int) {
            lightOnOff.postValue(value)
        }

        override fun updateEffect(value: Int) {

        }

        override fun updateBrightness(value: Int) {
            lightBrightness.postValue(value)
        }

        override fun updateColor(value: Int) {
        }

    }

    private val starSkyUpdater = object : LightObjectUpdater {
        override fun updateOnOff(value: Int) {
            starSkyOnOff.postValue(value)
        }

        override fun updateEffect(value: Int) {

        }

        override fun updateBrightness(value: Int) {
        }

        override fun updateColor(value: Int) {
            starSkyColor.postValue(value)
        }

    }

    fun init(activityContext: Context, isInitial: Boolean) {
        if (isInitial) {
            model.registerNetworkChanges(networkChangesCallback)
            model.setDeviceFinder(activityContext, deviceCallback)
            model.registerUpdaters(led1Updater, led2Updater, lightUpdater, starSkyUpdater)
        }
    }

    fun startIsAliveChecking() {
        model.startIsAliveChecking(deviceCallback)
    }

    fun searchDevice() {
        model.searchDevice(deviceCallback)
    }

    fun sendColor(id: Int, value: Int) {
        model.sendCommand(Command(id, 2, value))
    }

    fun sendBrightness(id: Int, value: Int) {
        model.sendCommand(Command(id, 1, value))
    }

    fun sendEffect(id: Int, value: Int) {
        model.sendCommand(Command(id, 3, value))
    }

    fun sendOnOffLight(id: Int) {
        model.sendCommand(Command(id, 0, 0))
    }

    fun sendDoorCommand() {
        thread {
            model.sendCommand(Command(Command.DOOR_ID, 0, 1))
            Thread.sleep(300)
            model.sendCommand(Command(Command.DOOR_ID, 0, 0))
        }
    }

    fun sendBarCommand(value: Int) {
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
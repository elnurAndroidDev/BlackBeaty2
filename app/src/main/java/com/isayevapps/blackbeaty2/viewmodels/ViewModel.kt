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

    private var seatPos = 1
    private var seatPart = 1

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

    fun showHideTV(value: Int) {
        model.sendCommand(Command(Command.TV_ID, 0, value))
    }

    fun onOffLight() {
        model.sendCommand(Command(Command.LIGHT_ID, 0, 0))
    }

    fun sendLightBrightness(value: Int) {
        model.sendCommand(Command(Command.LIGHT_ID, 1, value))
    }

    fun sendRGBBrightness(value: Int) {
        model.sendCommand(Command(Command.RGB_ID, 1, value))
    }

    fun sendRGBColor(value: Int) {
        model.sendCommand(Command(Command.RGB_ID, 2, value))
    }

    fun onOffRGB() {
        model.sendCommand(Command(Command.RGB_ID, 0, 0))
    }

    fun openCloseBar(value: Int) {
        model.sendCommand(Command(Command.BAR_ID, 0, value))
    }

    fun sendCurtainCommand(id: Int, value: Int) {
        model.startLongCommand(id, 0, value)
    }

    fun sendSeatCommand(value: Int) {
        model.startLongCommand(seatPos-1, seatPart-1, value)
    }

    fun stopSendingLongCommand() {
        model.stopSendingLongCommand()
    }

}
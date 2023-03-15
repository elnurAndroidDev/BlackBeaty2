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

    fun setLightBrightness(value: Int) {
        model.sendCommand(Command(Command.LIGHT_ID, 1, value))
    }

    fun setRGBBrightness(value: Int) {
        model.sendCommand(Command(Command.RGB_ID, 1, value))
    }

    fun setRGBColor(value: Int) {
        model.sendCommand(Command(Command.RGB_ID, 2, value))
    }

    fun onOffRGB() {
        model.sendCommand(Command(Command.RGB_ID, 0, 0))
    }

    fun onOffCurtain(pos: Int) {
        model.sendCommand(Command(Command.curtainId(pos), 0, 0))
    }

    fun openCloseBar(value: Int) {
        model.sendCommand(Command(Command.BAR_ID, 0, value))
    }

    fun setSeatCommand(id: Int, op: Int, value: Int) {
        model.setSeatCommand(id, op, value)
    }

    fun stopSendingSeatCommand() {
        model.stopSendingSeatCommand()
    }


}
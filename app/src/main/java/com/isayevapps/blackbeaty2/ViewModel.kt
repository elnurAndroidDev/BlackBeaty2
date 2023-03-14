package com.isayevapps.blackbeaty2

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

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

    fun searchDevice() {
        model.searchDevice()
    }

}
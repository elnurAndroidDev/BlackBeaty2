package com.isayevapps.blackbeaty2.callbacks

interface LightObjectUpdater {
    fun updateOnOff(value: Int)
    fun updateEffect(value: Int)
    fun updateColor(value: Int)
    fun updateBrightness(value: Int)
}
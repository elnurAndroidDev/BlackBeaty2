package com.isayevapps.blackbeaty2.models

import com.google.gson.annotations.SerializedName

data class ServerObject(
    @SerializedName("led")
    val led: List<Int>,
    @SerializedName("ledz")
    val ledz: List<Int>,
    @SerializedName("led1")
    val led1: List<Int>,
    @SerializedName("led2")
    val led2: List<Int>,
    @SerializedName("vib")
    val vib: Int
)
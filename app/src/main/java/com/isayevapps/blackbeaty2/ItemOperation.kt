package com.isayevapps.blackbeaty2

data class ItemOperation(
    private val id: Int,
    private val op: Int,
    private val value: Int
) {
    fun toCode() = id * 255 + op
    fun getValue() = value
}

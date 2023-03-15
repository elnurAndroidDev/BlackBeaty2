package com.isayevapps.blackbeaty2.models

data class Command(
    private val id: Int,
    private val op: Int,
    private val value: Int
) {
    override fun toString() = "(\$code\$:${id * 255 + op},\$value\$:$value)"

    companion object {
        fun curtainId(position: Int) = position + 3
        const val BAR_ID = 8
        const val TV_ID = 10
        const val LIGHT_ID = 11
        const val RGB_ID = 12
    }
}

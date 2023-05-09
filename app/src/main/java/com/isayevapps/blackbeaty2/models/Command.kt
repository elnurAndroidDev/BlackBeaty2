package com.isayevapps.blackbeaty2.models

data class Command(
    private val id: Int,
    private val op: Int,
    private val value: Int
) {
    override fun toString() = "(\$code\$:${id * 255 + op},\$value\$:$value)"

    fun getId() = id
    fun getOp() = op
    fun getValue() = value

    companion object {
        const val BAR_ID = 8
        const val COLOR_ID = 9
        const val LIGHT_ID = 10
        const val LED1_ID = 12
        const val LED2_ID = 13
        const val STAR_SKY_ID = 11
        const val UP = 1
        const val DOWN = 2
        const val RIGHT = 1
        const val LEFT = 2
        const val OPEN_BAR = 1
        const val CLOSE_BAR = 2
        const val OPEN_CURTAIN = 1
        const val CLOSE_CURTAIN = 2
        const val STOP = 0
    }
}

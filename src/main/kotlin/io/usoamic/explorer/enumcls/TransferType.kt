package io.usoamic.explorer.enumcls

enum class TransferType {
    DEPOSIT,
    WITHDRAWAL,
    INTERNAL,
    UNKNOWN;

    fun toPlainString(): String {
        val str = this.toString()
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase()
    }
}
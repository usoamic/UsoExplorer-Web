package io.usoamic.explorer.enumcls

enum class TransactionType {
    TRANSFER,
    BURN;

    fun isTransfer(): Boolean {
        return (this == TRANSFER)
    }

    fun isBurn(): Boolean {
        return (this == BURN)
    }

    fun toPlainString(): String {
        val str = this.toString()
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase()
    }
}
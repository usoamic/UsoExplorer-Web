package io.usoamic.explorer.other

import io.usoamic.web3kt.bignumber.BigNumber
import kotlin.js.Date

class Options(
    var day: String? = "2-digit",
    var hour: String? = "2-digit",
    var month: String? = "2-digit",
    var year: String? = "numeric"
)

class Timestamp private constructor(number: BigNumber) {
    private val _number = BigNumber(number)

    fun toSeconds(): Long {
        return _number.toString().toLong()
    }

    fun toMilliseconds(): Long {
        return (toSeconds()*1000)
    }

    fun toDate(): Date {
        return Date(toMilliseconds())
    }

    fun toLocaleString(): String {


        return toDate().toLocaleString("es")
    }

    companion object {
        fun fromBigNumber(number: BigNumber) = Timestamp(number)
    }
}
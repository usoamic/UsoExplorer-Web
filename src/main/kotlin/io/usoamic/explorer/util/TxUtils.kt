package io.usoamic.explorer.util

import io.usoamic.explorer.enumcls.TransferType

class TxUtils {
    companion object {
        fun getTxType(owner: String, from: String, to: String): TransferType {
            val lcOwner = owner.toLowerCase()
            val lcFrom = from.toLowerCase()
            val lcTo = to.toLowerCase()
            return when {
                ((lcOwner != lcFrom) && (lcOwner == lcTo)) -> TransferType.DEPOSIT
                ((lcOwner == lcFrom) && (lcOwner != lcTo)) -> TransferType.WITHDRAWAL
                ((lcOwner == lcFrom) && (lcOwner == lcTo)) -> TransferType.INTERNAL
                else -> TransferType.UNKNOWN
            }
        }
    }
}
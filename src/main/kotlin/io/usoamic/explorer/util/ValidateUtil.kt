package io.usoamic.explorer.util

import io.usoamic.explorer.exception.ValidateUtilException
import io.usoamic.web3kt.bignumber.BigNumber
import io.usoamic.web3kt.bignumber.BigNumberValue
import io.usoamic.web3kt.util.EthereumUtils

class ValidateUtil {
    companion object {
        fun validatePassword(password: String) = apply {
            validateThatNotEmpty(password, "Password Required")
        }

        fun validateAddress(address: String) = apply {
            validateThatNotEmpty(address, "Address Required")
            if(!EthereumUtils.isValidAddress(address)) {
                throw ValidateUtilException("Invalid Address")
            }
        }

        fun validateTxId(txId: String) = apply {
            validateThatNotEmpty(txId, "TxID Required")
            if(!Regex("0x([A-Fa-f0-9]{64})").containsMatchIn(txId) && (txId.toLongOrNull() == null)) {
                throw ValidateUtilException("Invalid TxHash")
            }
        }

        fun validateId(id: String) = apply {
            val intId = if(id.isNotEmpty()) BigNumber(id) else throw ValidateUtilException("Invalid Id")
            if(intId.isLessThan(BigNumberValue.ZERO)) {
                throw ValidateUtilException("Id must be greater than or equal to zero")
            }
        }

        private fun validateBool(bool: String, key: String) {
            if(bool.isEmpty()) {
                throw ValidateUtilException("$key Required")
            }
            if(bool != "true" && bool != "false") {
                throw ValidateUtilException("$key must be bool")
            }
        }

        private fun validateThatNotEmpty(str: String, message: String) {
            if(str.isEmpty()) {
                throw ValidateUtilException(message)
            }
        }
    }
}
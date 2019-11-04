package io.usoamic.explorer.util

import io.usoamic.web3kt.bignumber.BigNumber
import io.usoamic.web3kt.bignumber.extension.toBigNumber
import io.usoamic.web3kt.core.Web3
import io.usoamic.web3kt.util.EthUnit

class ConvertUtil {
    companion object {
        fun convertEthToWei(wei: BigNumber): BigNumber {
            return Web3.utils.toWei(wei, EthUnit.ETHER).toBigNumber()
        }

        fun convertEthToWei(wei: String): BigNumber {
            return Web3.utils.toWei(wei, EthUnit.ETHER).toBigNumber()
        }

        fun convertWeiToEth(wei: BigNumber): BigNumber {
            return Web3.utils.fromWei(wei, EthUnit.ETHER).toBigNumber()
        }

        fun convertWeiToEth(wei: String): BigNumber {
            return Web3.utils.fromWei(wei, EthUnit.ETHER).toBigNumber()
        }
    }
}
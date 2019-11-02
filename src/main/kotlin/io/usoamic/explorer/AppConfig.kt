package io.usoamic.explorer

import io.usoamic.usoamicktjs.enumcls.NetworkType
import io.usoamic.usoamicktjs.enumcls.NodeProvider
import io.usoamic.usoamicktjs.other.Config

object AppConfig {
    const val ABI = Config.ABI
    const val NUMBER_OF_ADDRESS_TRANSACTIONS = 10L
    const val NUMBER_OF_TRANSACTIONS = 10L

    val NETWORK = NetworkType.TESTNET
    val NODE_PROVIDER = NodeProvider.INFURA
}
package io.usoamic.explorer.base

import io.usoamic.usoamickotlinjs.core.Usoamic
import io.usoamic.usoamickotlinjs.other.Config.Companion.CONTRACT_ABI
import io.usoamic.usoamickotlinjs.other.Config.Companion.NODE
import io.usoamic.web3kt.core.Web3
import io.usoamic.web3kt.core.contract.model.CallOption
import io.usoamic.web3kt.core.extension.newContract

abstract class ExplorerView(application: Application) : View(application) {
    protected val web3 = Web3(NODE)
    private val contract = web3.newContract<Usoamic>(CONTRACT_ABI, "0x42210806DCA8E0C7A5Cff83192852eB0db4ce764")
    protected val methods = contract.methods
    protected val callOption = CallOption("0x5d8766ac0075bdf81b48f0bfcf92449e9def0f37")
}
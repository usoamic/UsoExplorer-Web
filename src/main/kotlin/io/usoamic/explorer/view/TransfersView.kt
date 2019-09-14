package io.usoamic.explorer.view

import io.usoamic.explorer.base.Application
import io.usoamic.explorer.base.ExplorerView
import io.usoamic.web3kt.core.contract.util.Coin
import js.externals.jquery.jQuery

class TransfersView(application: Application) : ExplorerView(application) {
    override val view = jQuery("#add_wallet_view")

    private val summary = jQuery("#summary")
    private val ethHeight = jQuery("#eth_height")
    private val usoSupply = jQuery("#uso_supply")
    private val usoFrozen = jQuery("#uso_frozen")

    init {
        setListeners()
    }

    override fun onStart() {
        super.onStart()
        summary.show()
    }

    override fun onRefresh() {
        super.onRefresh()
        refreshEthHeight()
        refreshUsoSupply()
        refreshUsoFrozen()
    }

    private fun refreshEthHeight() {
        web3.eth.getBlockNumber()
            .then {
                ethHeight.text(it.toString())
            }
            .catch {
                onException(it)
            }
    }

    private fun refreshUsoSupply() {
        methods.getSupply().call(callOption)
            .then {
                usoSupply.text(Coin.fromSat(it).toMillion())
            }
            .catch {
                onException(it)
            }
    }

    private fun refreshUsoFrozen() {
        methods.getFrozen().call(callOption)
            .then {
                usoFrozen.text(if(it) "YES" else "NO")
            }
            .catch {
                onException(it)
            }
    }


    private fun setListeners() {
//        saveBtn.onClick {
//            startLoading()
//            try {
//                val privateKey = privateKeyInput.content().removeHexPrefixIfExist()
//                val password = passwordInput.content()
//                val confirmPassword = confirmPasswordInput.content()
//
//
//
//            } catch (e: Throwable) {
//                saveBtn.stopLoading(false)
//                toastr.error(e.message)
//            }
//        }
    }

    override fun onStop() {
        super.onStop()
        summary.hide()
    }

    companion object {
        private var instance: TransfersView? = null

        fun open(application: Application) {
            if(instance == null) {
                instance = TransfersView(application)
            }
            return application.open(instance!!)
        }
    }
}
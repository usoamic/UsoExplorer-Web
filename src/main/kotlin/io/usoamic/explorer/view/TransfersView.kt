package io.usoamic.explorer.view

import io.usoamic.web3kt.util.extension.removeHexPrefixIfExist
import io.usoamic.explorer.base.Application
import io.usoamic.explorer.base.View
import js.externals.jquery.jQuery
import js.externals.toastr.extensions.error
import js.externals.toastr.toastr
import js.externals.jquery.JQuery
import js.externals.jquery.extension.*
import org.w3c.dom.HTMLElement

class TransfersView(application: Application) : View(application) {
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
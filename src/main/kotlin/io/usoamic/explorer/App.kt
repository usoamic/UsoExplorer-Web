package io.usoamic.explorer

import io.usoamic.explorer.base.Application
import io.usoamic.explorer.base.View
import io.usoamic.explorer.enumcls.Page
import io.usoamic.explorer.view.AccountsView
import io.usoamic.explorer.view.TransfersView
import io.usoamic.web3kt.abi.AbiDecoder
import io.usoamic.web3kt.kt2js.require
import js.externals.jquery.extension.setActive
import js.externals.jquery.jQuery
import js.externals.toastr.toastr
import kotlin.browser.window

class App : Application {
    override lateinit var currentView: View
    private val loader = jQuery(".loader")

    init {
        AbiDecoder.addABI(JSON.parse(AppConfig.ABI))
        startLoading()
        loadDependency()
        setListeners()
    }

    override fun onStart() {
        onHashChange()
        stopLoading()
    }

    override fun startLoading() {}

    override fun stopLoading() {
        loader.delay(0).fadeOut()
    }

    override fun open(view: View) {
        if (::currentView.isInitialized) {
            currentView.onStop()
            currentView.navBarItem?.setActive(false)
        }

        setTitle(view)

        view.navBarItem?.setActive(true)
        currentView = view
        currentView.onStart()
        currentView.onRefresh()
    }

    override fun openPage(page: Page, data: String) {
        window.location.hash = "#${page.name.toLowerCase()}" + if(data.isNotEmpty()) "_$data" else ""
    }

    override fun onError(s: String?) {
        s?.let {
            toastr.error(it)
        }
    }

    override fun onException(t: Throwable) {
        onError(t.message)
    }

    private fun setTitle(view: View) {
        val title = when(view) {
            is TransfersView -> "Transfers"
            is AccountsView -> "Accounts"
            else -> "Unknown"
        }
        window.document.title = "UsoExplorer" + (if(title.isNotEmpty()) " - $title" else "")
    }

    private fun setListeners() {
        window.addEventListener(
            "hashchange",
            {
                onHashChange()
            },
            false
        )
    }

    private fun onHashChange() {
        var findData = ""
        val page = try {
            val hash = window.location.hash
            val hashArg = hash.split("_")
            if(hashArg.size > 1) {
                findData = hashArg[1]
            }
            Page.valueOf(hashArg[0].replace("#", "").toUpperCase())
        } catch (e: IllegalStateException) {
            Page.TRANSFERS
        }

        when (page) {
            Page.TRANSFERS, Page.TRANSFER -> {
                TransfersView.open(this, findData)
            }
            Page.ACCOUNTS, Page.ACCOUNT -> {
                AccountsView.open(this, findData)
            }
        }
    }

    override fun loadDependency() {
        require("datatables.net-bs4")
    }
}


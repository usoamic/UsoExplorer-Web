package io.usoamic.explorer

import io.usoamic.explorer.view.TransfersView
import io.usoamic.web3kt.kt2js.require
import io.usoamic.explorer.base.Application
import io.usoamic.explorer.base.View
import io.usoamic.explorer.enumcls.Page
import io.usoamic.explorer.view.*
import js.externals.jquery.jQuery
import js.externals.toastr.toastr
import kotlin.browser.window

class App : Application {
    override lateinit var currentView: View
    private val loader = jQuery(".loader")

    init {
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
        }

        currentView = view
        currentView.onStart()
        currentView.onRefresh()
    }

    override fun openPage(page: Page) {
        window.location.hash = "#${page.name.toLowerCase()}"
    }

    override fun onError(s: String?) {
        s?.let {
            toastr.error(it)
        }
    }

    override fun onException(t: Throwable) {
        onError(t.message)
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
        val page = try {
            Page.valueOf(window.location.hash.replace("#", "").toUpperCase())
        } catch (e: IllegalStateException) {
            Page.TRANSFERS
        }

        when (page) {
            Page.TRANSFERS -> {
                AccountsView.open(this)
            }
            Page.ACCOUNTS -> {
                TransfersView.open(this)
            }
        }
    }

    override fun loadDependency() {
        require("datatables.net-bs4")
    }
}


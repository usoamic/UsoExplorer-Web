package io.usoamic.explorer.base

import io.usoamic.explorer.enumcls.Page

interface Application {
    var currentView: View
    fun onStart()
    fun startLoading()
    fun stopLoading()
    fun open(view: View)
    fun openPage(page: Page)
    fun onException(t: Throwable)
    fun onError(s: String?)
    fun loadDependency()
}
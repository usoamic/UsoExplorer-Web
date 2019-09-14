package io.usoamic.explorer.base

import js.externals.jquery.JQuery
import js.externals.jquery.extension.clearContent
import js.externals.jquery.extension.clearText
import js.externals.jquery.extension.clearVal
import org.w3c.dom.HTMLElement

abstract class View(protected val application: Application) {
    abstract val view: JQuery<HTMLElement>
    var isFind: Boolean = false
    abstract val input: JQuery<HTMLElement>

    open fun startLoading() {
        application.startLoading()
    }

    open fun onFind(findData: String) {
        input.content(findData)
    }

    open fun stopLoading() {
        application.stopLoading()
    }

    open fun onStart() {
        if(!isFind) {
            input.clearContent()
        }
        view.show()
    }

    open fun onRefresh() { }

    open fun onStop() {
        view.hide()
    }

    protected fun clearInputs(list: List<JQuery<HTMLElement>>) {
        list.forEach {
            it.clearVal()
        }
    }

    protected fun clearTexts(list: List<JQuery<HTMLElement>>) {
        list.forEach {
            it.clearText()
        }
    }

    open fun onError(s: String?) {
        stopLoading()
        application.onError(s)
    }

    open fun onException(t: Throwable) {
        stopLoading()
        application.onException(t)
    }
}
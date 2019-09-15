package io.usoamic.explorer.base

import io.usoamic.usoamickotlinjs.core.Usoamic
import io.usoamic.usoamickotlinjs.other.Config
import io.usoamic.web3kt.core.Web3
import io.usoamic.web3kt.core.contract.model.CallOption
import io.usoamic.web3kt.core.extension.newContract
import js.externals.jquery.JQuery
import js.externals.jquery.extension.*
import org.w3c.dom.HTMLElement

abstract class View(protected val application: Application) {
    abstract val navBarItem: JQuery<HTMLElement>?
    abstract val view: JQuery<HTMLElement>
    var isFind: Boolean = false
    abstract val input: JQuery<HTMLElement>
    abstract val searchBtn: JQuery<HTMLElement>

    protected val web3 = Web3(Config.NODE)
    private val contract = web3.newContract<Usoamic>(Config.CONTRACT_ABI, Config.CONTRACT_ADDRESS)
    protected val methods = contract.methods
    protected val callOption = CallOption("0x8b27fa2987630a1acd8d868ba84b2928de737bc2")

    open fun startLoading() {
        searchBtn.startLoading()
    }

    open fun onFind(findData: String) {
        input.content(findData)
    }

    open fun stopLoading() {
        searchBtn.stopLoading()
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

    protected fun clearTexts(list: List<JQuery<HTMLElement>>, defaultText: String = "") {
        list.forEach {
            it.text(defaultText)
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
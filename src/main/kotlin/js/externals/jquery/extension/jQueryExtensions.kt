package js.externals.jquery.extension

import js.externals.jquery.JQuery
import org.w3c.dom.HTMLElement

fun JQuery<HTMLElement>.onClick(callback: () -> Unit) {
    this.on("click", callback)
}

fun JQuery<HTMLElement>.enable() {
    this.prop("disabled", false)
}

fun JQuery<HTMLElement>.clearVal() {
    this.`val`("")
}

fun JQuery<HTMLElement>.clearContent() {
    clearVal()
}

fun JQuery<HTMLElement>.clearText() {
    this.text("")
}

fun JQuery<HTMLElement>.disable() {
    this.prop("disabled", true)
}

fun JQuery<HTMLElement>.startLoading() {
    this.data("text", this.html())
    this.html("<i class=\"fa fa-spinner fa-spin\"></i> Please wait...")
    this.prop("disabled", true)
}

fun JQuery<HTMLElement>.stopLoading(disable: Boolean = false) {
    val textAny: Any? = this.data("text")
    val text = if(textAny == null) "" else (textAny as String)
    if(text.isNotEmpty()) {
        this.html(text)
    }
    this.prop("disabled", disable)
}

fun JQuery<HTMLElement>.showLoading() {
    this.text("Loading...")
}

fun JQuery<HTMLElement>.addHidden() {
    this.addClass("hidden")
}

fun JQuery<HTMLElement>.removeHidden() {
    this.removeClass("hidden")
}

fun JQuery<HTMLElement>.setActive(active: Boolean) {
    if(active) {
        this.addClass("active")
    }
    else {
        this.removeClass("active")
    }
}
package io.usoamic.explorer.util

class Alert {
    companion object {
        fun show(str: String) {
            js("alert(str);")
        }
    }
}
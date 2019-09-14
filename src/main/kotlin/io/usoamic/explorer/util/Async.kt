package io.usoamic.explorer.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Async {
    companion object {
        fun launch(block: suspend CoroutineScope.() -> Unit) {
            GlobalScope.launch {
                delay(1000)
                block()
            }
        }
    }
}
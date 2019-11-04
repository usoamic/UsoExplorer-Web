package io.usoamic.explorer.util

class CommonUtils {
    companion object {
        fun reduceString(str: String, length: Int): String {
            if(str.length <= length) {
                return str
            }
            val pl = length/2
            val fp = str.substring(0, pl)
            val sp = str.substring(str.length - pl)
            return "$fp...$sp"
        }
    }
}
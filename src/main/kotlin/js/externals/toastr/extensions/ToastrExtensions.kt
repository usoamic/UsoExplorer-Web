package js.externals.toastr.extensions

import js.externals.toastr.toastr

const val UNKNOWN_ERROR = "Unknown error"

inline fun toastr.Companion.error(message: String?) {
    this.error(message ?: UNKNOWN_ERROR)
}

inline fun toastr.Companion.warning(message: String?) {
    this.warning(message ?: UNKNOWN_ERROR)
}
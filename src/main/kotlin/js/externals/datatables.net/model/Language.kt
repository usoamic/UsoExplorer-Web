package js.externals.datatables.net.model

data class Language(
    val loadingRecords: String = "&nbsp;",
    val processing: String = "Loading...",
    val emptyTable: String = "No data available in table"
) {
    companion object {
        fun initEmpty(): Language {
            return Language()
        }
    }
}
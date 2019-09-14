package js.externals.datatables.net.model

data class DataTableOption(
    val data: List<List<Any>>,
    val bDestroy: Boolean = true,
    val processing: Boolean = true,
    val language: Language = Language.initEmpty()
) {
    companion object {
        fun initEmpty(): DataTableOption {
            return DataTableOption(mutableListOf())
        }

        fun initLoading(): DataTableOption {
            return DataTableOption(
                data = mutableListOf(),
                bDestroy = true,
                processing = true,
                language = Language(
                    emptyTable = "Loading..."
                )
            )
        }
    }
}
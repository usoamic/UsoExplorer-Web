package js.externals.datatables.net.extension

import js.externals.datatables.net.JQueryDataTable
import js.externals.datatables.net.model.DataTableOption

fun JQueryDataTable.dataTable(options: DataTableOption) {
    dataTable(JSON.parse(JSON.stringify(options)))
}
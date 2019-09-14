package io.usoamic.explorer.view

import io.usoamic.explorer.base.Application
import io.usoamic.explorer.base.ExplorerView
import io.usoamic.explorer.other.Timestamp
import io.usoamic.explorer.util.CommonUtils
import io.usoamic.web3kt.core.contract.util.Coin
import js.externals.datatables.net.JQueryDataTable
import js.externals.datatables.net.extension.dataTable
import js.externals.datatables.net.model.DataTableOption
import js.externals.jquery.jQuery
import kotlin.math.min

class TransfersView(application: Application) : ExplorerView(application) {
    override val view = jQuery("#add_wallet_view")

    private val summary = jQuery("#summary")
    private val ethHeight = jQuery("#eth_height")
    private val usoSupply = jQuery("#uso_supply")
    private val usoFrozen = jQuery("#uso_frozen")

    private val lastTransfersTable = jQuery("#last_transfers").unsafeCast<JQueryDataTable>()
    private var numberOfLastTransfers: Long = 0

    init {
        prepareLastTransfers()
    }

    override fun onStart() {
        super.onStart()
        summary.show()
    }

    override fun onRefresh() {
        super.onRefresh()
        refreshEthHeight()
        refreshUsoSupply()
        refreshUsoFrozen()
        refreshLastTransfers()
    }

    private fun refreshEthHeight() {
        web3.eth.getBlockNumber()
            .then {
                ethHeight.text(it.toString())
            }
            .catch(this::onException)
    }

    private fun refreshUsoSupply() {
        methods.getSupply().call(callOption)
            .then {
                usoSupply.text(Coin.fromSat(it).toMillion())
            }
            .catch(this::onException)
    }

    private fun refreshUsoFrozen() {
        methods.getFrozen().call(callOption)
            .then {
                usoFrozen.text(if (it) "YES" else "NO")
            }
            .catch(this::onException)
    }

    private fun prepareLastTransfers() {
        lastTransfersTable.dataTable(DataTableOption.initLoading())
    }

    private fun refreshLastTransfers() {
        getTransactions {
            numberOfLastTransfers = it.size.toLong()
            val callback =
            lastTransfersTable.dataTable(DataTableOption(data = it))
        }
    }

    override fun onStop() {
        super.onStop()
        summary.hide()
    }

    private fun getTransactions(callback: (MutableList<List<Any>>) -> Unit) {
        methods.getNumberOfTransactions()
            .call(callOption)
            .then {
                val lastId = min(30, it.toLong())
                if (numberOfLastTransfers == lastId) {
                    return@then
                }
                if (lastId > 0) {
                    iterateTransactions(mutableListOf(),  lastId,0, lastId, callback)
                } else {
                    callback(mutableListOf())
                }
            }
            .catch(this::onException)
    }

    private fun iterateTransactions(
        list: MutableList<List<Any>>,
        id: Long,
        txId: Long,
        lastId: Long,
        callback: (MutableList<List<Any>>) -> Unit
    ) {
        methods.getTransaction(txId.toString()).call(callOption)
            .then { tx ->
                list.add(
                    listOf(
                        "$id",
                        CommonUtils.reduceString(tx.from, 15),
                        CommonUtils.reduceString(tx.to, 15),
                        Coin.fromSat(tx.value).toPlainString(),
                        Timestamp.fromBigNumber(tx.timestamp).toLocaleString()
                    )
                )
                val nextId = (txId + 1)
                if (nextId < lastId) {
                    iterateTransactions(list, (id - 1), nextId, lastId, callback)
                } else {
                    callback(list)
                }
            }
            .catch(this::onException)
    }

    companion object {
        private var instance: TransfersView? = null

        fun open(application: Application) {
            if (instance == null) {
                instance = TransfersView(application)
            }
            return application.open(instance!!)
        }
    }
}
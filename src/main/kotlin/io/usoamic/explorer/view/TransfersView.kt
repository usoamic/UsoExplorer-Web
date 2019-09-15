package io.usoamic.explorer.view

import io.usoamic.explorer.base.Application
import io.usoamic.explorer.base.View
import io.usoamic.explorer.enumcls.Page
import io.usoamic.explorer.enumcls.TransactionType
import io.usoamic.explorer.other.Timestamp
import io.usoamic.explorer.util.Async
import io.usoamic.explorer.util.CommonUtils
import io.usoamic.explorer.util.ValidateUtil
import io.usoamic.web3kt.abi.AbiDecoder
import io.usoamic.web3kt.bignumber.BigNumber
import io.usoamic.web3kt.core.contract.util.Coin
import js.externals.datatables.net.JQueryDataTable
import js.externals.datatables.net.extension.dataTable
import js.externals.datatables.net.model.DataTableOption
import js.externals.jquery.JQuery
import js.externals.jquery.extension.onClick
import js.externals.jquery.jQuery
import js.externals.toastr.toastr
import org.w3c.dom.HTMLElement
import kotlin.math.max

class TransfersView(application: Application) : View(application) {
    override val navBarItem: JQuery<HTMLElement>? = jQuery("#transfers_item")
    override val view = jQuery("#transfers_view")
    override val searchBtn = jQuery("#transfer_search_button")
    override val input = jQuery("#tx_id_input")

    private val ethHeight = jQuery("#eth_height")
    private val usoSupply = jQuery("#uso_supply")
    private val usoFrozen = jQuery("#uso_frozen")

    private val lastTransfersTable = jQuery("#last_transfers").unsafeCast<JQueryDataTable>()
    private var numberOfLastTransfers: Long = 0
    private val lastTransfersBlock = jQuery("#block_last_transfers")
    private val transferDataBlock = jQuery("#block_transfer_data")

    private val fromElement = jQuery("#from_element")
    private val toElement = jQuery("#to_element")
    private val valueElement = jQuery("#value_element")
    private val timestampElement = jQuery("#timestamp_element")

    init {
        setListeners()
        prepareLastTransfers()
    }

    override fun onStart() {
        super.onStart()

        if (!isFind) {
            setVisibility(false)
            clearTexts(listOf(fromElement, toElement, valueElement, timestampElement), "Loading...")
        }
    }

    override fun onRefresh() {
        super.onRefresh()
        refreshEthHeight()
        refreshUsoSupply()
        refreshUsoFrozen()
        refreshLastTransfers()
    }

    override fun onFind(findData: String) {
        super.onFind(findData)
        startLoading()

        try {
            ValidateUtil.validateTxId(findData)

            Async.launch {
                if(findData.toLongOrNull() == null) {
                    findTransferDataByTxHash(findData)
                }
                else {
                    findTransferDataByTxId(findData)
                }
            }
        } catch (e: Throwable) {
            onException(e)
        }
    }

    private fun setListeners() {
        searchBtn.onClick {
            val txId = input.content()
            application.openPage(Page.TRANSFER, txId)
        }
    }

    private fun setVisibility(isShow: Boolean) {
        if(isShow) {
            lastTransfersBlock.hide()
            transferDataBlock.show()
        }
        else {
            lastTransfersBlock.show()
            transferDataBlock.hide()
        }
    }

    private fun findTransferDataByTxHash(txHash: String) {
        web3.eth.getTransactionReceipt(txHash)
            .then { tx ->
                web3.eth.getBlock(tx.blockHash)
                    .then { block ->
                        try {
                            val events = AbiDecoder.decodeLogs(tx.logs)[0].events
                            setTransferData(
                                true,
                                events[0].value as String,
                                events[1].value as String,
                                events[2].value as String,
                                block.timestamp
                            )
                        } catch (t: Throwable) {
                            onException(t)
                        }
                    }
                    .catch(this::onException)
            }
            .catch(this::onException)
    }

    private fun findTransferDataByTxId(txId: String) {
        methods.getTransaction(txId).call(callOption)
            .then {
                setTransferData(it.isExist, it.from, it.to, it.value.toString(), it.timestamp)
            }
            .catch(this::onException)
    }

    private fun setTransferData(isExist: Boolean, from: String, to: String, value: String, timestamp: BigNumber) {
        setVisibility(isExist)
        if(isExist) {
            fromElement.text(from)
            toElement.text(to)
            valueElement.text(Coin.fromSat(value).toPlainString())
            timestampElement.text(Timestamp.fromBigNumber(timestamp).toLocaleString())
        }
        else {
            toastr.error("Transfer not found")
        }
        stopLoading()
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
            lastTransfersTable.dataTable(DataTableOption(data = it))
        }
    }

    private fun getTransactions(callback: (MutableList<List<Any>>) -> Unit) {
        methods.getNumberOfTransactions()
            .call(callOption)
            .then {
                val lastId = it.toLong() - 1
                if (numberOfLastTransfers == lastId) {
                    return@then
                }
                if (lastId > 0) {
                    iterateTransactions(mutableListOf(), 1, lastId, max(lastId - 10, 0), callback)
                } else {
                    callback(mutableListOf())
                }
            }
            .catch(this::onException)
    }

    private fun iterateTransactions(
        list: MutableList<List<Any>>,
        index: Long,
        txId: Long,
        lastId: Long,
        callback: (MutableList<List<Any>>) -> Unit
    ) {
        methods.getTransaction(txId.toString()).call(callOption)
            .then { tx ->
                val type = if(tx.to == "0x0000000000000000000000000000000000000000") TransactionType.BURN else TransactionType.TRANSFER
                list.add(
                    listOf(
                        "$index",
                        type.toPlainString(),
                        CommonUtils.reduceString(tx.from, 15),
                        if (type.isTransfer()) CommonUtils.reduceString(tx.to, 15) else "N/A",
                        Coin.fromSat(tx.value).toPlainString(),
                        Timestamp.fromBigNumber(tx.timestamp).toLocaleString()
                    )
                )
                val nextId = (txId - 1)
                if (nextId >= lastId) {
                    iterateTransactions(list, (index + 1), nextId, lastId, callback)
                } else {
                    callback(list)
                }
            }
            .catch(this::onException)
    }

    companion object {
        private var instance: TransfersView? = null

        fun open(application: Application, findData: String) {
            if (instance == null) {
                instance = TransfersView(application)
            }
            val isFind = findData.isNotEmpty()
            if(isFind) {
                instance?.onFind(findData)
            }
            instance?.isFind = isFind
            return application.open(instance!!)
        }
    }
}
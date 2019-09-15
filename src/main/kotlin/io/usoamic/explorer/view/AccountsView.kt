package io.usoamic.explorer.view

import io.usoamic.explorer.base.Application
import io.usoamic.explorer.base.View
import io.usoamic.explorer.enumcls.Page
import io.usoamic.explorer.enumcls.TransferType
import io.usoamic.explorer.other.Timestamp
import io.usoamic.explorer.util.Async
import io.usoamic.explorer.util.ConvertUtil
import io.usoamic.explorer.util.TxUtils
import io.usoamic.explorer.util.ValidateUtil
import io.usoamic.usoamickotlinjs.core.extension.getTransactionsByAddress
import io.usoamic.usoamickotlinjs.model.Transfer
import io.usoamic.web3kt.bignumber.BigNumber
import io.usoamic.web3kt.core.contract.util.Coin
import io.usoamic.web3kt.tx.block.DefaultBlockParameterName
import js.externals.datatables.net.JQueryDataTable
import js.externals.datatables.net.extension.dataTable
import js.externals.datatables.net.model.DataTableOption
import js.externals.jquery.JQuery
import js.externals.jquery.extension.onClick
import js.externals.jquery.jQuery
import org.w3c.dom.HTMLElement

class AccountsView(application: Application) : View(application) {
    override val navBarItem: JQuery<HTMLElement>? = jQuery("#accounts_item")
    override val view = jQuery("#accounts_view")

    override val input: JQuery<HTMLElement> = jQuery("#account_address_input")
    override val searchBtn: JQuery<HTMLElement> = jQuery("#account_search_button")

    private val accountDataBlock = jQuery("#block_account_data")

    private val ethBalanceElement = jQuery("#eth_balance_element")
    private val usoBalanceElement = jQuery("#uso_balance_element")
    private val numberOfTxElement = jQuery("#number_of_tx_element")
    private val accountTransfersBlock = jQuery("#block_account_transfers")

    private val accountTransfersTable = jQuery("#account_transfers").unsafeCast<JQueryDataTable>()

    private var numberOfTransfers: Long = 0

    init {
        setListeners()
    }

    private fun setListeners() {
        searchBtn.onClick {
            val address = input.content()
            application.openPage(Page.ACCOUNT, address)
        }
    }

    override fun onFind(findData: String) {
        super.onFind(findData)
        startLoading()
        try {
            ValidateUtil.validateAddress(findData)
            Async.launch {
                findAccount(findData)
            }
        } catch (t: Throwable) {
            onException(t)
        }
    }

    private fun findAccount(address: String) {
        methods.balanceOf(address).call(callOption)
            .then { usoBalance ->
                methods.getNumberOfTransactionsByAddress(address).call(callOption)
                    .then { numberOfTx ->
                        web3.eth.getBalance(address, DefaultBlockParameterName.LATEST)
                            .then { ethBalance ->
                                getTransactions(address, 500, numberOfTransfers) { transfers ->
                                    numberOfTransfers = transfers.size.toLong()
                                    setAccountData(transfers, ethBalance, usoBalance, numberOfTx)
                                }
                            }
                            .catch(this::onException)
                    }
                    .catch(this::onException)
            }
            .catch(this::onException)
    }

    private fun setAccountData(transfers: List<List<Any>>, ethBalance: BigNumber, usoBalance: BigNumber, numberOfTx: String) {
        accountDataBlock.show()
        accountTransfersBlock.show()
        accountTransfersTable.dataTable(DataTableOption(data = transfers))
        ethBalanceElement.text(ConvertUtil.convertWeiToEth(ethBalance).toString())

        usoBalanceElement.text(Coin.Companion.fromSat(usoBalance).toPlainString())
        numberOfTxElement.text(numberOfTx)
        stopLoading()
    }

    private fun getTransactions(address: String, lastId: Long?, loadedLastId: Long, callback: (List<List<Any>>) -> Unit) {
        lastId?.let { lId ->
            methods.getTransactionsByAddress(address, lId, loadedLastId) { list: MutableList<Transfer>, throwable: Throwable?, hasUpdate: Boolean ->
                if (!hasUpdate) {
                    return@getTransactionsByAddress
                }
                if (throwable != null) {
                    onException(throwable)
                    return@getTransactionsByAddress
                }
                val txList = mutableListOf<List<Any>>()
                var id = list.size

                list.forEach { tx ->
                    val txType = TxUtils.getTxType(address, tx.from, tx.to)
                    txList.add(
                        listOf(
                            id,
                            txType.toPlainString(),
                            when (txType) {
                                TransferType.DEPOSIT -> tx.from
                                TransferType.WITHDRAWAL -> tx.to
                                TransferType.INTERNAL -> address
                                TransferType.BURN, TransferType.UNKNOWN -> "N/A"
                            },
                            Coin.fromSat(tx.value).toPlainString(),
                            Timestamp.fromBigNumber(tx.timestamp).toLocaleString()
                        )
                    )
                    id--
                }
                callback(txList)
            }
        }
    }

    companion object {
        private var instance: AccountsView? = null

        fun open(application: Application, findData: String) {
            if (instance == null) {
                instance = AccountsView(application)
            }
            val isFind = findData.isNotEmpty()
            if (isFind) {
                instance?.onFind(findData)
            }
            instance?.isFind = isFind
            return application.open(instance!!)
        }
    }
}
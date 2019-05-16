package com.example.luis.btcsender

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.example.luis.btcsender.Adapter.TransactionsAdapter
import com.example.luis.btcsender.Retrofit.IMyAPI
import com.example.luis.btcsender.Retrofit.RetrofitClient
import com.example.luis.btcsender.model.RipioPojo
import com.example.luis.btcsender.model.Transaction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity(), SendBtcDialog.SendBtcDialogListener {

    private lateinit var myAPI: IMyAPI
    private var compositeDisposable = CompositeDisposable()
    private lateinit var ratesRequest: RipioPojo
    private var btcBalance: Double? = null
    private var transactions = ArrayList<Transaction>()
    private lateinit var recyclerViewTransactions: RecyclerView
    private lateinit var adapterTransactions: TransactionsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btcBalance = getBtcBalance()

        txtBtcBalance.text = String.format("%.8f", btcBalance)

        //init API
        val retrofit = RetrofitClient.getRipioInstance()
        myAPI = retrofit.create(IMyAPI::class.java)

        showBtcBalanceInLocalCurrency()

        fab.setOnClickListener { v -> openDialog() }
    }

    private fun showBtcBalanceInLocalCurrency() {
        compositeDisposable.add(myAPI.getRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ requestPojo ->
                    this.ratesRequest = requestPojo
                    txtArsBalance.text = "~ ${(btcBalance!! * requestPojo.rates!!.ARS_SELL!!).toInt()} ARS"
                }, { throwable -> Toast.makeText(this@MainActivity, throwable.message, Toast.LENGTH_LONG).show() }))
    }

    private fun getBtcBalance(): Double {
        return 1.toDouble()
    }

    private fun openDialog() {
        val args = Bundle()
        args.putDouble("btc_balance", btcBalance!!)
        val sendBtcDialog = SendBtcDialog()
        sendBtcDialog.arguments = args
        sendBtcDialog.show(supportFragmentManager, "dialog")
    }

    override fun updateTransfersList(transaction: Transaction) {
        transactions.add(transaction)
        btcBalance = btcBalance!!.minus(transaction.btc).minus(transaction.fee)
        txtBtcBalance.text = String.format("%.8f", btcBalance)
        showBtcBalanceInLocalCurrency()
        if (transactions.size == 1) {
            recyclerViewTransactions = recyclerView
            recyclerViewTransactions.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            adapterTransactions = TransactionsAdapter(this@MainActivity, transactions)
            recyclerViewTransactions.adapter = adapterTransactions
        } else {
            adapterTransactions.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}

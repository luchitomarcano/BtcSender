package com.example.luis.btcsender

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.example.luis.btcsender.Retrofit.IMyAPI
import com.example.luis.btcsender.Retrofit.RetrofitClient
import com.example.luis.btcsender.model.Transaction

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class SendBtcDialog : AppCompatDialogFragment() {
    private var listener: SendBtcDialogListener? = null
    private lateinit var myAPI: IMyAPI
    private var compositeDisposable = CompositeDisposable()
    private var alertDialog: AlertDialog? = null
    private var btcBalance: Double = 0.0
    private var transactionBtc: Double = 0.0
    private var transactionFee: Double = 0.0
    private lateinit var transactionDate: String
    private lateinit var transactionAddress: String
    private lateinit var transactionId: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val args = arguments
        btcBalance = args!!.getDouble("btc_balance")

        val builder = AlertDialog.Builder(activity!!)

        val inflater = activity!!.layoutInflater
        val dialog = inflater.inflate(R.layout.dialog_send_btc, null)

        //init API
        val retrofit = RetrofitClient.getFeeInstance()
        myAPI = retrofit.create(IMyAPI::class.java)

        builder.setView(dialog)
                .setTitle("Enviar Bitcoin")
                .setNegativeButton("Cancelar") { dialogInterface, i -> }
                .setPositiveButton("Enviar") { dialogInterface, i ->                }
        alertDialog = builder.create()
        return alertDialog!!
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            listener = context as SendBtcDialogListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(context!!.toString() + "must implement ExampleDialogListener")
        }
    }

    override fun onResume() {
        super.onResume()

        var feeInBtc: Double = 0.0
        var feeIsReady: Boolean = false

        alertDialog!!.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            if (feeIsReady) {
                transactionBtc = (alertDialog!!.findViewById<EditText>(R.id.editTextBtcAmount)!!.text.toString()).toDouble()
                transactionFee = (alertDialog!!.findViewById<TextView>(R.id.textViewFee)!!.text.toString().replace(",", ".")).toDouble()
                transactionDate = getCurrentDate()
                transactionAddress = (alertDialog!!.findViewById<EditText>(R.id.editTextBtcAddress)!!.text.toString())
                transactionId = UUID.randomUUID().toString().substring(0, 8)

                if (transactionBtc > 0 && (transactionBtc + feeInBtc) < btcBalance && isBtcAddressValid(transactionAddress)) {
                    val transaction = Transaction(transactionBtc, transactionFee, transactionDate, transactionAddress, transactionId)
                    listener!!.updateTransfersList(transaction)
                    alertDialog!!.dismiss()
                } else {
                    Toast.makeText(activity, "Por favor revise los datos", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(activity, "No ha sido calculada la comisión de red", Toast.LENGTH_LONG).show()
            }
        }

        compositeDisposable.add(myAPI.getFee()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ fee ->
                    feeIsReady = true
                    feeInBtc = (fee.halfHourFee!! * 225) / (100000000).toString().replace(",", ".").toDouble()
                    alertDialog!!.findViewById<TextView>(R.id.textViewFee)!!.text = String.format("%.8f", feeInBtc)
                }, { throwable -> Toast.makeText(activity, throwable.localizedMessage, Toast.LENGTH_LONG).show() }))
    }

    private fun getCurrentDate(): String {
        val c = Calendar.getInstance().getTime()

        val df = SimpleDateFormat("dd-MMM-yyyy")
        return df.format(c)

    }

    interface SendBtcDialogListener {
        fun updateTransfersList(transaction: Transaction)
    }

    fun isBtcAddressValid(btcAddress: String): Boolean {
        //validación simple para efectos de la prueba
        if (btcAddress.length in 26..35)
            return true
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}



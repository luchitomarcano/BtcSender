package com.example.luis.btcsender.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.luis.btcsender.R
import com.example.luis.btcsender.model.Transaction

class TransactionsAdapter(private val mContext: Context, private val mTransactions: List<Transaction>) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.btc.text = mTransactions[position].btc.toString() + " BTC"
        holder.address.text = "To: " + mTransactions[position].address.toString()
        holder.date.text = mTransactions[position].date.toString()
        holder.id.text = mTransactions[position].id.toString()
    }

    override fun getItemCount(): Int {
        return mTransactions.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var btc = itemView.findViewById(R.id.textViewItemBtc) as TextView
        internal var address = itemView.findViewById(R.id.textViewItemAddress) as TextView
        internal var date = itemView.findViewById(R.id.textViewItemDate) as TextView
        internal var id = itemView.findViewById(R.id.textViewItemId) as TextView
    }
}
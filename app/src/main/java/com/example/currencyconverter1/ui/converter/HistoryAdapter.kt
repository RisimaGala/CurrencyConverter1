package com.example.currencyconverter1.ui.converter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter1.R
import com.example.currencyconverter1.data.entity.ConversionHistory
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter : ListAdapter<ConversionHistory, HistoryAdapter.HistoryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversion_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val conversion = getItem(position)
        holder.bind(conversion)
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fromCurrencyText: TextView = itemView.findViewById(R.id.fromCurrencyText)
        private val toCurrencyText: TextView = itemView.findViewById(R.id.toCurrencyText)
        private val amountText: TextView = itemView.findViewById(R.id.amountText)
        private val convertedAmountText: TextView = itemView.findViewById(R.id.convertedAmountText)
        private val rateText: TextView = itemView.findViewById(R.id.rateText)
        private val timestampText: TextView = itemView.findViewById(R.id.timestampText)

        fun bind(conversion: ConversionHistory) {
            fromCurrencyText.text = conversion.fromCurrency
            toCurrencyText.text = conversion.toCurrency
            amountText.text = String.format("%.2f %s", conversion.amount, conversion.fromCurrency)
            convertedAmountText.text = String.format("%.2f %s", conversion.convertedAmount, conversion.toCurrency)
            rateText.text = String.format("1 %s = %.4f %s", conversion.fromCurrency, conversion.rate, conversion.toCurrency)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            timestampText.text = dateFormat.format(Date(conversion.timestamp))
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ConversionHistory>() {
        override fun areItemsTheSame(oldItem: ConversionHistory, newItem: ConversionHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ConversionHistory, newItem: ConversionHistory): Boolean {
            return oldItem == newItem
        }
    }
}
package com.example.batmanhood.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.batmanhood.R
import com.example.batmanhood.models.HistoricalPrices
import com.example.batmanhood.models.RealTimeStockQuote
import com.robinhood.spark.SparkView
import timber.log.Timber

class AssetRecyclerViewAdapter(
    private val realTimeStockData: LinkedHashMap<String,RealTimeStockQuote>,
    private val oneDayHistoricalData: LinkedHashMap<String,List<HistoricalPrices>>,
    private val userStockListOrdered :  MutableList<String>,
    private val onAssetListener: OnAssetListener) :
        RecyclerView.Adapter<AssetRecyclerViewAdapter.ViewHolder>(){


    class ViewHolder(view: View, private val onAssetListener: OnAssetListener) :
            RecyclerView.ViewHolder(view),
            View.OnClickListener
    {
        init {
            view.setOnClickListener(this)
        }

        val assetSymbol : TextView = view.findViewById(R.id.asset_symbol)
        val assetName : TextView = view.findViewById(R.id.asset_name)
        val assetPrice : TextView = view.findViewById(R.id.asset_price)
        val sparkyView : SparkView = view.findViewById(R.id.sparkview)
        val percentageTextView : TextView = view.findViewById(R.id.percentage_change_from_open)

        override fun onClick(v: View?) {
            Timber.d("adapterPosition in onClick for ViewHolder => $adapterPosition")
            onAssetListener.onAssetClick(v,adapterPosition)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.asset_recycler_view_row,parent,false)
        return ViewHolder(view,onAssetListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filteredList : MutableList<Float> = mutableListOf()
        userStockListOrdered.get(position).forEach {
            oneDayHistoricalData.get(it.toString())?.forEach {
                it.close?.let { it1 -> filteredList.add(it1) } } }
       // oneDayHistoricalData.get(position). { it.close?.let { it1 -> filteredList.add(it1) } }
        val rtQuoteAtPosition = realTimeStockData.get(realTimeStockData.keys.toList()[position])
        Log.e("SPARKY_LIST","Stock => ${rtQuoteAtPosition?.symbol.toString()} ==> ${filteredList.toString()}")
        val randomSparkAdapter = TestSparkAdapter(filteredList)
        val isStockHigherThanAtOpen  = filteredList.last() >= filteredList.first()

        holder.sparkyView.apply {
            adapter = randomSparkAdapter
            if(isStockHigherThanAtOpen) {
                lineColor = ContextCompat.getColor(context,R.color.batman_yellow)
            } else {
                lineColor = ContextCompat.getColor(context,R.color.color_for_stock_below_open)
            }

        }

        holder.percentageTextView.apply {
            text = calculatePercentageChange( filteredList.first(), filteredList.last()).toString()
            if(isStockHigherThanAtOpen) {
                setTextColor(ContextCompat.getColor(context,R.color.batman_yellow))
            }
            else {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
            }
        }

        if (rtQuoteAtPosition != null) {
            holder.assetSymbol.text = rtQuoteAtPosition.symbol.toString()
        }
        if (rtQuoteAtPosition != null) {
            holder.assetPrice.text =  rtQuoteAtPosition.latestPrice.toString()
        }
        if (rtQuoteAtPosition != null) {
            holder.assetName.text = rtQuoteAtPosition.companyName.toString()
        }
    }

    override fun getItemCount(): Int = realTimeStockData.size

    private fun calculatePercentageChange(openPrice : Float, latestPrice : Float) : Float  {
        return when {
            openPrice == latestPrice -> 0.0f
            openPrice > latestPrice -> {
                (((latestPrice-openPrice)/latestPrice)*100)
            }
            else -> {
                (((openPrice-latestPrice)/latestPrice)*100)
            }
        }
    }

    interface OnAssetListener {
        fun onAssetClick(view : View?,position : Int)
    }

}
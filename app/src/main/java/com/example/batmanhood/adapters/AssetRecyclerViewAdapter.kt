package com.example.batmanhood.adapters

import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.batmanhood.R
import com.example.batmanhood.models.HistoricalPrices
import com.example.batmanhood.models.RealTimeStockQuote
import com.example.batmanhood.utils.calculatePercentageDifference
import com.example.batmanhood.utils.daySoFarTradingRatio
import com.example.batmanhood.utils.onLayout
import com.example.batmanhood.viewModels.UserProfileViewModel
import com.robinhood.spark.SparkView
import kotlinx.android.synthetic.main.asset_recycler_view_row.view.*
import timber.log.Timber

class AssetRecyclerViewAdapter(
    private val realTimeStockData: LinkedHashMap<String,RealTimeStockQuote>,
    private val oneDayHistoricalData: LinkedHashMap<String,MutableList<HistoricalPrices>>,
    private val userStockListOrdered :  MutableList<String>,
    private val onAssetListener: OnAssetListener) :
        RecyclerView.Adapter<AssetRecyclerViewAdapter.ViewHolder>(){


    inner class ViewHolder(view: View, private val onAssetListener: OnAssetListener) :
            RecyclerView.ViewHolder(view),
            View.OnClickListener,
            View.OnLongClickListener
    {
        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        val assetSymbol : TextView = view.findViewById(R.id.asset_symbol)
        val assetName : TextView = view.findViewById(R.id.asset_name)
        val assetPrice : TextView = view.findViewById(R.id.asset_price)
        val sparkyView : SparkView = view.findViewById(R.id.sparkview)
        val percentageTextView : TextView = view.findViewById(R.id.percentage_change_from_open)

        override fun onClick(v: View?) {
            Timber.e("adapterPosition in onClick for ViewHolder => $adapterPosition")
            onAssetListener.onAssetClick(v,adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            Timber.e("adapterPosition in LongClick for ViewHolder => $adapterPosition")
            Timber.e("item => ${userStockListOrdered[adapterPosition]}")
            removeItem(userStockListOrdered[adapterPosition], adapterPosition)
            return true
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filteredList : MutableList<Float> = mutableListOf()
        val stockSymbolAtPosition = userStockListOrdered.get(position)
        oneDayHistoricalData[stockSymbolAtPosition]?.forEach {
                it.close?.let { it1 -> filteredList.add(it1) } }
        Timber.e("StockSymbolAtPosition => $stockSymbolAtPosition")
       // oneDayHistoricalData.get(position). { it.close?.let { it1 -> filteredList.add(it1) } }
        val rtQuoteAtPosition = realTimeStockData[stockSymbolAtPosition]
        val randomSparkAdapter = TestSparkAdapter(filteredList)
        val isStockHigherThanAtOpen  = filteredList.last() >= filteredList.first()

        holder.sparkyView.onLayout {
            holder.sparkyView.apply {
                val daySoFarRatio : Double  = daySoFarTradingRatio()
                if(daySoFarRatio >=  0) {
                    val rightPadding = width - (width * daySoFarRatio)
                    setPadding(0,0,rightPadding.toInt(),0)
                }
            }
        }

        holder.sparkyView.apply {
            adapter = randomSparkAdapter
            if(isStockHigherThanAtOpen) {
                lineColor = ContextCompat.getColor(context,R.color.color_for_stock_above_open)
            } else {
                lineColor = ContextCompat.getColor(context,R.color.color_for_stock_below_open)
            }
            var baseLinePaint : Paint = sparkview.baseLinePaint
            var dashpathEffect  = DashPathEffect(floatArrayOf(10.0F, 10.0F),0F)
            baseLinePaint.pathEffect = dashpathEffect
            baseLineColor = ContextCompat.getColor(context,R.color.white)
        }

        holder.percentageTextView.apply {
            text = "${calculatePercentageDifference( filteredList.first(), filteredList.last()).toString()}%"
            if(isStockHigherThanAtOpen) {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
            }
            else {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
            }
        }

        if (rtQuoteAtPosition != null) {
            holder.assetSymbol.text = rtQuoteAtPosition.symbol.toString()
        }
        if (rtQuoteAtPosition != null) {
            holder.assetPrice.text =  "\$${rtQuoteAtPosition.latestPrice.toString()}"
        }
        if (rtQuoteAtPosition != null) {
            holder.assetName.text = rtQuoteAtPosition.companyName.toString()
        }
    }


    fun removeItem(symbol : String, position: Int) {
        realTimeStockData.remove(symbol)
        userStockListOrdered.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(
        newStockRealData : LinkedHashMap<String, RealTimeStockQuote>,
        newStockHistoricaldata : LinkedHashMap<String, MutableList<HistoricalPrices>>
    ) {

    }

    override fun getItemCount(): Int = realTimeStockData.size


    interface OnAssetListener {
        fun onAssetClick(view : View?,position : Int)
        fun onLongAssetClick(view: View?, position : Int)
    }

}
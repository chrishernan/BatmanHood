package com.example.batmanhood.adapters

import com.robinhood.spark.SparkAdapter
import java.util.*

class TestSparkAdapter(listOfHistoricalPrices : List<Float>) : SparkAdapter() {
    private val priceData = listOfHistoricalPrices

    override fun getCount(): Int {
        return priceData.size
    }

    override fun getItem(index: Int): Any {
        return priceData[index]
    }

    override fun getY(index: Int): Float {
        return priceData[index]
    }

    override fun hasBaseLine(): Boolean {
        return true
    }
    override fun getBaseLine(): Float {
        return priceData.average().toFloat()

    }

}
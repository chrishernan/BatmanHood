package com.example.batmanhood.models


//S5KAGMW8MQA3H6VE advantage api key
data class StockInformation (
    val symbol : String,
    var name : String,
    var currentPrice: String,
    var marketValue: String,
    var openPrice: Float,
    var todaysHigh: Float,
    var todaysLow: Float,
    var fiftyTwoWeekHigh: Float,
    var fiftyTwoWeekLow: Float,
    var MarketCap: Float,
    var averageVolume: Float,
    var Volume: Float
)
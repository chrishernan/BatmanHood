package com.example.batmanhood.IO

import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject


class StockAndIndexApiHelper @Inject constructor(private val iexApiService: iexApiService){
     suspend fun getRealTimeStockQuote(
             stockSymbol: String,
             field: String,
             apiToken: String) = iexApiService
                .getRealTimeStockQuote(stockSymbol,field,apiToken)

     suspend fun getRealTimeStockQuoteAllFields(
             stockSymbol: String,
             apiToken: String) = iexApiService.getRealTimeStockQuoteAllFields(stockSymbol,apiToken)

     suspend fun getDelayedStockQuote(
             stockSymbol: String,
             apiToken: String) = iexApiService
                .getDelayedStockQuote(stockSymbol,apiToken)

     suspend fun getHistoricalStockPrices(
             stockSymbol: String,
             priceRangeOfPrices: String,
             todaysDate: String,
             apiToken: String) = iexApiService
                .getHistoricalStockPrices(stockSymbol,priceRangeOfPrices,todaysDate,apiToken)

     suspend fun getIntradayStockPrices(
             stockSymbol: String,
             apiToken: String) = iexApiService
                .getIntradayStockPrices(stockSymbol,apiToken)

     suspend fun getOtherStockInformation(
             stockSymbol: String,
             apiToken: String) = iexApiService
                .getOtherStockInformation(stockSymbol,apiToken)

     suspend fun getETFInformation(
             etfSymbol: String,
             apiToken: String) = iexApiService
                .getETFInformation(etfSymbol,apiToken)
}
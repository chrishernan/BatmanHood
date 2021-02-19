package com.example.batmanhood.IO

import com.example.batmanhood.models.HistoricalPrices
import com.example.batmanhood.models.RealTimeStockQuote
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject


class StockAndIndexApiHelper @Inject constructor(private val iexApiService: iexApiService){

     suspend fun getRealTimeStockQuote(
             stockSymbol: String,
             field: String,
             apiToken: String)= iexApiService
                .getRealTimeStockQuote(stockSymbol,field,apiToken)

     suspend fun getRealTimeStockQuoteAllFields(
             stockSymbol: String,
             apiToken: String) = iexApiService.getRealTimeStockQuoteAllFields(stockSymbol,apiToken)

     suspend fun getMultipleStockQuotes(
             symbols : String,
             types: String,
             apiToken: String) : HashMap<String,HashMap<String,RealTimeStockQuote>>
     = iexApiService.getMultipleStockQuotes(symbols, types, apiToken)

     suspend fun getDelayedStockQuote(
             stockSymbol: String,
             apiToken: String) = iexApiService
                .getDelayedStockQuote(stockSymbol,apiToken)

     suspend fun getHistoricalStockPrices(
             stockSymbol: String,
             fieldFilter : String,
             rangeOfDays: String,
             chartSimplify : String,
             apiToken: String) : List<HistoricalPrices>
        = iexApiService.getHistoricalStockPrices(stockSymbol,fieldFilter,rangeOfDays,chartSimplify,apiToken)

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

    suspend fun getAllUSCompanies(
        apiToken: String) =iexApiService.getAllUSCompanies(apiToken)

}
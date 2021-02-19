package com.example.batmanhood.IO

import com.example.batmanhood.models.AutofillCompany
import com.example.batmanhood.models.HistoricalPrices
import com.example.batmanhood.models.RealTimeStockQuote
import com.example.batmanhood.utils.Constants
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface iexApiService {
    /**
     *  add latestPrice to field field so that you don't get the huge json with all information
     *  todo probably add 52 week average,etc so that we can try and get as much info in one request
     *  todo or maybe just get everything and filter if that's not possible
     */
    @GET("stable/stock/{symbol}/quote/{field}")
    suspend fun getRealTimeStockQuote(
            @Path("symbol") stockSymbol: String,
            @Path("field") field: String,
            @Query("token") apiToken: String)

    @GET("stable/stock/{symbol}/quote")
    suspend fun getRealTimeStockQuoteAllFields(
            @Path("symbol") stockSymbol: String,
            @Query("token") apiToken: String) : RealTimeStockQuote

    @GET ("stable/stock/market/batch")
    suspend fun getMultipleStockQuotes(

            @Query("symbols") symbols : String,
            @Query("types") types: String,
            @Query("token") apiToken: String) : HashMap<String,HashMap<String,RealTimeStockQuote>>

    /**FTD
     * This gives a 15 minute price. Not sure what it can be used for yet
     */
    @GET("stable/stock/{symbol}/delayed-quote")
    suspend fun getDelayedStockQuote(
            @Path("symbol") stockSymbol: String,
            @Query("token") apiToken: String)

    /**
     *     This is what will be used to build the charts. Will be used for ranges that are bigger than one day
     *     TODO Range has it's own notation, will use an enum or something like it to set values for
     *     1 hour range, 1 day, 1 week, 1 month, 3 months, 6 months, 1 year, 2 years, 3 years, 5 years,
     *     10 years, MAX
     */
    @GET("stable/stock/{symbol}/chart/")
    suspend fun getHistoricalStockPrices(
            @Path("symbol") stockSymbol: String,
            @Query("filter") filterOfFields : String,
            @Query("range") rangeOfDays: String,
            @Query("chartSimplify") chartSimplify : String,
            @Query("token") apiToken: String) : List<HistoricalPrices>

    /**
     *  This is the method that will be used to get that day's data points
     *  It uses intraday endpoint.
     */

    @GET("stable/stock/{symbol}/intraday-prices")
    suspend fun getIntradayStockPrices(
            @Path("symbol") stockSymbol: String,
            @Query("token") apiToken: String
    )

    /**
     * Similar to getRealTimeStockQuote, but instead of only getting one field, it will get everything
     */
    @GET("stable/stock/{symbol}/quote/")
    suspend fun getOtherStockInformation(
            @Path("symbol") stockSymbol: String,
            @Query("token") apiToken: String
    )

    /**
     * This method will be used to retrieve ETF information that correlates to index performance.
     * Due to cost of retrieving actual index performance data, we will use ETF which are free.
     */
    @GET("stable/stock/{symbol}/company")
    suspend fun getETFInformation(
            @Path("symbol") etfSymbol: String,
            @Query("token") apiToken: String
    )

    @GET("beta/ref-data/symbols")
    suspend fun getAllUSCompanies(
        @Query("token") apiToken: String
    ) : MutableList<AutofillCompany>

    //static implementation of create() method
    companion object{
        fun create() : iexApiService {
            val logging  = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
            httpClient.addInterceptor(logging);

            val retrofit= Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.IEX_BASE_URL)
                .client(httpClient.build())
                .build()

            return retrofit.create(iexApiService::class.java)
        }
    }

}
package com.example.batmanhood.IO

import com.example.batmanhood.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface iexApiService {
    /**
     *  add latestPrice to field field so that you don't get the huge json with all information
     *  todo probably add 52 week average,etc so that we can try and get as much info in one request
     *  todo or maybe just get everything and filter if that's not possible
     */
    @GET("stock/{symbol}/quote/{field}")
    suspend fun getRealTimeStockQuote(
            @Path("symbol") stockSymbol: String,
            @Path("field") field: String,
            @Query("token") apiToken: String)

    @GET("stock/{symbol}/quote")
    suspend fun getRealTimeStockQuoteAllFields(
            @Path("symbol") stockSymbol: String,
            @Query("token") apiToken: String)

    /**FTD
     * This gives a 15 minute price. Not sure what it can be used for yet
     */
    @GET("stock/{symbol}/delayed-quote")
    suspend fun getDelayedStockQuote(
            @Path("symbol") stockSymbol: String,
            @Query("token") apiToken: String)

    /**
     *     This is what will be used to build the charts. Will be used for ranges that are bigger than one day
     *     TODO Range has it's own notation, will use an enum or something like it to set values for
     *     1 hour range, 1 day, 1 week, 1 month, 3 months, 6 months, 1 year, 2 years, 3 years, 5 years,
     *     10 years, MAX
     */
    @GET("stock/{symbol}/chart/{range}/{date}")
    suspend fun getHistoricalStockPrices(
            @Path("symbol") stockSymbol: String,
            @Path("range") priceRangeOfPrices: String,
            @Path("date") todaysDate: String,
            @Query("token") apiToken: String
    )

    /**
     *  This is the method that will be used to get that day's data points
     *  It uses intraday endpoint.
     */

    @GET("stock/{symbol}/intraday-prices")
    suspend fun getIntradayStockPrices(
            @Path("symbol") stockSymbol: String,
            @Query("token") apiToken: String
    )

    /**
     * Similar to getRealTimeStockQuote, but instead of only getting one field, it will get everything
     */
    @GET("stock/{symbol}/quote/")
    suspend fun getOtherStockInformation(
            @Path("symbol") stockSymbol: String,
            @Query("token") apiToken: String
    )

    /**
     * This method will be used to retrieve ETF information that correlates to index performance.
     * Due to cost of retrieving actual index performance data, we will use ETF which are free.
     */
    @GET("stock/{symbol}/company")
    suspend fun getETFInformation(
            @Path("symbol") etfSymbol: String,
            @Query("token") apiToken: String
    )

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
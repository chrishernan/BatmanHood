package com.example.batmanhood.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RealTimeStockQuote : Parcelable {
    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("companyName")
    @Expose
    var companyName: String? = null

    @SerializedName("primaryExchange")
    @Expose
    var primaryExchange: String? = null

    @SerializedName("calculationPrice")
    @Expose
    var calculationPrice: String? = null

    @SerializedName("open")
    @Expose
    var open: Any? = null

    @SerializedName("openTime")
    @Expose
    var openTime: Any? = null

    @SerializedName("openSource")
    @Expose
    var openSource: String? = null

    @SerializedName("close")
    @Expose
    var close: Any? = null

    @SerializedName("closeTime")
    @Expose
    var closeTime: Any? = null

    @SerializedName("closeSource")
    @Expose
    var closeSource: String? = null

    @SerializedName("high")
    @Expose
    var high: Any? = null

    @SerializedName("highTime")
    @Expose
    var highTime: Long? = null

    @SerializedName("highSource")
    @Expose
    var highSource: String? = null

    @SerializedName("low")
    @Expose
    var low: Any? = null

    @SerializedName("lowTime")
    @Expose
    var lowTime: Any? = null

    @SerializedName("lowSource")
    @Expose
    var lowSource: Any? = null

    @SerializedName("latestPrice")
    @Expose
    var latestPrice: Double? = null

    @SerializedName("latestSource")
    @Expose
    var latestSource: String? = null

    @SerializedName("latestTime")
    @Expose
    var latestTime: String? = null

    @SerializedName("latestUpdate")
    @Expose
    var latestUpdate: Long? = null

    @SerializedName("latestVolume")
    @Expose
    var latestVolume: Any? = null

    @SerializedName("iexRealtimePrice")
    @Expose
    var iexRealtimePrice: Double? = null

    @SerializedName("iexRealtimeSize")
    @Expose
    var iexRealtimeSize: Long? = null

    @SerializedName("iexLastUpdated")
    @Expose
    var iexLastUpdated: Long? = null

    @SerializedName("delayedPrice")
    @Expose
    var delayedPrice: Any? = null

    @SerializedName("delayedPriceTime")
    @Expose
    var delayedPriceTime: Any? = null

    @SerializedName("oddLotDelayedPrice")
    @Expose
    var oddLotDelayedPrice: Any? = null

    @SerializedName("oddLotDelayedPriceTime")
    @Expose
    var oddLotDelayedPriceTime: Any? = null

    @SerializedName("extendedPrice")
    @Expose
    var extendedPrice: Any? = null

    @SerializedName("extendedChange")
    @Expose
    var extendedChange: Any? = null

    @SerializedName("extendedChangePercent")
    @Expose
    var extendedChangePercent: Any? = null

    @SerializedName("extendedPriceTime")
    @Expose
    var extendedPriceTime: Any? = null

    @SerializedName("previousClose")
    @Expose
    var previousClose: Double? = null

    @SerializedName("previousVolume")
    @Expose
    var previousVolume: Long? = null

    @SerializedName("change")
    @Expose
    var change: Double? = null

    @SerializedName("changePercent")
    @Expose
    var changePercent: Double? = null

    @SerializedName("volume")
    @Expose
    var volume: Any? = null

    @SerializedName("iexMarketPercent")
    @Expose
    var iexMarketPercent: Double? = null

    @SerializedName("iexVolume")
    @Expose
    var iexVolume: Long? = null

    @SerializedName("avgTotalVolume")
    @Expose
    var avgTotalVolume: Long? = null

    @SerializedName("iexBidPrice")
    @Expose
    var iexBidPrice: Long? = null

    @SerializedName("iexBidSize")
    @Expose
    var iexBidSize: Long? = null

    @SerializedName("iexAskPrice")
    @Expose
    var iexAskPrice: Long? = null

    @SerializedName("iexAskSize")
    @Expose
    var iexAskSize: Long? = null

    @SerializedName("iexOpen")
    @Expose
    var iexOpen: Double? = null

    @SerializedName("iexOpenTime")
    @Expose
    var iexOpenTime: Long? = null

    @SerializedName("iexClose")
    @Expose
    var iexClose: Double? = null

    @SerializedName("iexCloseTime")
    @Expose
    var iexCloseTime: Long? = null

    @SerializedName("marketCap")
    @Expose
    var marketCap: Long? = null

    @SerializedName("peRatio")
    @Expose
    var peRatio: Double? = null

    @SerializedName("week52High")
    @Expose
    var week52High: Double? = null

    @SerializedName("week52Low")
    @Expose
    var week52Low: Double? = null

    @SerializedName("ytdChange")
    @Expose
    var ytdChange: Double? = null

    @SerializedName("lastTradeTime")
    @Expose
    var lastTradeTime: Long? = null

    @SerializedName("isUSMarketOpen")
    @Expose
    var isUSMarketOpen: Boolean? = null

    protected constructor(`in`: Parcel) {
        symbol = `in`.readValue(String::class.java.classLoader) as String?
        companyName = `in`.readValue(String::class.java.classLoader) as String?
        primaryExchange = `in`.readValue(String::class.java.classLoader) as String?
        calculationPrice = `in`.readValue(String::class.java.classLoader) as String?
        open = `in`.readValue(Any::class.java.classLoader)
        openTime = `in`.readValue(Any::class.java.classLoader)
        openSource = `in`.readValue(String::class.java.classLoader) as String?
        close = `in`.readValue(Any::class.java.classLoader)
        closeTime = `in`.readValue(Any::class.java.classLoader)
        closeSource = `in`.readValue(String::class.java.classLoader) as String?
        high = `in`.readValue(Any::class.java.classLoader)
        highTime = `in`.readValue(Long::class.java.classLoader) as Long?
        highSource = `in`.readValue(String::class.java.classLoader) as String?
        low = `in`.readValue(Any::class.java.classLoader)
        lowTime = `in`.readValue(Any::class.java.classLoader)
        lowSource = `in`.readValue(Any::class.java.classLoader)
        latestPrice = `in`.readValue(Double::class.java.classLoader) as Double?
        latestSource = `in`.readValue(String::class.java.classLoader) as String?
        latestTime = `in`.readValue(String::class.java.classLoader) as String?
        latestUpdate = `in`.readValue(Long::class.java.classLoader) as Long?
        latestVolume = `in`.readValue(Any::class.java.classLoader)
        iexRealtimePrice = `in`.readValue(Double::class.java.classLoader) as Double?
        iexRealtimeSize = `in`.readValue(Long::class.java.classLoader) as Long?
        iexLastUpdated = `in`.readValue(Long::class.java.classLoader) as Long?
        delayedPrice = `in`.readValue(Any::class.java.classLoader)
        delayedPriceTime = `in`.readValue(Any::class.java.classLoader)
        oddLotDelayedPrice = `in`.readValue(Any::class.java.classLoader)
        oddLotDelayedPriceTime = `in`.readValue(Any::class.java.classLoader)
        extendedPrice = `in`.readValue(Any::class.java.classLoader)
        extendedChange = `in`.readValue(Any::class.java.classLoader)
        extendedChangePercent = `in`.readValue(Any::class.java.classLoader)
        extendedPriceTime = `in`.readValue(Any::class.java.classLoader)
        previousClose = `in`.readValue(Double::class.java.classLoader) as Double?
        previousVolume = `in`.readValue(Long::class.java.classLoader) as Long?
        change = `in`.readValue(Double::class.java.classLoader) as Double?
        changePercent = `in`.readValue(Double::class.java.classLoader) as Double?
        volume = `in`.readValue(Any::class.java.classLoader)
        iexMarketPercent = `in`.readValue(Double::class.java.classLoader) as Double?
        iexVolume = `in`.readValue(Long::class.java.classLoader) as Long?
        avgTotalVolume = `in`.readValue(Long::class.java.classLoader) as Long?
        iexBidPrice = `in`.readValue(Long::class.java.classLoader) as Long?
        iexBidSize = `in`.readValue(Long::class.java.classLoader) as Long?
        iexAskPrice = `in`.readValue(Long::class.java.classLoader) as Long?
        iexAskSize = `in`.readValue(Long::class.java.classLoader) as Long?
        iexOpen = `in`.readValue(Double::class.java.classLoader) as Double?
        iexOpenTime = `in`.readValue(Long::class.java.classLoader) as Long?
        iexClose = `in`.readValue(Double::class.java.classLoader) as Double?
        iexCloseTime = `in`.readValue(Long::class.java.classLoader) as Long?
        marketCap = `in`.readValue(Long::class.java.classLoader) as Long?
        peRatio = `in`.readValue(Double::class.java.classLoader) as Double?
        week52High = `in`.readValue(Double::class.java.classLoader) as Double?
        week52Low = `in`.readValue(Double::class.java.classLoader) as Double?
        ytdChange = `in`.readValue(Double::class.java.classLoader) as Double?
        lastTradeTime = `in`.readValue(Long::class.java.classLoader) as Long?
        isUSMarketOpen = `in`.readValue(Boolean::class.java.classLoader) as Boolean?
    }

    constructor() {}

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(symbol)
        dest.writeValue(companyName)
        dest.writeValue(primaryExchange)
        dest.writeValue(calculationPrice)
        dest.writeValue(open)
        dest.writeValue(openTime)
        dest.writeValue(openSource)
        dest.writeValue(close)
        dest.writeValue(closeTime)
        dest.writeValue(closeSource)
        dest.writeValue(high)
        dest.writeValue(highTime)
        dest.writeValue(highSource)
        dest.writeValue(low)
        dest.writeValue(lowTime)
        dest.writeValue(lowSource)
        dest.writeValue(latestPrice)
        dest.writeValue(latestSource)
        dest.writeValue(latestTime)
        dest.writeValue(latestUpdate)
        dest.writeValue(latestVolume)
        dest.writeValue(iexRealtimePrice)
        dest.writeValue(iexRealtimeSize)
        dest.writeValue(iexLastUpdated)
        dest.writeValue(delayedPrice)
        dest.writeValue(delayedPriceTime)
        dest.writeValue(oddLotDelayedPrice)
        dest.writeValue(oddLotDelayedPriceTime)
        dest.writeValue(extendedPrice)
        dest.writeValue(extendedChange)
        dest.writeValue(extendedChangePercent)
        dest.writeValue(extendedPriceTime)
        dest.writeValue(previousClose)
        dest.writeValue(previousVolume)
        dest.writeValue(change)
        dest.writeValue(changePercent)
        dest.writeValue(volume)
        dest.writeValue(iexMarketPercent)
        dest.writeValue(iexVolume)
        dest.writeValue(avgTotalVolume)
        dest.writeValue(iexBidPrice)
        dest.writeValue(iexBidSize)
        dest.writeValue(iexAskPrice)
        dest.writeValue(iexAskSize)
        dest.writeValue(iexOpen)
        dest.writeValue(iexOpenTime)
        dest.writeValue(iexClose)
        dest.writeValue(iexCloseTime)
        dest.writeValue(marketCap)
        dest.writeValue(peRatio)
        dest.writeValue(week52High)
        dest.writeValue(week52Low)
        dest.writeValue(ytdChange)
        dest.writeValue(lastTradeTime)
        dest.writeValue(isUSMarketOpen)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<RealTimeStockQuote?> =
            object : Parcelable.Creator<RealTimeStockQuote?> {
                override fun createFromParcel(`in`: Parcel): RealTimeStockQuote? {
                    return RealTimeStockQuote(`in`)
                }

                override fun newArray(size: Int): Array<RealTimeStockQuote?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
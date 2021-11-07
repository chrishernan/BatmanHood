package com.example.batmanhood.utils

import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import com.example.batmanhood.adapters.AssetRecyclerViewAdapter
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.*
import java.util.*

fun calculateDollarDifference(latestPrice : Float, oldestPrice : Float) : Float {
    return cropTo2DecimalPlaces(latestPrice-oldestPrice)

}

fun calculatePercentageDifference(latestPrice : Float, oldestPrice : Float) : Float{
    return when {
        oldestPrice == latestPrice -> 0.0f
        oldestPrice > latestPrice -> {
            cropTo2DecimalPlaces(((latestPrice-oldestPrice)/oldestPrice)*100)
        }
        else -> {
            cropTo2DecimalPlaces(((latestPrice-oldestPrice)/oldestPrice)*100)
        }
    }
}

fun cropTo2DecimalPlaces(num : Float) : Float {
    val roundedUpDecimal = BigDecimal(num.toDouble()).setScale(2,RoundingMode.HALF_EVEN)
    return roundedUpDecimal.toFloat()
}

fun <T1: Any, T2: Any, R: Any> safeLetTwo(p1: T1?, p2: T2?,block: (T1, T2)->R?): R? {
    return if (p1 != null && p2 != null ) block(p1, p2) else null
}

fun <T1: Any, T2: Any,T3: Any, R: Any> safeLetThree(p1: T1?, p2: T2?,p3: T3? ,block: (T1, T2,T3)->R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2,p3) else null
}

@RequiresApi(Build.VERSION_CODES.O)
fun daySoFarTradingRatio() : Double {
    val currentDeviceTime = Instant.now()
    val eastCoastZoneId = ZoneId.of("America/Indiana/Indianapolis")
    val eastCoastTime = ZonedDateTime.ofInstant(currentDeviceTime,eastCoastZoneId)
    if(eastCoastTime.hour >= Constants.MARKET_CLOSE_TIME) return -1.0
    if(eastCoastTime.hour <= Constants.MARKET_OPEN_TIME_HOUR || eastCoastTime.minute < Constants.MARKET_OPEN_TIME_MINUTE )
        return -1.0
    //todo figure out how to store days that market is closed and check here so that you display full width sparkview
    val minutesMarketHasBeenOpen = ((eastCoastTime.hour - Constants.MARKET_OPEN_TIME_HOUR)*60)+eastCoastTime.minute
    val ratio = minutesMarketHasBeenOpen/(Constants.TOTAL_MINUTES_MARKET_OPEN).toDouble()
    return ratio
}

fun View.onLayout(cb: () -> Unit) {
    if (this.viewTreeObserver.isAlive) {
        this.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        cb()
                        this@onLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
    }
}
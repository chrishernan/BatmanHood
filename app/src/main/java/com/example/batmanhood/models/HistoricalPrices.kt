package com.example.batmanhood.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HistoricalPrices : Parcelable {
    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("minute")
    @Expose
    var minute: String? = null

    @SerializedName("close")
    @Expose
    var close: Float? = null

    protected constructor(`in`: Parcel) {
        date = `in`.readValue(String::class.java.classLoader) as String?
        minute = `in`.readValue(String::class.java.classLoader) as String?
        close = `in`.readValue(Float::class.java.classLoader) as Float?
    }

    constructor() {}

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(date)
        dest.writeValue(minute)
        dest.writeValue(close)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<HistoricalPrices?> =
            object : Parcelable.Creator<HistoricalPrices?> {
                override fun createFromParcel(`in`: Parcel): HistoricalPrices? {
                    return HistoricalPrices(`in`)
                }

                override fun newArray(size: Int): Array<HistoricalPrices?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
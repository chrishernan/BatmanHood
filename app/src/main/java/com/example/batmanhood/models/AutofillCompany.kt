package com.example.batmanhood.models

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class AutofillCompany : Parcelable {
    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("exchange")
    @Expose
    var exchange: String? = null

    @SerializedName("exchangeSuffix")
    @Expose
    var exchangeSuffix: String? = null

    @SerializedName("exchangeName")
    @Expose
    var exchangeName: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("iexId")
    @Expose
    var iexId: String? = null

    @SerializedName("region")
    @Expose
    var region: String? = null

    @SerializedName("currency")
    @Expose
    var currency: String? = null

    @SerializedName("isEnabled")
    @Expose
    var isEnabled: Boolean? = null

    @SerializedName("figi")
    @Expose
    var figi: String? = null

    @SerializedName("cik")
    @Expose
    var cik: String? = null

    @SerializedName("lei")
    @Expose
    var lei: String? = null

    protected constructor(`in`: Parcel) {
        symbol = `in`.readValue(String::class.java.classLoader) as String?
        exchange = `in`.readValue(String::class.java.classLoader) as String?
        exchangeSuffix = `in`.readValue(String::class.java.classLoader) as String?
        exchangeName = `in`.readValue(String::class.java.classLoader) as String?
        name = `in`.readValue(String::class.java.classLoader) as String?
        date = `in`.readValue(String::class.java.classLoader) as String?
        type = `in`.readValue(String::class.java.classLoader) as String?
        iexId = `in`.readValue(String::class.java.classLoader) as String?
        region = `in`.readValue(String::class.java.classLoader) as String?
        currency = `in`.readValue(String::class.java.classLoader) as String?
        isEnabled = `in`.readValue(Boolean::class.java.classLoader) as Boolean?
        figi = `in`.readValue(String::class.java.classLoader) as String?
        cik = `in`.readValue(String::class.java.classLoader) as String?
        lei = `in`.readValue(String::class.java.classLoader) as String?
    }

    constructor() {}

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(symbol)
        dest.writeValue(exchange)
        dest.writeValue(exchangeSuffix)
        dest.writeValue(exchangeName)
        dest.writeValue(name)
        dest.writeValue(date)
        dest.writeValue(type)
        dest.writeValue(iexId)
        dest.writeValue(region)
        dest.writeValue(currency)
        dest.writeValue(isEnabled)
        dest.writeValue(figi)
        dest.writeValue(cik)
        dest.writeValue(lei)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Creator<AutofillCompany?> = object : Creator<AutofillCompany?> {
            override fun createFromParcel(`in`: Parcel): AutofillCompany? {
                return AutofillCompany(`in`)
            }

            override fun newArray(size: Int): Array<AutofillCompany?> {
                return arrayOfNulls(size)
            }
        }
    }
}
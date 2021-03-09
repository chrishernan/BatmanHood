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

    @SerializedName("name")
    @Expose
    var name: String? = null

    protected constructor(`in`: Parcel) {
        symbol = `in`.readValue(String::class.java.classLoader) as String?
        name = `in`.readValue(String::class.java.classLoader) as String?
    }

    constructor() {}

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(symbol)
        dest.writeValue(name)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField val CREATOR: Creator<AutofillCompany?> = object : Creator<AutofillCompany?> {
            override fun createFromParcel(`in`: Parcel): AutofillCompany? {
                return AutofillCompany(`in`)
            }

            override fun newArray(size: Int): Array<AutofillCompany?> {
                return arrayOfNulls(size)
            }
        }
    }
}
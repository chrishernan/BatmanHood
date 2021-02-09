package com.example.batmanhood.models


import android.os.Parcel
import android.os.Parcelable

data class User(
        val id: String = "",
        val name: String = "",
        val email: String = "",
        val image: String = "",
        val mobile: Long = 0,
        val fcmToken: String = "",
        val crypto_list: MutableList<String> = mutableListOf("btc", "eth"),
        var stock_list: MutableList<String> = mutableListOf("aapl", "msft", "amzn"),
        val index_list: MutableList<String> = mutableListOf(".inx", ".ixic", ".dji")
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString()!!,
            source.readString()!!,
            source.readString()!!,
            source.readString()!!,
            source.readLong(),
            source.readString()!!,
            source.createStringArrayList()!!,
            source.createStringArrayList()!!,
            source.createStringArrayList()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(email)
        writeString(image)
        writeLong(mobile)
        writeString(fcmToken)
        writeStringList(crypto_list)
        writeStringList(stock_list)
        writeStringList(index_list)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}
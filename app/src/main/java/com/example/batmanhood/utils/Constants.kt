package com.example.batmanhood.utils

object Constants {

    // Firebase Constants
    // This  is used for the collection name for USERS.
    const val USERS: String = "users"
    const val ASSETS: String = "assets"
    const val PASSWORD_DIGIT_PATTERN: String = "^(?=.*[0-9])"
    const val PASSWORD_LOWER_CASE_PATTERN: String = "(?=.*[a-z])"
    const val PASSWORD_UPPER_CASE_PATTERN: String = "(?=.*[A-Z])"
    const val PASSWORD_SPECIAL_CHARACTER_PATTERN : String = """(?=.*[*.!@$%^&(){}[]:;<>,.?/~_+-=|\])"""

    //Historical Stock Prices date range
    const val ONEDAY : String = "1d"
    const val ONEWEEK : String = "5dm"
    const val ONEMONTH : String = "1mm"
    const val SIXMONTHS : String = "6m"
    const val ONEYEAR : String = "1y"
    const val FIVEYEARS : String = "5y"
    const val HISTORICALDATAAPIFILTER = "date,minute,close"
    const val CHARTSIMPLIFY = "true"
    const val INCLUDETODAYTRADINGDATA = "true"

    //Test Endpoint for Stocks and Index Funds
    const val IEX_BASE_URL = "https://sandbox.iexapis.com/"
    const val IEX_TOKEN = "Tpk_e9c11e2d2f0342e2b4c548e70d21a510"
}
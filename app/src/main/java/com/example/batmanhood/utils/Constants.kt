package com.example.batmanhood.utils

object Constants {

    // Firebase Constants
    // This  is used for the collection name for USERS.
    const val USERS: String = "users"
    const val ASSETS: String = "assets"
    const val PASSWORD_DIGIT_PATTERN: String = "^(?=.*[0G-9])"
    const val PASSWORD_LOWER_CASE_PATTERN: String = "(?=.*[a-z])"
    const val PASSWORD_UPPER_CASE_PATTERN: String = "(?=.*[A-Z])"
    const val PASSWORD_SPECIAL_CHARACTER_PATTERN : String = """(?=.*[*.!@$%^&(){}[]:;<>,.?/~_+-=|\])"""

    //Historical Stock Prices date range
    const val ONE_DAY : String = "1d"
    const val FIVE_DAYS : String = "5dm"
    const val ONE_MONTH : String = "1mm"
    const val SIX_MONTHS : String = "6m"
    const val ONE_YEAR : String = "1y"
    const val FIVE_YEARS : String = "5y"
    const val HISTORICAL_DATA_API_FILTER = "date,minute,close"
    const val ALL_COMPANIES_API_FILTER = "symbol,name"
    const val CHART_SIMPLIFY = "true"
    const val INCLUDE_TODAY_TRADING_DATA = "true"

    //Millisecond lengths pertinent to trading
    const val FULL_TRADING_HOURS_IN_MILLI = 27000000
    const val MARKET_CLOSE_TIME = 16
    const val MARKET_OPEN_TIME_HOUR = 9
    const val MARKET_OPEN_TIME_MINUTE = 30
    const val TOTAL_MINUTES_MARKET_OPEN = 390

    //Test Endpoint for Stocks and Index Funds
    const val IEX_BASE_URL = "https://sandbox.iexapis.com/"
    const val IEX_TOKEN = "Tpk_e9c11e2d2f0342e2b4c548e70d21a510"
}
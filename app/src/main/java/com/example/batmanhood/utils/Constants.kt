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
    //Test URL
    const val IEX_BASE_URL = "https://sandbox.iexapis.com/"
    const val IEX_TOKEN = "Tpk_e9c11e2d2f0342e2b4c548e70d21a510"
}
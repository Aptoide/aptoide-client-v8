package com.aptoide.aptoide_ab_testing

class FlagrException(message: String, val code: Int = 0) :
    RuntimeException(message + code.toString())
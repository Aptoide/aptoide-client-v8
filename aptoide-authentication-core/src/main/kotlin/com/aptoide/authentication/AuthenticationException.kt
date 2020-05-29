package com.aptoide.authentication

class AuthenticationException(message: String, val code: Int = 0) :
    RuntimeException(message + code.toString())
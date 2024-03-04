package com.appcoins.payments.json

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@MustBeDocumented
@Retention(SOURCE)
@Target(CLASS, VALUE_PARAMETER)
annotation class Json(
  val name: String = "",
)

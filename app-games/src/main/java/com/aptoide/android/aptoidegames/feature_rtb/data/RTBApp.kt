package com.aptoide.android.aptoidegames.feature_rtb.data

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import kotlin.random.Random

data class RTBApp(
  val app: App,
  val adUrl: String?,
  val adTimeout: Int?,
  val isAptoideInstall: Boolean
)

val randomRTBApp
  get() = RTBApp(
    app = randomApp,
    adUrl = "https://en.aptoide.com",
    adTimeout = 10,
    isAptoideInstall = Random.nextBoolean()
  )

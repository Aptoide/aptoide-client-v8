package com.aptoide.android.aptoidegames

import android.content.Intent
import android.net.Uri
import com.aptoide.android.aptoidegames.analytics.presentation.withPrevScreen

// Deep link
private const val DEEPLINK_KEY = "ag.link"

fun Intent.putDeeplink(deepLink: String): Intent = putExtra(DEEPLINK_KEY, deepLink)

// Launch source
private const val LAUNCH_SOURCE = "launchSource"

val Intent.appOpenSource: String
  get() = getStringExtra(LAUNCH_SOURCE) ?: "app_icon_click"

fun Intent.putNotificationSource(): Intent = putExtra(LAUNCH_SOURCE, "notification")

val Intent?.agDeepLink
  get() = this?.extras
    ?.getString(DEEPLINK_KEY)
    ?.withPrevScreen("notification")
    ?.let(Uri::parse)

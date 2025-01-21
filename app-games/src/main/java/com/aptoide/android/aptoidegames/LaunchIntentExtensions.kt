package com.aptoide.android.aptoidegames

import android.content.Intent
import android.net.Uri
import com.aptoide.android.aptoidegames.analytics.presentation.withPrevScreen

// Deep link
const val DEEPLINK_KEY = "dti.link"

val Intent?.hasDeepLink get() = this?.extras?.containsKey(DEEPLINK_KEY) ?: false

//App link
const val APP_LINK_SCHEMA = "https://"
const val APP_LINK_HOST = "{LANG}.aptoide.com"

fun Intent.putDeeplink(deepLink: String): Intent = putExtra(DEEPLINK_KEY, deepLink)

// Ahab
private const val AHAB_NOTIFICATION = "AHAB_NOTIFICATION"

val Intent?.isAhab: Boolean get() = this?.getBooleanExtra(AHAB_NOTIFICATION, false) == true

fun Intent.markAsAhab(): Intent = putExtra(AHAB_NOTIFICATION, hasDeepLink)

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

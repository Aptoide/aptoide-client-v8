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

//External url
const val EXTERNAL_KEY = "external_url"
const val WEBVIEW_KEY = "webview"

// Launch source
private const val LAUNCH_SOURCE = "launchSource"
private const val NOTIFICATION_TAG = "notificationTag"
private const val NOTIFICATION_PACKAGE = "notificationPackage"

val Intent.appOpenSource: String
  get() = getStringExtra(LAUNCH_SOURCE) ?: "app_icon_click"

fun Intent.putNotificationSource(): Intent = putExtra(LAUNCH_SOURCE, "notification")

fun Intent.putNotificationTag(tag: String? = null): Intent =
  putExtra(NOTIFICATION_TAG, tag)

fun Intent.putNotificationPackage(packageName: String? = null): Intent =
  putExtra(NOTIFICATION_PACKAGE, packageName)

val Intent?.agDeepLink
  get() = this?.extras
    ?.getString(DEEPLINK_KEY)
    ?.withPrevScreen("notification")
    ?.let(Uri::parse)

val Intent?.isAGNotification: Boolean
  get() = this?.extras?.getString(LAUNCH_SOURCE) == "notification"
    && this.notificationTag != null

val Intent?.notificationTag: String?
  get() = this?.extras
    ?.getString(NOTIFICATION_TAG)

val Intent?.notificationPackage: String?
  get() = this?.extras
    ?.getString(NOTIFICATION_PACKAGE)

val Intent?.externalUrl
  get() = this?.extras
    ?.getString(EXTERNAL_KEY)
    ?.let(Uri::parse)

val Intent?.shouldOpenWebView
  get() = this?.extras
    ?.getString(WEBVIEW_KEY)?.toBooleanStrictOrNull() ?: false

package com.aptoide.android.aptoidegames.apkfy

import cm.aptoide.pt.feature_apps.data.App

fun App.isRoblox() = packageName == "com.roblox.client"

fun App.isFreeFire() = packageName == "com.dts.freefireth" || packageName == "com.dts.freefiremax"
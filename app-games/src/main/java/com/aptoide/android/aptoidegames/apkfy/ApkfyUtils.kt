package com.aptoide.android.aptoidegames.apkfy

import cm.aptoide.pt.feature_apps.data.App

const val ROBLOX_PACKAGE = "com.roblox.client"
const val FREE_FIRE_PACKAGE = "com.dts.freefireth"
const val FREE_FIRE_MAX_PACKAGE = "com.dts.freefiremax"

fun isRobloxPackage(packageName: String) = packageName == ROBLOX_PACKAGE

fun isFreeFirePackage(packageName: String) =
  packageName == FREE_FIRE_PACKAGE || packageName == FREE_FIRE_MAX_PACKAGE

fun App.isRoblox() = isRobloxPackage(packageName)

fun App.isFreeFire() = isFreeFirePackage(packageName)

package com.aptoide.android.aptoidegames.di

import com.aptoide.android.aptoidegames.BuildConfig

// The trending games shown while a search is empty
internal val DEFAULT_TRENDING_URL: String =
  "${BuildConfig.STORE_DOMAIN}listApps/sort=trending60d/limit=9"

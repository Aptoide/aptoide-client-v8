package com.aptoide.android.aptoidegames.di

import cm.aptoide.pt.device_api.catalog.CatalogParent
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource.Companion.SORT_TRENDING
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.newservices.NewServicesListing
import com.aptoide.android.aptoidegames.newservices.NewServicesListingUrl

// The trending games shown while a search is empty: the new services' once they are read
internal val DEFAULT_TRENDING_URL: String = if (BuildConfig.NEW_SERVICES_ENABLED) {
  NewServicesListingUrl.build(
    NewServicesListing(category = CatalogParent.GAMES, sort = SORT_TRENDING, limit = 9)
  )
} else {
  "${BuildConfig.STORE_DOMAIN}listApps/sort=trending60d/limit=9"
}

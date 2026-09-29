package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.device_api.network.DeviceProfile
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsService
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppSummaryResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppsPageResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.RelatedAppsResponse
import cm.aptoide.pt.feature_apps.data.randomApp
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope

internal data class ListingRequest(
  val q: String?,
  val category: String?,
  val sort: String?,
  val limit: Int?,
  val refresh: Int?,
)

/** The device API, answering every listing with [listing] and recording what was asked. */
internal class FakeDeviceApi(
  private val listing: List<String> = listOf("device.api.app"),
  private val failure: Throwable? = null,
) : DeviceApiAppsService {

  val listings = mutableListOf<ListingRequest>()
  val related = mutableListOf<String>()

  override suspend fun getApps(
    q: String?,
    category: String?,
    sort: String?,
    limit: Int?,
    variant: String,
    sdk: Int?,
    abi: String?,
    tv: Boolean?,
    density: Int?,
    refresh: Int?,
  ): AppsPageResponse {
    listings += ListingRequest(q, category, sort, limit, refresh)
    failure?.let { throw it }
    return AppsPageResponse(items = listing.map { it.toSummary() }, nextCursor = null)
  }

  override suspend fun getRelated(
    packageName: String,
    limit: Int?,
    variant: String,
    sdk: Int?,
    abi: String?,
    tv: Boolean?,
    density: Int?,
  ): RelatedAppsResponse {
    related += packageName
    failure?.let { throw it }
    return RelatedAppsResponse(items = listing.map { it.toSummary() })
  }

  fun dataSource(scope: TestScope) = DeviceApiAppsDataSource(
    service = this,
    variant = "google-certified",
    storeName = "a-store",
    deviceProfile = { DeviceProfile(sdk = 34, abis = listOf("arm64-v8a"), tv = false, density = 480) },
    dispatcher = StandardTestDispatcher(scope.testScheduler),
  )

  private fun String.toSummary() = AppSummaryResponse(packageName = this, name = this)
}

/** The v7 repository, answering everything with one app and recording what was asked. */
internal class FakeV7AppsListRepository : AppsListRepository {

  val calls = mutableListOf<String>()
  val app = randomApp.copy(packageName = "v7.app")

  override suspend fun getAppsList(url: String, bypassCache: Boolean): List<App> =
    answer("url:$url:$bypassCache")

  override suspend fun getAppsList(
    storeId: Long,
    groupId: Long,
    bypassCache: Boolean,
  ): List<App> = answer("group:$storeId:$groupId:$bypassCache")

  override suspend fun getRecommended(path: String): List<App> = answer("recommended:$path")

  override suspend fun getCategoryAppsList(categoryName: String): List<App> =
    answer("category:$categoryName")

  override suspend fun getAppVersions(packageName: String): List<App> =
    answer("versions:$packageName")

  override suspend fun getAppsList(packageNames: String): List<App> =
    answer("packages:$packageNames")

  override suspend fun getSortedAppsList(sort: String, limit: Int): List<App> =
    answer("sorted:$sort:$limit")

  private fun answer(call: String): List<App> {
    calls += call
    return listOf(app)
  }
}

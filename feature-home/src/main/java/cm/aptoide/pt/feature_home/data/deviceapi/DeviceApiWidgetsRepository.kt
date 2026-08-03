package cm.aptoide.pt.feature_home.data.deviceapi

import cm.aptoide.pt.device_api.error.deviceApiCall
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceHomeAppsCache
import cm.aptoide.pt.feature_apps.data.deviceapi.toApp
import cm.aptoide.pt.feature_home.data.WidgetsRepository
import cm.aptoide.pt.feature_home.data.deviceapi.model.AppCollectionSectionResponse
import cm.aptoide.pt.feature_home.data.deviceapi.model.FeaturedBannerSectionResponse
import cm.aptoide.pt.feature_home.data.deviceapi.model.HomeSectionResponse
import cm.aptoide.pt.feature_home.data.deviceapi.model.TopChartSectionResponse
import cm.aptoide.pt.feature_home.domain.Widget
import cm.aptoide.pt.feature_home.domain.WidgetLayout
import cm.aptoide.pt.feature_home.domain.WidgetType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

/**
 * Device-API home (`GET /home`, catalog-only). Maps the typed sections onto the
 * client's `Widget`/`Bundle` renderers and stashes each section's inline apps in
 * [DeviceHomeAppsCache] (the lazy fetch-by-tag renderer reads them back).
 *
 * Catalog-only per the migration decision: monetization/engagement sections that
 * `/home` doesn't carry are absent (see .context/contract-friction.md). Unknown
 * section kinds are already dropped to null by the Gson discriminator factory;
 * category_grid / editorial_card are skipped here (their own screens cover them).
 */
class DeviceApiWidgetsRepository(
  private val service: DeviceApiWidgetsService,
  private val variant: String,
  private val storeName: String,
  private val homeAppsCache: DeviceHomeAppsCache,
  private val scope: CoroutineScope,
) : WidgetsRepository {

  override suspend fun getStoreWidgets(context: String?, bypassCache: Boolean): List<Widget> =
    withContext(scope.coroutineContext) {
      deviceApiCall { service.getHome(variant, refresh = if (bypassCache) 1 else null) }
        .sections.orEmpty()
        .filterNotNull()
        .mapIndexedNotNull { index, section -> section.toWidget(index) }
    }

  private fun HomeSectionResponse.toWidget(index: Int): Widget? = when (this) {
    is FeaturedBannerSectionResponse -> appsWidget(
      index = index,
      title = tagline.orEmpty(),
      layout = WidgetLayout.CAROUSEL_LARGE,
      graphic = imageUrl,
      apps = listOfNotNull(app?.toApp(storeName)),
    )

    is AppCollectionSectionResponse -> appsWidget(
      index = index,
      title = title.orEmpty(),
      layout = WidgetLayout.CAROUSEL,
      graphic = null,
      apps = apps.orEmpty().map { it.toApp(storeName) },
    )

    is TopChartSectionResponse -> appsWidget(
      index = index,
      title = title.orEmpty(),
      layout = WidgetLayout.GRID,
      graphic = null,
      apps = apps.orEmpty().map { it.toApp(storeName) },
    )

    else -> null // category_grid / editorial_card: covered by their own screens
  }

  private fun appsWidget(
    index: Int,
    title: String,
    layout: WidgetLayout,
    graphic: String?,
    apps: List<App>,
  ): Widget {
    val key = DeviceHomeAppsCache.KEY_PREFIX + index
    homeAppsCache.put(key, apps)
    return Widget(
      title = title,
      type = WidgetType.APPS_GROUP,
      layout = layout,
      view = key,
      tag = key,
      action = emptyList(),
      icon = null,
      graphic = graphic,
      background = null,
      url = null,
    )
  }
}

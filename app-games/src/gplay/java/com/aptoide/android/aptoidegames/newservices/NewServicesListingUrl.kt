package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource.Companion.SORT_DOWNLOADS
import java.net.URLDecoder

/** A listing of the new services: the apps of [category], sorted by [sort]. */
internal data class NewServicesListing(
  val category: String,
  val sort: String = SORT_DOWNLOADS,
  val limit: Int? = null,
)

/**
 * A [NewServicesListing] written as an url, which is how shared code keeps and passes around
 * what a bundle lists:
 *
 * `newservices/listApps/category=game_action/sort=downloads/limit=9`
 *
 * It is written as path segments because shared code adds to the url it holds the same way,
 * appending a `limit` to see all of a bundle. The last of a repeated segment wins. It holds
 * `listApps/` because bundles only offer to see all of an url that does.
 */
internal object NewServicesListingUrl {

  private const val PREFIX = "newservices/"
  private const val CATEGORY = "category"
  private const val SORT = "sort"
  private const val LIMIT = "limit"

  fun build(listing: NewServicesListing): String = buildString {
    append(PREFIX).append("listApps")
    append("/$CATEGORY=").append(listing.category)
    append("/$SORT=").append(listing.sort)
    listing.limit?.let { append("/$LIMIT=").append(it) }
  }

  /** The listing [url] stands for, or null when it is not one of the new services. */
  fun parse(url: String): NewServicesListing? {
    if (!url.startsWith(PREFIX)) return null
    val values = url.removePrefix(PREFIX)
      .split("/")
      // Shared code may append a segment encoded, turning its = into %3D. One that cannot be
      // decoded is dropped rather than failing the whole read
      .mapNotNull { segment ->
        runCatching { URLDecoder.decode(segment, Charsets.UTF_8.name()) }.getOrNull()
      }
      .filter { "=" in it }
      .associate { it.substringBefore("=") to it.substringAfter("=") }
    val category = values[CATEGORY]?.takeIf { it.isNotBlank() } ?: return null
    return NewServicesListing(
      category = category,
      sort = values[SORT]?.takeIf { it.isNotBlank() } ?: SORT_DOWNLOADS,
      limit = values[LIMIT]?.toIntOrNull(),
    )
  }
}

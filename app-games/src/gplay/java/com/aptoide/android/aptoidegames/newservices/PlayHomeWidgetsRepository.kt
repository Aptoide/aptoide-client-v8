package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.device_api.catalog.CatalogParent
import cm.aptoide.pt.device_api.error.DeviceApiException
import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesDataSource
import cm.aptoide.pt.feature_categories.domain.Category
import cm.aptoide.pt.feature_home.data.WidgetsRepository
import cm.aptoide.pt.feature_home.domain.Widget
import cm.aptoide.pt.feature_home.domain.WidgetAction
import cm.aptoide.pt.feature_home.domain.WidgetActionType
import cm.aptoide.pt.feature_home.domain.WidgetLayout
import cm.aptoide.pt.feature_home.domain.WidgetType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The home of the Play build, composed here as the new services have none for it: the
 * AppCoins games v7 knows, the top games, one row per game genre, the categories, and the
 * user's own games.
 *
 * Each row is a [Widget] that shared code turns into a bundle and fills lazily by the url it
 * carries, so the rows cost one request each as they scroll into view. The AppCoins games
 * come from the v7 store, whose apps carry the flags and the files Aptoide's installer needs;
 * everything else is a listing of the new services.
 */
@Singleton
internal class PlayHomeWidgetsRepository @Inject constructor(
  @StoreName private val storeName: String,
  private val categories: DeviceApiCategoriesDataSource,
) : WidgetsRepository {

  // The context selects one of v7's homes, of which this build has one. The cache is that of
  // each row's listing, invalidated by tag when the rows are filled, so there is none here.
  override suspend fun getStoreWidgets(context: String?, bypassCache: Boolean): List<Widget> =
    listOf(appCoinsGames(), topGames()) +
      gameGenres().map { it.toRow() } +
      listOf(categoriesGrid(), myGames())

  private fun appCoinsGames() = appsRow(
    tag = "apps-group-appcoins",
    title = APPCOINS_GAMES_TITLE,
    layout = WidgetLayout.CAROUSEL,
    url = "listApps/store_name=$storeName/limit=$CAROUSEL_SIZE",
  )

  private fun topGames() = appsRow(
    tag = "apps-group-top-games",
    title = TOP_GAMES_TITLE,
    layout = WidgetLayout.CAROUSEL,
    url = NewServicesListingUrl.build(
      NewServicesListing(category = CatalogParent.GAMES, limit = CAROUSEL_SIZE)
    ),
  )

  private fun Category.toRow() = appsRow(
    tag = "apps-group-genre-$name",
    title = title,
    layout = WidgetLayout.GRID,
    url = NewServicesListingUrl.build(NewServicesListing(category = name, limit = GRID_SIZE)),
  )

  // Without a connection the home cannot be read at all, and the caller shows so. When the
  // service itself fails, the rows that need no genre are still worth showing.
  private suspend fun gameGenres(): List<Category> = try {
    categories.categories(parent = CatalogParent.GAMES)
  } catch (_: DeviceApiException) {
    emptyList()
  }

  private fun categoriesGrid() = Widget(
    title = CATEGORIES_TITLE,
    type = WidgetType.STORE_GROUPS,
    layout = WidgetLayout.GRID,
    view = "newservices/categories/parent=${CatalogParent.GAMES}",
    tag = "categories",
    action = null,
    icon = null,
    graphic = null,
    background = null,
    url = null,
  )

  private fun myGames() = Widget(
    title = MY_GAMES_TITLE,
    type = WidgetType.MY_GAMES,
    layout = WidgetLayout.GRID,
    view = null,
    tag = "my-games",
    action = null,
    icon = null,
    graphic = null,
    background = null,
    url = null,
  )

  // The see-all action is what shared code looks for, by the tag's suffix, before offering it
  private fun appsRow(tag: String, title: String, layout: WidgetLayout, url: String) = Widget(
    title = title,
    type = WidgetType.APPS_GROUP,
    layout = layout,
    view = url,
    tag = tag,
    action = listOf(WidgetAction(type = WidgetActionType.BUTTON, tag = "$tag-more", url = url)),
    icon = null,
    graphic = null,
    background = null,
    url = null,
  )

  companion object {
    // Fixed titles are keys into the translated bundle titles, like the v7 home's
    const val APPCOINS_GAMES_TITLE = "AppCoins Games"
    const val TOP_GAMES_TITLE = "Top Games"
    const val CATEGORIES_TITLE = "Categories"
    const val MY_GAMES_TITLE = "My Games"

    private const val CAROUSEL_SIZE = 24
    private const val GRID_SIZE = 9
  }
}

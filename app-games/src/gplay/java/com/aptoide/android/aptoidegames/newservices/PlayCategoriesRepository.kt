package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.aptoide_network.di.V7Backend
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesDataSource
import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesDataSource.Companion.PARENT_GAMES
import cm.aptoide.pt.feature_categories.domain.AppCategory
import cm.aptoide.pt.feature_categories.domain.Category
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The categories of the Play build: the game ones of the new services, whose catalog holds
 * apps as well. What the new services cannot answer stays on v7.
 */
@Singleton
internal class PlayCategoriesRepository @Inject constructor(
  @V7Backend private val v7: CategoriesRepository,
  private val newServices: DeviceApiCategoriesDataSource,
) : CategoriesRepository {

  // The url is how v7 addresses the categories of a store. There is one set of them here.
  override suspend fun getCategoriesList(url: String): List<Category> =
    newServices.categories(parent = PARENT_GAMES)

  override suspend fun getGlobalCategoriesList(url: String): List<Category> =
    v7.getGlobalCategoriesList(url)

  override suspend fun getAppsCategories(packageNames: List<String>): List<AppCategory> =
    v7.getAppsCategories(packageNames)
}

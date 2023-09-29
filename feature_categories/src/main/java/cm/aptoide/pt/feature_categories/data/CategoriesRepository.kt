package cm.aptoide.pt.feature_categories.data

import cm.aptoide.pt.feature_categories.domain.AppCategory
import cm.aptoide.pt.feature_categories.domain.Category

interface CategoriesRepository {

  suspend fun getCategoriesList(url: String): List<Category>

  suspend fun getGlobalCategoriesList(url: String): List<Category>

  suspend fun getAppsCategories(packageNames: List<String>): List<AppCategory>
}
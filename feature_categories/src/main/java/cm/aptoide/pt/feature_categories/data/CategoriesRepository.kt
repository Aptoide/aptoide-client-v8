package cm.aptoide.pt.feature_categories.data

import cm.aptoide.pt.feature_categories.domain.AppCategory
import cm.aptoide.pt.feature_categories.domain.Category

interface CategoriesRepository {

  suspend fun getCategoriesList(url: String): List<Category>

  suspend fun getHomeBundleActionListCategories(bundleTag: String): Pair<List<Category>, String>

  suspend fun getAppsCategories(packageNames: List<String>): List<AppCategory>
}

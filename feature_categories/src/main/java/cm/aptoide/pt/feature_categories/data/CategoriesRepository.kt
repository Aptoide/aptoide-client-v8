package cm.aptoide.pt.feature_categories.data

import cm.aptoide.pt.feature_categories.domain.AppCategory
import cm.aptoide.pt.feature_categories.domain.Category
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {

  fun getCategoriesList(url: String): Flow<List<Category>>

  fun getHomeBundleActionListCategories(bundleTag: String): Flow<Pair<List<Category>, String>>

  suspend fun getAppsCategories(packageNames: List<String>): List<AppCategory>
}

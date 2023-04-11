package cm.aptoide.pt.feature_categories.data

import cm.aptoide.pt.feature_categories.data.network.model.CategoryJson
import cm.aptoide.pt.feature_categories.data.network.service.CategoriesRemoteService
import cm.aptoide.pt.feature_categories.domain.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class AptoideCategoriesRepository @Inject constructor(
  private val categoriesService: CategoriesRemoteService
) : CategoriesRepository {

  override fun getCategoriesList(url: String): Flow<List<Category>> = flow<List<Category>> {
    if (url.isEmpty()) {
      throw IllegalStateException()
    }
    val query = url.split("store/groups/get/")[1]
    val response = categoriesService.getCategoriesList(query)
      .datalist?.list?.map {
        it.toDomainModel()
      }
      ?: throw IllegalStateException()
    emit(response)
  }.flowOn(Dispatchers.IO)
}

fun CategoryJson.toDomainModel() = Category(
  id = id,
  name = name,
  title = title,
  icon = icon,
  graphic = graphic,
  background = background
)

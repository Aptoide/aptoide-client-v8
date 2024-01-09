package cm.aptoide.pt.feature_home.domain

import cm.aptoide.pt.aptoide_network.domain.UrlsCacheInitializer
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_home.data.WidgetsRepository

class AptoideUrlsCacheInitializer(
  private val widgetsRepository: WidgetsRepository,
  private val articlesRepository: EditorialRepository,
) : UrlsCacheInitializer {

  override suspend fun initialise(): MutableMap<String, String> {
    val cache = mutableMapOf<String, String>()
    cache[BundlesUseCase.WIDGETS_TAG] = ""

    widgetsRepository.getStoreWidgets()
      .onEach { it.cacheUrls(cache::set) }
      .find { it.type == WidgetType.ACTION_ITEM }
      ?.view
      ?.let { url ->
        articlesRepository
          .getArticlesMeta(
            editorialWidgetUrl = url,
            subtype = null
          )
          .forEach { it.cacheUrls(cache::set) }
      }
    return cache
  }
}

fun Widget.cacheUrls(save: (String, String) -> Unit) {
  view?.let { save(tag, it) }
  action?.forEach {
    if (it.tag.endsWith("-more")) {
      save(it.tag, it.url + "/limit=50")
    } else {
      save(it.tag, it.url)
    }
  }
}

package cm.aptoide.pt.feature_home.domain

import cm.aptoide.pt.aptoide_network.domain.UrlsCacheInitializer
import cm.aptoide.pt.feature_bonus.data.BonusData
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.usecase.tagsUrls
import cm.aptoide.pt.feature_home.data.WidgetsRepository

class AptoideUrlsCacheInitializer(
  private val widgetsRepository: WidgetsRepository,
  private val articlesRepository: EditorialRepository,
) : UrlsCacheInitializer {

  override suspend fun initialise(): Map<String, String> = widgetsRepository.getStoreWidgets()
    .let { list ->
      list.tagsUrls +
        (BundlesUseCase.WIDGETS_TAG to "") +
        (list.find { it.type == WidgetType.ACTION_ITEM }
          ?.view
          ?.let { url ->
            articlesRepository
              .getArticlesMeta(editorialWidgetUrl = url, subtype = null)
              .tagsUrls(url)
          } ?: emptyMap()) +
        (list.find { bundle ->
          bundle.type == WidgetType.APPC_BANNER
        }?.let { bonusBundle ->
          BonusData.setBonusData(bonusBundle.title, bonusBundle.tag)
          emptyMap()
        } ?: mapOf(
          "bonus-banner-more" to "listApps/store_id=3613731/group_id=15614123/order=rand"
        )) +
        mapOf("ab-test-companion-app-bundle" to "listApps/store_name=aptoide-games/group_name=enjoying-roblox/nocache=1/aab=1")
    }
}

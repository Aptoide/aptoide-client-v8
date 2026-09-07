package com.aptoide.android.aptoidegames.editorial.data

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppRepository
import cm.aptoide.pt.feature_apps.data.emptyApp
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.data.model.Media
import cm.aptoide.pt.feature_editorial.domain.Action
import cm.aptoide.pt.feature_editorial.domain.Article
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.domain.ArticleType
import cm.aptoide.pt.feature_editorial.domain.Paragraph
import cm.aptoide.pt.feature_editorial.domain.RELATED_ARTICLE_CACHE_ID_PREFIX
import com.aptoide.android.aptoidegames.editorial.data.model.Block
import com.aptoide.android.aptoidegames.editorial.data.model.EditorialListItem
import com.aptoide.android.aptoidegames.editorial.data.model.EditorialResponse
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Backs the existing [EditorialRepository] contract with the new public Editorial service,
 * mapping the new DTOs onto the existing domain models ([Article]/[ArticleMeta]/[Paragraph])
 * so the whole existing editorial UI works unchanged. Wired only on the dev/test build.
 */
class EditorialServiceRepository(
  private val api: EditorialServiceApi,
  private val appRepository: AppRepository,
  // Resolves the card's category chip from a localized resource at the composition root, so no
  // user-facing strings live in this data layer (the contract carries no caption/flair field).
  private val captionLabeler: (ArticleType) -> String,
) : EditorialRepository {

  override suspend fun getLatestArticle(): List<ArticleMeta> =
    api.getEditorials(limit = 1).items.map { it.toArticleMeta() }

  override suspend fun getArticlesMeta(
    editorialWidgetUrl: String,
    subtype: String?,
  ): List<ArticleMeta> =
    // The v7 widget url is irrelevant to this service; only the (optional) subtype filter maps.
    api.getEditorials(subtype = subtype.toContractSubtype()).items.map { it.toArticleMeta() }

  override suspend fun getRelatedArticlesMeta(packageName: String): List<ArticleMeta> =
    api.getEditorials(app = packageName).items.map { it.toArticleMeta() }

  override suspend fun getArticle(editorialUrl: String): Article =
    api.getEditorial(extractSlug(editorialUrl)).toArticle()

  private suspend fun EditorialResponse.toArticle(): Article = Article(
    id = slug,
    title = title,
    caption = summary.orEmpty(),
    subtype = subtype.toArticleType(),
    image = coverImageUrl.orEmpty(),
    date = publishedAt.toUiDate(),
    views = viewCount,
    relatedTag = RELATED_ARTICLE_CACHE_ID_PREFIX + slug,
    content = blocks.filterNotNull().toParagraphs(),
  )

  /** One [Paragraph] per block, in order. app_embed apps are resolved in parallel. */
  private suspend fun List<Block>.toParagraphs(): List<Paragraph> = coroutineScope {
    val blocks = this@toParagraphs
    val resolvedApps: Map<String, Deferred<App>> = blocks.filterIsInstance<Block.AppEmbed>()
      .distinctBy { it.packageName }
      .associate { embed -> embed.packageName to async { embed.resolveApp() } }

    blocks.map { block ->
      when (block) {
        is Block.Heading -> paragraphOf(title = block.text)
        is Block.Paragraph -> paragraphOf(message = block.text)
        is Block.Image -> paragraphOf(
          media = listOf(Media(type = "image", description = block.alt, image = block.url, url = null))
        )
        is Block.Video -> paragraphOf(
          media = listOf(Media(type = "video_webview", description = null, image = null, url = block.url))
        )
        is Block.CtaAction -> paragraphOf(action = Action(title = block.label, url = block.url))
        is Block.AppEmbed -> paragraphOf(
          app = resolvedApps[block.packageName]?.await() ?: block.toApp()
        )
      }
    }
  }

  /** Full store app for a working card (install + detail); falls back to the embed on failure. */
  private suspend fun Block.AppEmbed.resolveApp(): App =
    runCatching { appRepository.getApp(packageName) }.getOrElse { toApp() }

  private fun Block.AppEmbed.toApp(): App = emptyApp.copy(
    name = name,
    packageName = packageName,
    icon = iconUrl.orEmpty(),
    description = summary,
  )

  private fun EditorialListItem.toArticleMeta(): ArticleMeta {
    val type = subtype.toArticleType()
    return ArticleMeta(
      id = slug,
      title = title,
      url = slug,
      caption = captionLabeler(type),
      summary = summary.orEmpty(),
      image = coverImageUrl.orEmpty(),
      subtype = type,
      date = publishedAt.toUiDate(),
      views = viewCount,
    )
  }
}

private fun paragraphOf(
  title: String? = null,
  message: String? = null,
  action: Action? = null,
  media: List<Media> = emptyList(),
  app: App? = null,
): Paragraph = Paragraph(title = title, message = message, action = action, media = media, app = app)

private val UI_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

/**
 * The existing editorial UI parses dates as "yyyy-MM-dd HH:mm:ss" (device tz); the new service
 * returns ISO-8601 instants. Always returns a parseable date so the detail screen can't crash;
 * a missing/malformed date falls back to a deterministic sentinel (EPOCH) rather than "now", so
 * undated content never looks freshly published or reorders by wall-clock.
 */
private fun String?.toUiDate(): String {
  val instant = this?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: Instant.EPOCH
  return UI_DATE_FORMATTER.format(instant.atZone(ZoneId.systemDefault()))
}

private fun String?.toArticleType(): ArticleType = when (this?.lowercase()) {
  "app_of_the_week" -> ArticleType.APP_OF_THE_WEEK
  "game_of_the_week" -> ArticleType.GAME_OF_THE_WEEK
  "collection" -> ArticleType.COLLECTION
  "news" -> ArticleType.NEWS
  "new_app" -> ArticleType.NEW_APP
  else -> ArticleType.OTHER
}

/** Maps an [ArticleType]-style name (e.g. "COLLECTION") to the contract enum, dropping unknowns. */
private fun String?.toContractSubtype(): String? = this?.lowercase()?.takeIf {
  it in CONTRACT_SUBTYPES
}

private val CONTRACT_SUBTYPES =
  setOf("app_of_the_week", "game_of_the_week", "collection", "news", "new_app")

/**
 * Recovers the bare slug from whatever [cm.aptoide.pt.feature_editorial.domain.usecase.ArticleUseCase]
 * passes: a cached value, or "card/get/<id|slug>=<slug>/store_name=<store>".
 */
private fun extractSlug(editorialUrl: String): String =
  editorialUrl
    .substringAfter("card/get/", editorialUrl)
    .substringBefore("/store_name")
    .substringAfter("id=")
    .substringAfter("slug=")
    .substringBefore("/")
    .trim()

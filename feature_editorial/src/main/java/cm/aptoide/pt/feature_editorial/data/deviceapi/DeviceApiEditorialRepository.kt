package cm.aptoide.pt.feature_editorial.data.deviceapi

import cm.aptoide.pt.device_api.error.deviceApiCall
import cm.aptoide.pt.feature_apps.data.emptyApp
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.data.model.Media
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.AppEmbedBlockResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.CtaActionBlockResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.EditorialBlockResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.EditorialListItemResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.EditorialResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.HeadingBlockResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.ImageBlockResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.ParagraphBlockResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.VideoBlockResponse
import cm.aptoide.pt.feature_editorial.domain.Action
import cm.aptoide.pt.feature_editorial.domain.Article
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.domain.ArticleType
import cm.aptoide.pt.feature_editorial.domain.Paragraph
import cm.aptoide.pt.feature_editorial.domain.RELATED_ARTICLE_CACHE_ID_PREFIX
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Device-API editorial (aptoideGamesDev only). `GET /editorials` for lists,
 * `GET /editorials/{slug}` for detail. Slugs replace v7 card URLs; [getArticle]
 * extracts the slug from whatever url/source string the caller passes. `getRelatedArticlesMeta`
 * (→ `?app=`) fills the app-view Related tab. Comments are deferred.
 */
class DeviceApiEditorialRepository(
  private val service: DeviceApiEditorialService,
) : EditorialRepository {

  override suspend fun getLatestArticle(): List<ArticleMeta> =
    deviceApiCall { service.listEditorials(limit = 1) }.items.orEmpty().map { it.toArticleMeta() }

  override suspend fun getArticle(editorialUrl: String): Article =
    deviceApiCall { service.getEditorial(extractSlug(editorialUrl)) }.toArticle()

  override suspend fun getArticlesMeta(
    editorialWidgetUrl: String,
    subtype: String?,
  ): List<ArticleMeta> = deviceApiCall {
    service.listEditorials(subtype = subtype?.let(::toDeviceSubtype), limit = 25)
  }.items.orEmpty().map { it.toArticleMeta() }

  override suspend fun getRelatedArticlesMeta(packageName: String): List<ArticleMeta> =
    deviceApiCall { service.listEditorials(app = packageName, limit = 10) }
      .items.orEmpty().map { it.toArticleMeta() }
}

/**
 * Pulls the slug out of whatever identifier the caller passes: a v7-style card url
 * (`card/get/{slug}/store_name=…`), a `key={slug}` wrapper (`id=…`, seen from the
 * article route / urls cache), a cached slug, or a raw slug.
 */
internal fun extractSlug(editorialUrl: String): String {
  var raw = editorialUrl.trim()
  raw = when {
    "card/get/" in raw -> raw.substringAfter("card/get/")
    "card/" in raw -> raw.substringAfter("card/")
    "editorials/" in raw -> raw.substringAfter("editorials/")
    else -> raw
  }
  raw = raw.substringBefore("/store_name").substringBefore("/").trim()
  // strip a leading `key=` wrapper, e.g. "id=game-of-the-week-roblox"; a plain slug
  // has no '=', so substringAfterLast returns it unchanged.
  return raw.substringAfterLast("=").trim()
}

private fun EditorialListItemResponse.toArticleMeta() = ArticleMeta(
  id = slug.orEmpty(),
  title = title.orEmpty(),
  url = slug.orEmpty(),
  caption = summary.orEmpty(),
  summary = summary.orEmpty(),
  image = coverImageUrl.orEmpty(),
  subtype = toArticleType(subtype),
  date = formatEditorialDate(publishedAt),
  views = viewCount?.toLong() ?: 0L,
)

private fun EditorialResponse.toArticle() = Article(
  id = slug.orEmpty(),
  title = title.orEmpty(),
  caption = summary.orEmpty(),
  subtype = toArticleType(subtype),
  image = coverImageUrl.orEmpty(),
  date = formatEditorialDate(publishedAt),
  views = viewCount?.toLong() ?: 0L,
  relatedTag = RELATED_ARTICLE_CACHE_ID_PREFIX + slug.orEmpty(),
  content = blocks.orEmpty().filterNotNull().map { it.toParagraph() },
)

private fun EditorialBlockResponse.toParagraph(): Paragraph = when (this) {
  is HeadingBlockResponse ->
    Paragraph(title = text, message = null, action = null, media = emptyList(), app = null)

  is ParagraphBlockResponse ->
    Paragraph(title = null, message = text, action = null, media = emptyList(), app = null)

  is ImageBlockResponse -> Paragraph(
    title = null, message = null, action = null,
    media = listOf(Media(type = "image", description = alt, image = url, url = null)),
    app = null,
  )

  is VideoBlockResponse -> Paragraph(
    title = null, message = null, action = null,
    media = listOf(Media(type = "video", description = null, image = null, url = url)),
    app = null,
  )

  is CtaActionBlockResponse -> Paragraph(
    title = null, message = null,
    action = Action(title = label.orEmpty(), url = url.orEmpty()),
    media = emptyList(), app = null,
  )

  is AppEmbedBlockResponse -> Paragraph(
    title = null, message = summary, action = null, media = emptyList(),
    app = emptyApp.copy(
      packageName = packageName.orEmpty(),
      name = name.orEmpty(),
      icon = iconUrl.orEmpty(),
      description = summary,
    ),
  )
}

private val EDITORIAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

/**
 * Converts the device's ISO-8601 `published_at` (e.g. `2026-06-29T11:58:55.294386Z`)
 * to the `yyyy-MM-dd HH:mm:ss` format the client's `TextFormatter`/`DateUtils` parse.
 * Falls back to now on any parse failure — must never return an unparseable string
 * (the date renderer throws on a bad format).
 */
private fun formatEditorialDate(isoDate: String?): String {
  val instant = isoDate?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: Instant.now()
  return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(EDITORIAL_DATE_FORMAT)
}

private fun toArticleType(subtype: String?): ArticleType = when (subtype?.lowercase()) {
  "app_of_the_week" -> ArticleType.APP_OF_THE_WEEK
  "game_of_the_week" -> ArticleType.GAME_OF_THE_WEEK
  "collection" -> ArticleType.COLLECTION
  "news" -> ArticleType.NEWS
  "new_app" -> ArticleType.NEW_APP
  else -> ArticleType.OTHER
}

/** Maps a caller subtype (device or v7 enum-name) to a device subtype, or null if unknown. */
private fun toDeviceSubtype(subtype: String): String? = when (subtype.lowercase()) {
  "app_of_the_week" -> "app_of_the_week"
  "game_of_the_week" -> "game_of_the_week"
  "collection" -> "collection"
  "news" -> "news"
  "new_app" -> "new_app"
  else -> null
}

package cm.aptoide.pt.feature_editorial.domain

import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_editorial.data.model.Media
import cm.aptoide.pt.feature_editorial.domain.ArticleType.OTHER
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import kotlin.random.nextInt

const val ARTICLE_CACHE_ID_PREFIX = "editorial-"

data class Article(
  val id: String,
  val title: String,
  val caption: String,
  val subtype: ArticleType,
  val image: String,
  val date: String,
  val views: Long,
  val content: List<Paragraph>,
)

data class Paragraph(
  val title: String?,
  val message: String?,
  val action: Action?,
  val media: List<Media>,
  val app: App?,
)

data class ArticleMeta(
  val id: String,
  val title: String,
  val url: String,
  val caption: String,
  val summary: String,
  val image: String,
  val subtype: ArticleType,
  val date: String,
  val views: Long,
) {

  fun cacheUrls(save: (String, String) -> Unit) {
    save(ARTICLE_CACHE_ID_PREFIX + id, url)
  }
}

enum class ArticleType {
  APP_OF_THE_WEEK,
  COLLECTION,
  GAME_OF_THE_WEEK,
  NEW_APP,
  NEWS,
  OTHER
}

data class Action(val title: String, val url: String)

val randomArticle
  get() = Article(
    id = "${Random.nextInt(1..1000)}",
    title = getRandomString(range = 2..6, capitalize = true),
    caption = getRandomString(range = 1..3, capitalize = true),
    subtype = ArticleType.values().getOrElse(Random.nextInt(5)) { OTHER },
    image = "random",
    date = LocalDateTime.now()
      .minusDays(Random.nextLong(30))
      .format(DateTimeFormatter.ofPattern("uuuu-MM-dd hh:mm:ss")).toString(),
    views = Random.nextLong(50000L),
    content = List(Random.nextInt(1..4)) {
      Paragraph(
        title = getRandomString(range = 1..2, capitalize = true),
        message = getRandomString(range = 15..150),
        action = null,
        media = List(Random.nextInt(1..2)) {
          Media(
            type = "image",
            description = getRandomString(range = 0..15),
            image = "random",
            url = null
          )
        },
        app = if (Random.nextBoolean()) {
          randomApp
        } else {
          null
        }
      )
    }
  )

val randomArticleMeta
  get() = ArticleMeta(
    id = "${Random.nextInt(1..1000)}",
    title = getRandomString(range = 2..6, capitalize = true),
    url = "www.${getRandomString(range = 1..3, separator = ".")}.com",
    caption = getRandomString(range = 1..3, capitalize = true),
    summary = getRandomString(range = 10..200),
    image = "random",
    subtype = ArticleType.values().getOrElse(Random.nextInt(5)) { OTHER },
    date = LocalDateTime.now()
      .minusDays(Random.nextLong(30))
      .format(DateTimeFormatter.ofPattern("uuuu-MM-dd hh:mm:ss")).toString(),
    views = Random.nextLong(50000L)
  )

package cm.aptoide.pt.feature_editorial.data

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_apps.data.toDomainModel
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_editorial.data.model.ContentAction
import cm.aptoide.pt.feature_editorial.data.model.ContentJSON
import cm.aptoide.pt.feature_editorial.data.model.Data
import cm.aptoide.pt.feature_editorial.data.model.EditorialDetailJson
import cm.aptoide.pt.feature_editorial.data.model.EditorialJson
import cm.aptoide.pt.feature_editorial.domain.Action
import cm.aptoide.pt.feature_editorial.domain.Article
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.domain.ArticleType
import cm.aptoide.pt.feature_editorial.domain.Paragraph
import cm.aptoide.pt.feature_editorial.domain.RELATED_ARTICLE_CACHE_ID_PREFIX
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AptoideEditorialRepository @Inject constructor(
  private val campaignRepository: CampaignRepository,
  private val editorialRemoteDataSource: Retrofit,
  private val storeName: String,
) : EditorialRepository {

  override suspend fun getLatestArticle(): List<ArticleMeta> =
    editorialRemoteDataSource.getLatestEditorial()
      .datalist?.list?.map(EditorialJson::toDomainModel) ?: throw IllegalStateException()

  override suspend fun getArticle(editorialUrl: String): Article =
    editorialUrl.split("card/")[1]
      .let { editorialRemoteDataSource.getArticleDetail(it) }
      .data?.toDomainModel(campaignRepository)
      ?: throw IllegalStateException()

  override suspend fun getArticlesMeta(
    editorialWidgetUrl: String,
    subtype: String?,
  ): List<ArticleMeta> {
    if (editorialWidgetUrl.contains("cards/")) {
      return editorialRemoteDataSource
        .getArticlesMeta(editorialWidgetUrl.split("cards/")[1], subtype)
        .datalist?.list?.map(EditorialJson::toDomainModel)
        ?: throw IllegalStateException()
    } else {
      throw IllegalStateException()
    }
  }

  override suspend fun getRelatedArticlesMeta(packageName: String): List<ArticleMeta> =
    editorialRemoteDataSource.getRelatedArticlesMeta(packageName, storeName)
      .datalist?.list?.map(EditorialJson::toDomainModel)
      ?: throw IllegalStateException()

  internal interface Retrofit {
    @GET("cards/get/type=CURATION_1/aptoide_uid=0/limit=1")
    suspend fun getLatestEditorial(
      @Query("aab") aab: Int = 1,
    ): BaseV7DataListResponse<EditorialJson>

    @GET("cards/{widgetUrl}/aptoide_uid=0/")
    suspend fun getArticlesMeta(
      @Path("widgetUrl", encoded = true) widgetUrl: String,
      @Query("subtype") subtype: String?,
      @Query("aab") aab: Int = 1,
    ): BaseV7DataListResponse<EditorialJson>

    @GET("card/{widgetUrl}/aptoide_uid=0/")
    suspend fun getArticleDetail(
      @Path("widgetUrl", encoded = true) widgetUrl: String,
      @Query("aab") aab: Int = 1,
    ): EditorialDetailJson

    @GET("cards/get/type=CURATION_1/aptoide_uid=0/limit=10")
    suspend fun getRelatedArticlesMeta(
      @Query(value = "package_name", encoded = true) packageName: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
    ): BaseV7DataListResponse<EditorialJson>
  }
}

private fun Data.toDomainModel(
  campaignRepository: CampaignRepository,
): Article = Article(
  id = id,
  title = title,
  caption = caption,
  subtype = ArticleType.valueOf(subtype),
  image = background,
  date = date,
  views = views,
  relatedTag = RELATED_ARTICLE_CACHE_ID_PREFIX + id,
  content = map(content, campaignRepository)
)

fun map(
  content: List<ContentJSON>,
  campaignRepository: CampaignRepository,
): List<Paragraph> {
  val contentList = ArrayList<Paragraph>()
  val randomAdListId = UUID.randomUUID().toString()

  content.forEach {
    contentList.add(
      Paragraph(
        title = it.title,
        message = it.message,
        action = it.action?.toDomainModel(),
        media = it.media.map { media ->
          media.copy(
            url = media.url?.trim()?.replace(oldValue = " ", newValue = ""),
            image = media.image?.trim()?.replace(oldValue = " ", newValue = "")
          )
        },
        app = it.app?.toDomainModel(
          campaignRepository = campaignRepository,
          adListId = randomAdListId
        )
      )
    )
  }

  return contentList
}

private fun ContentAction.toDomainModel(): Action = Action(title = title, url = url)

private fun EditorialJson.toDomainModel(): ArticleMeta = ArticleMeta(
  id = card_id,
  title = title,
  url = url,
  caption = message,
  subtype = ArticleType.valueOf(subtype),
  summary = summary,
  image = icon,
  date = date,
  views = views
)

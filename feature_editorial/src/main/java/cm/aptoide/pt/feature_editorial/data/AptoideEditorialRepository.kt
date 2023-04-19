package cm.aptoide.pt.feature_editorial.data

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_apps.data.toDomainModel
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.data.CampaignUrlNormalizer
import cm.aptoide.pt.feature_editorial.data.model.*
import cm.aptoide.pt.feature_editorial.domain.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AptoideEditorialRepository @Inject constructor(
  private val campaignRepository: CampaignRepository,
  private val campaignUrlNormalizer: CampaignUrlNormalizer,
  private val editorialRemoteDataSource: Retrofit,
  private val storeName: String
) : EditorialRepository {

  private val cachedUrls = mutableMapOf<String, String>()

  override suspend fun getLatestArticle(): List<ArticleMeta> =
    editorialRemoteDataSource.getLatestEditorial()
      .datalist?.list?.map(EditorialJson::toDomainModel) ?: throw IllegalStateException()

  override suspend fun getArticle(articleId: String): Article = cachedUrls[articleId]
    ?.let { it.split("card/")[1] }
    ?.let { editorialRemoteDataSource.getArticleDetail(it) }
    ?.data?.toDomainModel(campaignRepository, campaignUrlNormalizer)
    ?: throw IllegalStateException()

  override suspend fun getArticlesMeta(
    editorialWidgetUrl: String,
    subtype: String?
  ): List<ArticleMeta> {
    if (editorialWidgetUrl.contains("cards/")) {
      return editorialRemoteDataSource
        .getArticlesMeta(editorialWidgetUrl.split("cards/")[1], subtype)
        .datalist?.list?.map(EditorialJson::toDomainModel)
        ?.onEach { cachedUrls[it.id] = it.url } ?: throw IllegalStateException()
    } else {
      throw IllegalStateException()
    }
  }

  override suspend fun getRelatedArticlesMeta(packageName: String): List<ArticleMeta> =
    editorialRemoteDataSource.getRelatedArticlesMeta(packageName, storeName)
      .datalist?.list?.map(EditorialJson::toDomainModel)
      ?.onEach { cachedUrls[it.id] = it.url } ?: throw IllegalStateException()

  internal interface Retrofit {
    @GET("cards/get/type=CURATION_1/aptoide_uid=0/limit=1")
    suspend fun getLatestEditorial(
      @Query("aab") aab: Int = 1
    ): BaseV7DataListResponse<EditorialJson>

    @GET("cards/{widgetUrl}/aptoide_uid=0/")
    suspend fun getArticlesMeta(
      @Path("widgetUrl", encoded = true) widgetUrl: String,
      @Query("subtype") subtype: String?,
      @Query("aab") aab: Int = 1
    ): BaseV7DataListResponse<EditorialJson>

    @GET("card/{widgetUrl}/aptoide_uid=0/")
    suspend fun getArticleDetail(
      @Path("widgetUrl", encoded = true) widgetUrl: String,
      @Query("aab") aab: Int = 1
    ): EditorialDetailJson

    @GET("cards/get/type=CURATION_1/aptoide_uid=0/limit=10")
    suspend fun getRelatedArticlesMeta(
      @Query(value = "package_name", encoded = true) packageName: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1
    ): BaseV7DataListResponse<EditorialJson>
  }
}

private fun Data.toDomainModel(
  campaignRepository: CampaignRepository? = null,
  campaignUrlNormalizer: CampaignUrlNormalizer? = null
): Article = Article(
  id = this.id,
  title = this.title,
  caption = this.caption,
  subtype = ArticleType.valueOf(this.subtype),
  image = this.background,
  date = this.date,
  views = this.views,
  content = map(this.content, campaignRepository, campaignUrlNormalizer)
)

fun map(
  content: List<ContentJSON>,
  campaignRepository: CampaignRepository? = null,
  campaignUrlNormalizer: CampaignUrlNormalizer? = null
): List<Paragraph> {
  val contentList = ArrayList<Paragraph>()

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
        app = it.app?.toDomainModel(campaignRepository, campaignUrlNormalizer)
      )
    )
  }

  return contentList
}

private fun ContentAction.toDomainModel(): Action = Action(title = this.title, url = this.url)

private fun EditorialJson.toDomainModel(): ArticleMeta = ArticleMeta(
  id = this.card_id,
  title = this.title,
  url = this.url,
  caption = this.message,
  subtype = ArticleType.valueOf(this.subtype),
  summary = this.summary,
  image = this.icon,
  date = this.date,
  views = this.views
)

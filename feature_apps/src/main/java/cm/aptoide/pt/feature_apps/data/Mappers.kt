package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.data.model.AabJSON
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_apps.data.model.CampaignUrl
import cm.aptoide.pt.feature_apps.data.model.CampaignUrls
import cm.aptoide.pt.feature_apps.data.model.DynamicSplitJSON
import cm.aptoide.pt.feature_apps.data.model.ObbJSON
import cm.aptoide.pt.feature_apps.data.model.SplitJSON
import cm.aptoide.pt.feature_apps.data.model.VideoTypeJSON
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_apps.domain.Votes
import cm.aptoide.pt.feature_campaigns.CampaignImpl
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.CampaignTuple
import java.util.UUID
import javax.inject.Inject

// TODO: Review the relationship between Campaigns and App modules

interface AppMapper {
  fun map(
    appJSON: AppJSON,
    adListId: String = UUID.randomUUID().toString()
  ): App
}

interface AppsListMapper {
  fun map(appJSONs: List<AppJSON>): List<App>
}

class AptoideAppMapper @Inject constructor(
  private val campaignRepository: CampaignRepository,
) : AppMapper {
  override fun map(
    appJSON: AppJSON,
    adListId: String,
  ): App = appJSON.toDomainModel(
    campaignRepository = campaignRepository,
    adListId = adListId
  )
}

class AptoideAppsListMapper @Inject constructor(
  private val appMapper: AppMapper,
) : AppsListMapper {
  override fun map(appJSONs: List<AppJSON>): List<App> {
    val randomAdListId = UUID.randomUUID().toString()
    return appJSONs.map { appMapper.map(it, randomAdListId) }
  }
}

private fun AppJSON.toDomainModel(
  campaignRepository: CampaignRepository,
  adListId: String,
) = App(
  appId = this.id!!,
  name = this.name!!,
  packageName = this.packageName!!,
  md5 = this.file.md5sum,
  icon = this.icon!!,
  featureGraphic = this.graphic.toString(),
  isAppCoins = this.appcoins!!.billing,
  bdsFlags = this.appcoins?.flags,
  malware = this.file.malware?.rank,
  rating = Rating(
    avgRating = this.stats.rating.avg,
    totalVotes = this.stats.rating.total,
    votes = this.stats.rating.votes?.map { Votes(it.value, it.count) }
  ),
  pRating = Rating(
    avgRating = this.stats.prating.avg,
    totalVotes = this.stats.prating.total,
    votes = this.stats.prating.votes?.map { Votes(it.value, it.count) }
  ),
  downloads = this.stats.downloads,
  pDownloads = this.stats.pdownloads,
  versionName = this.file.vername,
  versionCode = this.file.vercode,
  screenshots = this.media?.screenshots,
  description = this.media?.description,
  news = if (this.media?.news.isNullOrEmpty()) null else this.media.news,
  videos = this.media?.videos?.filter { it.type == VideoTypeJSON.YOUTUBE }?.map { it.url }
    ?: this.media?.video?.takeIf { it.source == "youtube" }?.url?.let { listOf(it) }
    ?: emptyList(),
  store = Store(
    storeName = this.store.name,
    icon = this.store.avatar,
    apps = this.store.stats?.apps,
    subscribers = this.store.stats?.subscribers,
    downloads = this.store.stats?.downloads
  ),
  releaseDate = this.added,
  modifiedDate = this.modified!!,
  updateDate = this.updated,
  releaseUpdateDate = this.release?.updated,
  website = this.developer?.website,
  email = this.developer?.email,
  privacyPolicy = this.developer?.privacy,
  permissions = this.file.used_permissions,
  file = File(
    md5 = this.file.md5sum,
    size = this.file.filesize,
    path = this.file.path ?: "",
    path_alt = this.file.path_alt ?: ""
  ),
  aab = aab.toDomainModel(),
  obb = obb.toDomainModel(),
  developerName = this.developer?.name,
  signature = this.file.signature?.sha1,
  campaigns = this.urls.mapCampaigns(campaignRepository)
    ?.apply { this.adListId = adListId }
)

private fun CampaignUrls.mapCampaigns(
  campaignRepository: CampaignRepository?,
): CampaignImpl? {
  if (campaignRepository != null) {
    return CampaignImpl(
      impressions = this.impression.toCampaignTupleList(),
      clicks = this.click.toCampaignTupleList(),
      downloads = this.download.toCampaignTupleList(),
      repository = campaignRepository,
    )
  }
  return null
}

private fun List<CampaignUrl>?.toCampaignTupleList(): List<CampaignTuple> {
  return this?.map { campaignUrl -> CampaignTuple(campaignUrl.name, campaignUrl.url) }
    ?: emptyList()
}

private fun ObbJSON?.toDomainModel(): Obb? = this?.run {
  val main = File(
    _fileName = main.filename,
    md5 = main.md5sum,
    size = main.filesize,
    path = main.path ?: "",
    path_alt = ""
  )
  if (patch != null) {
    Obb(
      main = main,
      patch = File(
        _fileName = patch.filename,
        md5 = patch.md5sum,
        size = patch.filesize,
        path = patch.path ?: "",
        path_alt = ""
      )
    )
  } else {
    Obb(main = main, patch = null)
  }
}

private fun AabJSON?.toDomainModel() = this?.run {
  Aab(
    requiredSplitTypes = requiredSplitTypes,
    baseSplits = splits.map(SplitJSON::toDomainModel)
  )
}

fun DynamicSplitJSON.toDomainModel(): DynamicSplit = DynamicSplit(
  type = DynamicSplit.Type.valueOf(type),
  deliveryType = deliveryTypes.toSet().run { find { it == "INSTALL_TIME" } ?: first() },
  file = File(
    md5 = this.md5sum,
    size = this.filesize,
    path = this.path,
    path_alt = ""
  ),
  splits = this.splits.map { it.toDomainModel() }
)

fun SplitJSON.toDomainModel() = Split(
  type = type,
  file = File(
    md5 = md5sum,
    size = filesize,
    path = path,
    path_alt = ""
  )
)

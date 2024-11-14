package com.aptoide.android.aptoidegames.chatbot.data

import androidx.annotation.Keep
import cm.aptoide.pt.feature_apps.data.Aab
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.File
import cm.aptoide.pt.feature_apps.data.Obb
import cm.aptoide.pt.feature_apps.data.Split

import cm.aptoide.pt.aptoide_network.data.network.CacheConstants
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7Response
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_apps.data.model.VideoTypeJSON
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_apps.domain.Votes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import javax.inject.Inject

internal class GetAppApiRepositoryImpl @Inject constructor(
    private val appsRemoteDataSource: Retrofit,
    private val scope: CoroutineScope,
) : GetAppApiRepository {

    /** TODO remove this hardcoded garbage, i just need it to work*/
    override suspend fun getApp(
        packageName: String,
        bypassCache: Boolean,
    ): App =
        withContext(scope.coroutineContext) {
            appsRemoteDataSource.getApp(
                path = packageName,
                storeName = if (packageName != "com.appcoins.wallet") "apps" else null,
                bypassCache = if (bypassCache) CacheConstants.NO_CACHE else null
            )
                .nodes.meta.data
                .toDomainModelQuickChatbotFix()
        }

    internal interface Retrofit {
        @GET("app/get/")
        suspend fun getApp(
            @Query(value = "package_name", encoded = true) path: String,
            @Query("store_name") storeName: String? = null,
            @Query("aab") aab: Int = 1,
            @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?,
        ): GetAppResponse
    }
}

@Keep
internal data class GetAppResponse(var nodes: Nodes) : BaseV7Response()

@Keep
internal data class Nodes(
    var meta: GetAppMeta,
)

@Keep
internal data class GetAppMeta(val data: AppJSON)

/** this should not exist*/
fun AppJSON.toDomainModelQuickChatbotFix() = App(
    appId = this.id!!,
    name = this.name!!,
    packageName = this.packageName!!,
    appSize = this.file.filesize + (this.obb?.main?.filesize ?: 0) + (this.obb?.patch?.filesize ?: 0),
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
    versionName = this.file.vername,
    versionCode = this.file.vercode,
    screenshots = this.media?.screenshots?.map { it.url },
    description = this.media?.description,
    videos = this.media?.videos?.filter { it.type == VideoTypeJSON.YOUTUBE }?.map { it.url }
        ?: emptyList(),
    store = Store(
        storeName = this.store.name,
        icon = this.store.avatar,
        apps = this.store.stats?.apps,
        subscribers = this.store.stats?.subscribers,
        downloads = this.store.stats?.downloads
    ),
    releaseDate = this.added,
    updateDate = this.updated,
    releaseUpdateDate = this.release?.updated,
    website = this.developer?.website,
    email = this.developer?.email,
    privacyPolicy = this.developer?.privacy,
    permissions = this.file.used_permissions,
    file = File(
        vername = this.file.vername,
        vercode = this.file.vercode,
        md5 = this.file.md5sum,
        filesize = this.file.filesize,
        path = this.file.path ?: "",
        path_alt = this.file.path_alt ?: ""
    ),
    aab = mapAab(this),
    obb = mapObb(this),
    developerName = this.developer?.name,
    campaigns = null,
    news = null,
    pDownloads = this.stats.pdownloads,
    modifiedDate = this.modified ?: ""
)


private fun mapObb(app: AppJSON): Obb? =
    if (app.obb != null) {
        val main = File(
            _fileName = app.obb!!.main.filename,
            vername = app.file.vername,
            vercode = app.file.vercode,
            md5 = app.obb!!.main.md5sum,
            filesize = app.obb!!.main.filesize,
            path = app.obb!!.main.path ?: "",
            path_alt = ""
        )
        if (app.obb!!.patch != null) {
            Obb(
                main = main,
                patch = File(
                    _fileName = app.obb!!.patch?.filename,
                    vername = app.file.vername,
                    vercode = app.file.vercode,
                    md5 = app.obb!!.patch!!.md5sum,
                    filesize = app.obb!!.patch!!.filesize,
                    path = app.obb!!.patch!!.path ?: "",
                    path_alt = ""
                )
            )
        } else {
            Obb(main = main, patch = null)
        }
    } else {
        null
    }

private fun mapAab(app: AppJSON) = app.aab?.let {
    Aab(
        requiredSplitTypes = it.requiredSplitTypes,
        splits = it.splits.map { split ->
            Split(
                type = split.type,
                file = File(
                    _fileName = split.name,
                    vername = app.file.vername,
                    vercode = app.file.vercode,
                    md5 = split.md5sum,
                    filesize = split.filesize,
                    path = split.path,
                    path_alt = ""
                )
            )
        }
    )
}
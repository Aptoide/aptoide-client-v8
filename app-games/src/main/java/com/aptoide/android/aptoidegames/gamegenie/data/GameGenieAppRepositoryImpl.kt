package com.aptoide.android.aptoidegames.gamegenie.data

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppMapper
import com.aptoide.android.aptoidegames.gamegenie.domain.GameContext
import com.aptoide.android.aptoidegames.gamegenie.io_models.GetAppResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

internal class GameGenieAppRepositoryImpl @Inject constructor(
  private val appsRemoteDataSource: Retrofit,
  private val mapper: AppMapper,
  private val scope: CoroutineScope,
  private val packageManager: PackageManager,
) : GameGenieAppRepository {

  override suspend fun getApp(packageName: String): App =
    withContext(scope.coroutineContext) {
      appsRemoteDataSource.getApp(
        path = packageName,
      )
        .nodes.meta.data.let(mapper::map)
    }

  override fun getInstalledApps(): Flow<List<GameContext>> = flow {
    val pm = packageManager
    val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
      .filter { app -> (app.flags and ApplicationInfo.FLAG_SYSTEM) == 0 } // Exclude system apps
      .map { GameContext(it.loadLabel(pm).toString(), it.packageName) }
    emit(apps)
  }.flowOn(Dispatchers.IO)

  internal interface Retrofit {
    @GET("app/get/")
    suspend fun getApp(
      @Query(value = "package_name", encoded = true) path: String,
    ): GetAppResponse
  }
}

package com.aptoide.android.aptoidegames.newservices.di

import cm.aptoide.pt.aptoide_network.di.BackendOverride
import cm.aptoide.pt.aptoide_network.di.V7Backend
import cm.aptoide.pt.feature_updates.data.UpdatesRepository
import cm.aptoide.pt.feature_updates.domain.SilentUpdatePolicy
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.newservices.AnySilentUpdatePolicy
import com.aptoide.android.aptoidegames.newservices.PlaySilentUpdatePolicy
import com.aptoide.android.aptoidegames.newservices.PlayUpdatesRepository
import com.aptoide.android.aptoidegames.newservices.selectBackend
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * The updates of the Play build, behind the build switch like the catalog. They stay on v7,
 * which has the files, kept to the apps that install through Aptoide.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object NewServicesUpdatesModule {

  @Provides
  @Singleton
  @BackendOverride
  fun provideUpdatesRepository(@V7Backend v7: UpdatesRepository): UpdatesRepository =
    selectBackend(BuildConfig.NEW_SERVICES_ENABLED, v7) { PlayUpdatesRepository(v7) }

  @Provides
  @Singleton
  fun provideSilentUpdatePolicy(): SilentUpdatePolicy =
    selectBackend<SilentUpdatePolicy>(BuildConfig.NEW_SERVICES_ENABLED, AnySilentUpdatePolicy) {
      PlaySilentUpdatePolicy
    }
}

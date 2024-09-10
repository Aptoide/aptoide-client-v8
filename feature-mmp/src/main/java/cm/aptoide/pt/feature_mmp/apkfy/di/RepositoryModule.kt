package cm.aptoide.pt.feature_mmp.apkfy.di

import cm.aptoide.pt.feature_mmp.MMPPreferencesRepository
import cm.aptoide.pt.feature_mmp.apkfy.domain.ApkfyManagerImpl
import cm.aptoide.pt.feature_mmp.apkfy.repository.ApkfyRepository
import cm.aptoide.pt.feature_mmp.apkfy.repository.AptoideMMPRepository
import cm.aptoide.pt.feature_mmp.di.RetrofitMMP
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun provideApkfyRepository(@RetrofitMMP retrofitMMP: Retrofit): ApkfyRepository {
    return AptoideMMPRepository(
      mmpRemoteDataSource = retrofitMMP.create(AptoideMMPRepository.Retrofit::class.java)
    )
  }
}

package cm.aptoide.pt.installer.di

import cm.aptoide.pt.install_info_mapper.domain.CachingInstallPackageInfoMapper
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.installer.AptoideInstallPackageInfoMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface InstallerModule {

  @Singleton
  @Provides
  fun providePayloadMapper(
    installPackageInfoMapper: AptoideInstallPackageInfoMapper,
  ): InstallPackageInfoMapper = CachingInstallPackageInfoMapper(installPackageInfoMapper)
}

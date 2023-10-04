package cm.aptoide.pt.oem_extractor.di

import cm.aptoide.pt.oem_extractor.OemIdExtractorServiceImpl
import cm.aptoide.pt.oem_extractor.extractors.IExtractOemId
import cm.aptoide.pt.oem_extractor.extractors.OemIdExtractorV1
import cm.aptoide.pt.oem_extractor.extractors.OemIdExtractorV2
import cm.aptoide.pt.osp_handler.handler.OemIdExtractorService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ExtractorModule {

  @Singleton
  @Binds
  @ExtractorV1
  fun bindOemIdExtractorV1(extractor: OemIdExtractorV1): IExtractOemId

  @Singleton
  @Binds
  @ExtractorV2
  fun bindOemIdExtractorV2(extractor: OemIdExtractorV2): IExtractOemId

  @Singleton
  @Binds
  fun bindOemIdExtractorService(extractor: OemIdExtractorServiceImpl): OemIdExtractorService

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class ExtractorV1

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class ExtractorV2

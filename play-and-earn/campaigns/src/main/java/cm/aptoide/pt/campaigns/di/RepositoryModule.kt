package cm.aptoide.pt.campaigns.di

import android.content.Context
import androidx.room.Room
import cm.aptoide.pt.aptoide_network.di.ApiChainCatappultDomain
import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.campaigns.data.FakePaECampaignsRepository
import cm.aptoide.pt.campaigns.data.PaECampaignsApi
import cm.aptoide.pt.campaigns.data.PaECampaignsRepository
import cm.aptoide.pt.campaigns.data.database.PaECampaignsDatabase
import cm.aptoide.pt.campaigns.data.database.PaeMissionDao
import cm.aptoide.pt.wallet.authorization.data.WalletAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providePaECampaignsApi(
    @BaseOkHttp okHttpClient: OkHttpClient,
    @ApiChainCatappultDomain apiChainCatappultDomain: String,
    walletAuthInterceptor: WalletAuthInterceptor
  ): PaECampaignsApi {
    //TODO: Readd once the auth is fixed on missions
    //val client = okHttpClient.newBuilder().addInterceptor(walletAuthInterceptor).build()

    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(apiChainCatappultDomain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(PaECampaignsApi::class.java)
  }

  @Provides
  @Singleton
  fun providePaECampaignsRepository(): PaECampaignsRepository = FakePaECampaignsRepository()

  @Singleton
  @Provides
  fun providePaECampaignsDatabase(@ApplicationContext appContext: Context): PaECampaignsDatabase {
    return Room.databaseBuilder(appContext, PaECampaignsDatabase::class.java, "pae_campaigns.db")
      .build()
  }

  @Singleton
  @Provides
  fun providePaEMissionsDao(paECampaignsDatabase: PaECampaignsDatabase): PaeMissionDao {
    return paECampaignsDatabase.paeMissionDao()
  }
}

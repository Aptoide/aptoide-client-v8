package cm.aptoide.pt

import android.content.SharedPreferences
import cm.aptoide.accountmanager.AptoideAccountManager
import cm.aptoide.pt.dataprovider.WebService
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest
import cm.aptoide.pt.store.StoreCredentialsProvider
import cm.aptoide.pt.store.StoreUtilsProxy
import okhttp3.OkHttpClient
import rx.Completable
import rx.Observable

class FollowedStoresManager(private val storeCredentials: StoreCredentialsProvider,
                            private val defaultFollowedStores: List<String?>,
                            private val storeUtilsProxy: StoreUtilsProxy,
                            private val accountSettingsBodyInterceptorPoolV7: BodyInterceptor<BaseBody?>,
                            private val accountManager: AptoideAccountManager,
                            private val defaultClient: OkHttpClient,
                            private val tokenInvalidator: TokenInvalidator,
                            private val defaultSharedPreferences: SharedPreferences) {

  fun setDefaultFollowedStores(): Completable {
    return Observable.from(defaultFollowedStores)
        .flatMapCompletable { followedStoreName: String? ->
          val defaultStoreCredentials = storeCredentials[followedStoreName]
          storeUtilsProxy.addDefaultStore(
              GetStoreMetaRequest.of(defaultStoreCredentials,
                  accountSettingsBodyInterceptorPoolV7,
                  defaultClient, WebService.getDefaultConverter(),
                  tokenInvalidator,
                  defaultSharedPreferences), accountManager, defaultStoreCredentials)
        }
        .toList()
        .first()
        .toSingle()
        .toCompletable()
  }
}
package cm.aptoide.pt.v8engine.util;

import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by neuro on 14-10-2016.
 */

public class StoreUtils {

  public static final String PRIVATE_STORE_ERROR = "STORE-3";
  public static final String PRIVATE_STORE_WRONG_CREDENTIALS = "STORE-4";

  public static BaseRequestWithStore.StoreCredentials getStoreCredentials(long storeId) {

    StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);

    Store store = storeAccessor.get(storeId).toBlocking().first();

    String username = null;
    String passwordSha1 = null;

    if (store != null) {
      username = store.getUsername();
      passwordSha1 = store.getPasswordSha1();
      return new BaseRequestWithStore.StoreCredentials(storeId, store.getStoreName(), username,
          passwordSha1);
    }

    return new BaseRequestWithStore.StoreCredentials(storeId, username, passwordSha1);
  }

  public static BaseRequestWithStore.StoreCredentials getStoreCredentials(String storeName) {

    StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);

    Store store = storeAccessor.get(storeName).toBlocking().first();

    String username = null;
    String passwordSha1 = null;

    if (store != null) {
      username = store.getUsername();
      passwordSha1 = store.getPasswordSha1();

      return new BaseRequestWithStore.StoreCredentials(store.getStoreId(), storeName, username,
          passwordSha1);
    }

    return new BaseRequestWithStore.StoreCredentials(storeName, username, passwordSha1);
  }

  public static BaseRequestWithStore.StoreCredentials getStoreCredentialsFromUrl(String url) {

    V7Url v7Url = new V7Url(url);
    Long storeId = v7Url.getStoreId();
    String storeName = v7Url.getStoreName();

    if (storeId != null) {
      return getStoreCredentials(storeId);
    } else if (storeName != null) {
      return getStoreCredentials(storeName);
    } else {
      return new BaseRequestWithStore.StoreCredentials();
    }
  }

  public static BaseRequestWithStore.StoreCredentials getStoreCredentialsFromUrlOrNull(String url) {

    V7Url v7Url = new V7Url(url);
    Long storeId = v7Url.getStoreId();
    String storeName = v7Url.getStoreName();

    if (storeId != null) {
      return getStoreCredentials(storeId);
    } else if (storeName != null) {
      return getStoreCredentials(storeName);
    }
    return null;
  }

  /**
   * If you want to do event tracking (Analytics) use (v8engine)StoreUtilsProxy.subscribeStore
   * instead, else, use this
   */
  @Deprecated public static void subscribeStore(String storeName,
      @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener,
      @Nullable ErrorRequestListener errorRequestListener) {
    subscribeStore(GetStoreMetaRequest.of(getStoreCredentials(storeName),
        AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID()), successRequestListener,
        errorRequestListener);
  }

  public static boolean isSubscribedStore(String storeName) {
    StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
    return storeAccessor.get(storeName) != null;
  }

  public static String split(String repoUrl) {
    Logger.d("Aptoide-RepoUtils", "Splitting " + repoUrl);
    repoUrl = formatRepoUri(repoUrl);
    return repoUrl.split("http://")[1].split("\\.store")[0].split("\\.bazaarandroid.com")[0];
  }

  public static String formatRepoUri(String repoUri) {

    repoUri = repoUri.toLowerCase(Locale.ENGLISH);

    if (repoUri.contains("http//")) {
      repoUri = repoUri.replaceFirst("http//", "http://");
    }

    if (repoUri.length() != 0 && repoUri.charAt(repoUri.length() - 1) != '/') {
      repoUri = repoUri + '/';
      Logger.d("Aptoide-ManageRepo", "repo uri: " + repoUri);
    }
    if (!repoUri.startsWith("http://")) {
      repoUri = "http://" + repoUri;
      Logger.d("Aptoide-ManageRepo", "repo uri: " + repoUri);
    }

    return repoUri;
  }

  /**
   * If you want to do event tracking (Analytics) use (v8engine)StoreUtilsProxy.subscribeStore
   * instead, else, use this.
   */
  @Deprecated public static void subscribeStore(GetStoreMetaRequest getStoreMetaRequest,
      @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener,
      @Nullable ErrorRequestListener errorRequestListener) {
    getStoreMetaRequest.execute(getStoreMeta -> {

      if (BaseV7Response.Info.Status.OK.equals(getStoreMeta.getInfo().getStatus())) {

        StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);

        Store store = new Store();

        cm.aptoide.pt.model.v7.store.Store storeData = getStoreMeta.getData();
        store.setStoreId(storeData.getId());
        store.setStoreName(storeData.getName());
        store.setDownloads(storeData.getStats().getDownloads());

        store.setIconPath(storeData.getAvatar());
        store.setTheme(storeData.getAppearance().getTheme());

        if (isPrivateCredentialsSet(getStoreMetaRequest)) {
          store.setUsername(getStoreMetaRequest.getBody().getStoreUser());
          store.setPasswordSha1(getStoreMetaRequest.getBody().getStorePassSha1());
        }

        // TODO: 18-05-2016 neuro private ainda na ta
        if (AptoideAccountManager.isLoggedIn()) {
          AptoideAccountManager.subscribeStore(storeData.getName());
        }

        storeAccessor.save(store);

        if (successRequestListener != null) {
          successRequestListener.call(getStoreMeta);
        }
      }
    }, (e) -> {
      if (errorRequestListener != null) {
        errorRequestListener.onError(e);
      }
      CrashReports.logException(e);
    });
  }

  private static boolean isPrivateCredentialsSet(GetStoreMetaRequest getStoreMetaRequest) {
    return getStoreMetaRequest.getBody().getStoreUser() != null
        && getStoreMetaRequest.getBody().getStorePassSha1() != null;
  }

  public static List<Long> getSubscribedStoresIds() {

    List<Long> storesNames = new LinkedList<>();
    StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
    List<Store> stores = storeAccessor.getAll().toBlocking().first();
    for (Store store : stores) {
      storesNames.add(store.getStoreId());
    }

    return storesNames;
  }

  public static HashMapNotNull<String, List<String>> getSubscribedStoresAuthMap() {
    StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
    HashMapNotNull<String, List<String>> storesAuthMap = new HashMapNotNull<>();
    List<Store> stores = storeAccessor.getAll().toBlocking().first();
    for (Store store : stores) {
      if (store.getPasswordSha1() != null) {
        storesAuthMap.put(store.getStoreName(),
            new LinkedList<>(Arrays.asList(store.getUsername(), store.getPasswordSha1())));
      }
    }
    return storesAuthMap.size() > 0 ? storesAuthMap : null;
  }

  public static void unsubscribeStore(String name) {
    if (AptoideAccountManager.isLoggedIn()) {
      AptoideAccountManager.unsubscribeStore(name);
    }
    StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
    storeAccessor.remove(name);
  }
}

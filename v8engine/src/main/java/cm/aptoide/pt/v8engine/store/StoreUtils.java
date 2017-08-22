package cm.aptoide.pt.v8engine.store;

import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.interfaces.ErrorRequestListener;
import cm.aptoide.pt.dataprovider.interfaces.SuccessRequestListener;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import rx.Observable;

/**
 * Created by neuro on 14-10-2016.
 */

public class StoreUtils {
  private static final String PRIVATE_STORE_ERROR_CODE = "STORE-3";
  private static final String PRIVATE_STORE_WRONG_CREDENTIALS_ERROR_CODE = "STORE-4";
  private static final String STORE_SUSPENDED_ERROR_CODE = "STORE-7";

  @Deprecated public static BaseRequestWithStore.StoreCredentials getStoreCredentials(long storeId,
      StoreCredentialsProvider storeCredentialsProvider) {
    return storeCredentialsProvider.get(storeId);
  }

  @Partners @Deprecated public static @Nullable
  BaseRequestWithStore.StoreCredentials getStoreCredentialsFromUrl(String url,
      StoreCredentialsProvider storeCredentialsProvider) {
    return storeCredentialsProvider.fromUrl(url);
  }

  /**
   * If you want to do event tracking (Analytics) use (v8engine)StoreUtilsProxy.subscribeStore
   * instead, else, use this.
   */
  @Deprecated public static Observable<GetStoreMeta> subscribeStore(
      GetStoreMetaRequest getStoreMetaRequest, AptoideAccountManager accountManager,
      String storeUserName, String storePassword, StoreAccessor storeAccessor) {

    return getStoreMetaRequest.observe()
        .flatMap(getStoreMeta -> accountManager.accountStatus()
            .first()
            .toSingle()
            .flatMapObservable(account -> {
              if (BaseV7Response.Info.Status.OK.equals(getStoreMeta.getInfo()
                  .getStatus())) {
                // TODO: 18-05-2016 neuro private ainda na ta
                if (account.isLoggedIn()) {
                  return accountManager.subscribeStore(getStoreMeta.getData()
                      .getName(), storeUserName, storePassword)
                      .andThen(Observable.just(getStoreMeta));
                } else {
                  return Observable.just(getStoreMeta);
                }
              } else {
                return Observable.error(
                    new Exception("Something went wrong while getting store meta"));
              }
            }))
        .doOnNext(
            getStoreMeta -> saveStore(getStoreMeta.getData(), getStoreMetaRequest, storeAccessor));
  }

  private static void saveStore(cm.aptoide.pt.dataprovider.model.v7.store.Store storeData,
      GetStoreMetaRequest getStoreMetaRequest, StoreAccessor storeAccessor) {
    Store store = new Store();

    store.setStoreId(storeData.getId());
    store.setStoreName(storeData.getName());
    store.setDownloads(storeData.getStats()
        .getDownloads());

    store.setIconPath(storeData.getAvatar());
    store.setTheme(storeData.getAppearance()
        .getTheme());

    if (isPrivateCredentialsSet(getStoreMetaRequest)) {
      store.setUsername(getStoreMetaRequest.getBody()
          .getStoreUser());
      store.setPasswordSha1(getStoreMetaRequest.getBody()
          .getStorePassSha1());
    }
    storeAccessor.save(store);
  }

  /**
   * If you want to do event tracking (Analytics) use (v8engine)StoreUtilsProxy.subscribeStore
   * instead, else, use this.
   */
  @Deprecated public static void subscribeStore(GetStoreMetaRequest getStoreMetaRequest,
      @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener,
      @Nullable ErrorRequestListener errorRequestListener, AptoideAccountManager accountManager,
      String storeUserName, String storePassword, StoreAccessor storeAccessor) {

    subscribeStore(getStoreMetaRequest, accountManager, storeUserName, storePassword,
        storeAccessor).subscribe(getStoreMeta -> {
      if (successRequestListener != null) {
        successRequestListener.call(getStoreMeta);
      }
    }, (e) -> {
      if (errorRequestListener != null) {
        errorRequestListener.onError(e);
      }
      CrashReport.getInstance()
          .log(e);
    });
  }

  /**
   * @see StoreCredentialsProvider
   */
  @Deprecated public static BaseRequestWithStore.StoreCredentials getStoreCredentials(
      String storeName, StoreCredentialsProvider storeCredentialsProvider) {
    return storeCredentialsProvider.get(storeName);
  }

  private static boolean isPrivateCredentialsSet(GetStoreMetaRequest getStoreMetaRequest) {
    return getStoreMetaRequest.getBody()
        .getStoreUser() != null
        && getStoreMetaRequest.getBody()
        .getStorePassSha1() != null;
  }

  public static Observable<Boolean> isSubscribedStore(String storeName,
      StoreAccessor storeAccessor) {
    return storeAccessor.get(storeName)
        .map(store -> store != null);
  }

  public static ArrayList<String> split(List<String> repoUrl) {

    ArrayList<String> res = new ArrayList<String>();

    if (repoUrl != null) {
      for (String s : repoUrl) {
        res.add(split(s));
      }
    }
    return res;
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

  public static List<Long> getSubscribedStoresIds(StoreAccessor storeAccessor) {

    List<Long> storesNames = new LinkedList<>();
    List<Store> stores = storeAccessor.getAll()
        .toBlocking()
        .first();
    for (Store store : stores) {
      storesNames.add(store.getStoreId());
    }

    return storesNames;
  }

  public static HashMapNotNull<String, List<String>> getSubscribedStoresAuthMap(
      StoreAccessor storeAccessor) {
    HashMapNotNull<String, List<String>> storesAuthMap = new HashMapNotNull<>();
    List<Store> stores = storeAccessor.getAll()
        .toBlocking()
        .first();
    for (Store store : stores) {
      if (store.getPasswordSha1() != null) {
        storesAuthMap.put(store.getStoreName(),
            new LinkedList<>(Arrays.asList(store.getUsername(), store.getPasswordSha1())));
      }
    }
    return storesAuthMap.size() > 0 ? storesAuthMap : null;
  }

  public static void unSubscribeStore(String name, AptoideAccountManager accountManager,
      StoreCredentialsProvider storeCredentialsProvider, StoreAccessor storeAccessor) {
    accountManager.accountStatus()
        .map(account -> account.isLoggedIn())
        .first()
        .subscribe(isLoggedIn -> {
          if (isLoggedIn) {
            accountManager.unsubscribeStore(name, storeCredentialsProvider.get(name)
                .getName(), storeCredentialsProvider.get(name)
                .getPasswordSha1());
          }
          storeAccessor.remove(name);
        });
  }

  public static StoreError getErrorType(String code) {
    StoreError error;
    switch (code) {
      case PRIVATE_STORE_WRONG_CREDENTIALS_ERROR_CODE:
        error = StoreError.PRIVATE_STORE_WRONG_CREDENTIALS;
        break;
      case PRIVATE_STORE_ERROR_CODE:
        error = StoreError.PRIVATE_STORE_ERROR;
        break;
      case STORE_SUSPENDED_ERROR_CODE:
        error = StoreError.STORE_SUSPENDED;
        break;
      default:
        error = StoreError.GENERIC_ERROR;
    }
    return error;
  }

  public enum StoreError {
    PRIVATE_STORE_ERROR, PRIVATE_STORE_WRONG_CREDENTIALS, GENERIC_ERROR, STORE_SUSPENDED
  }
}

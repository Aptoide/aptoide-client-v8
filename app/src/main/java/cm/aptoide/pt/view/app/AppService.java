package cm.aptoide.pt.view.app;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetRecommendedRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreUtils;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

/**
 * Created by trinkes on 18/10/2017.
 */

public class AppService {
  private final StoreCredentialsProvider storeCredentialsProvider;
  private final BodyInterceptor<BaseBody> bodyInterceptorV7;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private boolean loading;

  public AppService(StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor<BaseBody> bodyInterceptorV7, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  private Single<AppsList> loadApps(long storeId, boolean bypassCache, int offset, int limit) {
    if (loading) {
      return Single.just(new AppsList(true));
    }
    ListAppsRequest.Body body =
        new ListAppsRequest.Body(storeCredentialsProvider.get(storeId), limit, sharedPreferences);
    body.setOffset(offset);
    body.setStoreId(storeId);
    return new ListAppsRequest(body, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences).observe(bypassCache, false)
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(appsList -> mapListApps(appsList))
        .toSingle()
        .onErrorReturn(throwable -> createErrorAppsList(throwable));
  }

  private Observable<AppsList> mapListApps(ListApps listApps) {
    if (listApps.isOk()) {
      List<Application> list = new ArrayList<>();
      for (App app : listApps.getDataList()
          .getList()) {
        list.add(new Application(app.getName(), app.getIcon(), app.getStats()
            .getRating()
            .getAvg(), app.getStats()
            .getDownloads(), app.getPackageName(), app.getId(), ""));
      }
      return Observable.just(new AppsList(list, false, listApps.getDataList()
          .getNext()));
    } else {
      return Observable.error(new IllegalStateException("Could not obtain request from server."));
    }
  }

  public Single<DetailedAppRequestResult> loadDetailedApp(long appId, String packageName) {
    if (loading) {
      return Single.just(new DetailedAppRequestResult(true));
    }
    return GetAppRequest.of(packageName, bodyInterceptorV7, appId, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(false, false)
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(getApp -> mapAppToDetailedAppRequestResult(getApp, ""))
        .toSingle()
        .onErrorReturn(throwable -> createDetailedAppRequestResultError(throwable));
  }

  //Might need PaidApp logic
  public Single<DetailedAppRequestResult> loadDetailedApp(long appId, String storeName,
      String packageName) {
    if (loading) {
      return Single.just(new DetailedAppRequestResult(true));
    }
    return GetAppRequest.of(appId, null,
        StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider), packageName,
        bodyInterceptorV7, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe(false, false)
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(getApp -> mapAppToDetailedAppRequestResult(getApp, ""))
        .toSingle()
        .onErrorReturn(throwable -> createDetailedAppRequestResultError(throwable));
  }

  //Might need PaidApp logic
  public Single<DetailedAppRequestResult> loadDetailedApp(String packageName, String storeName) {
    if (loading) {
      return Single.just(new DetailedAppRequestResult(true));
    }
    return GetAppRequest.of(packageName, storeName, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(false, false)
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(getApp -> mapAppToDetailedAppRequestResult(getApp, ""))
        .toSingle()
        .onErrorReturn(throwable -> createDetailedAppRequestResultError(throwable));
  }

  //Might need PaidApp logic
  public Single<DetailedAppRequestResult> loadDetailedAppFromMd5(String md5) {
    if (loading) {
      return Single.just(new DetailedAppRequestResult(true));
    }
    return GetAppRequest.ofMd5(md5, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(false, ManagerPreferences.getAndResetForceServerRefresh(sharedPreferences))
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(getApp -> mapAppToDetailedAppRequestResult(getApp, ""))
        .toSingle()
        .onErrorReturn(throwable -> createDetailedAppRequestResultError(throwable));
  }

  public Single<DetailedAppRequestResult> loadDetailedAppFromUname(String uName) {
    if (loading) {
      return Single.just(new DetailedAppRequestResult(true));
    }
    return GetAppRequest.ofUname(uName, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(false, false)
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(getApp -> mapAppToDetailedAppRequestResult(getApp, uName))
        .toSingle()
        .onErrorReturn(throwable -> createDetailedAppRequestResultError(throwable));
  }

  private Observable<DetailedAppRequestResult> mapAppToDetailedAppRequestResult(GetApp getApp,
      String uName) {
    if (getApp.isOk()) {
      GetAppMeta.App app = getApp.getNodes()
          .getMeta()
          .getData();
      GetAppMeta.GetAppMetaFile file = app.getFile();
      GetAppMeta.GetAppMetaFile.Flags flags = app.getFile()
          .getFlags();
      AppFlags appFlags = new AppFlags(flags.getReview(), mapToFlagsVote(flags.getVotes()));
      DetailedApp detailedApp =
          new DetailedApp(app.getId(), app.getName(), app.getPackageName(), app.getSize(),
              app.getIcon(), app.getGraphic(), app.getAdded(), app.getModified(), file.isGoodApp(),
              file.getMalware(), appFlags, file.getTags(), file.getUsedFeatures(),
              file.getUsedPermissions(), file.getFilesize(), app.getMd5(), file.getMd5sum(),
              file.getPath(), file.getPathAlt(), file.getVercode(), file.getVername(),
              app.getDeveloper(), app.getStore(), app.getMedia(), app.getStats(), app.getObb(),
              app.getPay(), app.getUrls()
              .getW(), app.isPaid(), uName);
      return Observable.just(new DetailedAppRequestResult(detailedApp));
    } else {
      return Observable.error(new IllegalStateException("Could not obtain request from server."));
    }
  }

  public Single<AppsList> loadRecommendedApps(int limit, String packageName) {
    if (loading) {
      return Single.just(new AppsList(true));
    }
    return new GetRecommendedRequest(new GetRecommendedRequest.Body(limit, packageName),
        bodyInterceptorV7, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences).observe(true, false)
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(appsList -> mapListApps(appsList))
        .toSingle()
        .onErrorReturn(throwable -> createErrorAppsList(throwable));
  }

  public Single<AppsList> loadFreshApps(long storeId, int limit) {
    return loadApps(storeId, true, 0, limit);
  }

  public Single<AppsList> loadApps(long storeId, int offset, int limit) {
    return loadApps(storeId, false, offset, limit);
  }

  @NonNull private AppsList createErrorAppsList(Throwable throwable) {
    if (throwable instanceof NoNetworkConnectionException) {
      return new AppsList(AppsList.Error.NETWORK);
    } else {
      return new AppsList(AppsList.Error.GENERIC);
    }
  }

  @NonNull
  private DetailedAppRequestResult createDetailedAppRequestResultError(Throwable throwable) {
    if (throwable instanceof NoNetworkConnectionException) {
      return new DetailedAppRequestResult(DetailedAppRequestResult.Error.NETWORK);
    } else {
      return new DetailedAppRequestResult(DetailedAppRequestResult.Error.GENERIC);
    }
  }

  private List<FlagsVote> mapToFlagsVote(List<GetAppMeta.GetAppMetaFile.Flags.Vote> votes) {
    List<FlagsVote> flagsVotes = new ArrayList<>();
    if (votes != null) {
      for (GetAppMeta.GetAppMetaFile.Flags.Vote vote : votes) {
        flagsVotes.add(new FlagsVote(vote.getCount(), mapToFlagsVoteType(vote.getType())));
      }
    }
    return flagsVotes;
  }

  private FlagsVote.VoteType mapToFlagsVoteType(GetAppMeta.GetAppMetaFile.Flags.Vote.Type type) {
    FlagsVote.VoteType flagsVoteVoteType = null;
    switch (type) {
      case FAKE:
        flagsVoteVoteType = FlagsVote.VoteType.FAKE;
        break;
      case GOOD:
        flagsVoteVoteType = FlagsVote.VoteType.GOOD;
        break;
      case VIRUS:
        flagsVoteVoteType = FlagsVote.VoteType.VIRUS;
        break;
      case FREEZE:
        flagsVoteVoteType = FlagsVote.VoteType.FREEZE;
        break;
      case LICENSE:
        flagsVoteVoteType = FlagsVote.VoteType.LICENSE;
        break;
      default:
        break;
    }
    return flagsVoteVoteType;
  }
}

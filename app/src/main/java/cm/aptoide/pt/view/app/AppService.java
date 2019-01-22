package cm.aptoide.pt.view.app;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.listapp.File;
import cm.aptoide.pt.dataprovider.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetRecommendedRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.repository.exception.RepositoryItemNotFoundException;
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
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;
  private boolean loadingApps;
  private boolean loadingSimilarApps;

  public AppService(StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor<BaseBody> bodyInterceptorV7,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, Resources resources) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
  }

  private Single<AppsList> loadApps(long storeId, boolean bypassCache, int offset, int limit) {
    if (loadingApps) {
      return Single.just(new AppsList(true));
    }
    ListAppsRequest.Body body =
        new ListAppsRequest.Body(storeCredentialsProvider.get(storeId), limit, sharedPreferences);
    body.setOffset(offset);
    body.setStoreId(storeId);
    return new ListAppsRequest(body, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences).observe(bypassCache, false)
        .doOnSubscribe(() -> loadingApps = true)
        .doOnUnsubscribe(() -> loadingApps = false)
        .doOnTerminate(() -> loadingApps = false)
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
            .getDownloads(), app.getPackageName(), app.getId(), "",
            app.getAppcoins() != null && app.getAppcoins()
                .hasBilling()));
      }
      return Observable.just(new AppsList(list, false, listApps.getDataList()
          .getNext()));
    } else {
      return Observable.error(new IllegalStateException("Could not obtain request from server."));
    }
  }

  public Single<DetailedAppRequestResult> loadDetailedApp(long appId, String storeName,
      String packageName) {
    if (loadingApps) {
      return Single.just(new DetailedAppRequestResult(true));
    }
    return GetAppRequest.of(appId, null,
        StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider), packageName,
        bodyInterceptorV7, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe(false, false)
        .doOnSubscribe(() -> loadingApps = true)
        .doOnUnsubscribe(() -> loadingApps = false)
        .doOnTerminate(() -> loadingApps = false)
        .flatMap(getApp -> mapApp(getApp, ""))
        .toSingle()
        .onErrorReturn(throwable -> createDetailedAppRequestResultError(throwable));
  }

  public Single<DetailedAppRequestResult> loadDetailedApp(String packageName, String storeName) {
    if (loadingApps) {
      return Single.just(new DetailedAppRequestResult(true));
    }
    return GetAppRequest.of(packageName, storeName, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(false, false)
        .doOnSubscribe(() -> loadingApps = true)
        .doOnUnsubscribe(() -> loadingApps = false)
        .doOnTerminate(() -> loadingApps = false)
        .flatMap(getApp -> mapApp(getApp, ""))
        .toSingle()
        .onErrorReturn(throwable -> createDetailedAppRequestResultError(throwable));
  }

  public Single<DetailedAppRequestResult> loadDetailedAppFromMd5(String md5) {
    if (loadingApps) {
      return Single.just(new DetailedAppRequestResult(true));
    }
    return GetAppRequest.ofMd5(md5, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(false, ManagerPreferences.getAndResetForceServerRefresh(sharedPreferences))
        .doOnSubscribe(() -> loadingApps = true)
        .doOnUnsubscribe(() -> loadingApps = false)
        .doOnTerminate(() -> loadingApps = false)
        .flatMap(getApp -> mapApp(getApp, ""))
        .toSingle()
        .onErrorReturn(throwable -> createDetailedAppRequestResultError(throwable));
  }

  public Single<DetailedAppRequestResult> loadDetailedAppFromUniqueName(String uniqueName) {
    if (loadingApps) {
      return Single.just(new DetailedAppRequestResult(true));
    }
    return GetAppRequest.ofUname(uniqueName, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(false, false)
        .doOnSubscribe(() -> loadingApps = true)
        .doOnUnsubscribe(() -> loadingApps = false)
        .doOnTerminate(() -> loadingApps = false)
        .flatMap(getApp -> mapApp(getApp, uniqueName))
        .toSingle()
        .onErrorReturn(throwable -> createDetailedAppRequestResultError(throwable));
  }

  public Single<AppsList> loadRecommendedApps(int limit, String packageName) {
    if (loadingSimilarApps) {
      return Single.just(new AppsList(true));
    }
    return new GetRecommendedRequest(new GetRecommendedRequest.Body(limit, packageName),
        bodyInterceptorV7, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences).observe(true, false)
        .doOnSubscribe(() -> loadingSimilarApps = true)
        .doOnUnsubscribe(() -> loadingSimilarApps = false)
        .doOnTerminate(() -> loadingSimilarApps = false)
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

  private Observable<DetailedAppRequestResult> mapApp(GetApp getApp, String uniqueName) {
    if (getApp.isOk()) {
      GetAppMeta.App app = getApp.getNodes()
          .getMeta()
          .getData();
      ListAppVersions listAppVersions = getApp.getNodes()
          .getVersions();
      GetAppMeta.GetAppMetaFile file = app.getFile();
      GetAppMeta.GetAppMetaFile.Flags flags = app.getFile()
          .getFlags();
      GetAppMeta.Developer developer = app.getDeveloper();
      GetAppMeta.Stats stats = app.getStats();
      GetAppMeta.Stats.Rating rating = stats.getRating();
      GetAppMeta.Stats.Rating globalRating = stats.getGlobalRating();
      GetAppMeta.Media media = app.getMedia();

      AppFlags appFlags = new AppFlags(flags.getReview(), mapToFlagsVote(flags.getVotes()));
      AppDeveloper appDeveloper =
          new AppDeveloper(developer.getName(), developer.getEmail(), developer.getPrivacy(),
              developer.getWebsite());
      AppRating appRating =
          new AppRating(rating.getAvg(), rating.getTotal(), mapToRatingsVote(rating.getVotes()));
      AppRating globalAppRating = new AppRating(globalRating.getAvg(), globalRating.getTotal(),
          mapToRatingsVote(globalRating.getVotes()));
      AppStats appStats =
          new AppStats(appRating, globalAppRating, stats.getDownloads(), stats.getPdownloads());
      AppMedia appMedia = new AppMedia(media.getDescription(), media.getKeywords(), media.getNews(),
          mapToScreenShots(media.getScreenshots()), mapToVideo(media.getVideos()));

      if (app.isPaid()) {
        return getPaidApp(app.getId()).map(paidApp -> {
          app.getPay()
              .setStatus(paidApp.getPayment()
                  .getStatus());
          return new DetailedAppRequestResult(
              new DetailedApp(app.getId(), app.getName(), app.getPackageName(), app.getSize(),
                  app.getIcon(), app.getGraphic(), app.getAdded(), app.getModified(),
                  file.isGoodApp(), file.getMalware(), appFlags, file.getTags(),
                  file.getUsedFeatures(), file.getUsedPermissions(), file.getFilesize(),
                  app.getMd5(), file.getPath(), file.getPathAlt(), file.getVercode(),
                  file.getVername(), appDeveloper, app.getStore(), appMedia, appStats, app.getObb(),
                  app.getPay(), app.getUrls()
                  .getW(), app.isPaid(), paidApp.getPayment()
                  .isPaid(), paidApp.getPath()
                  .getStringPath(), paidApp.getPayment()
                  .getStatus(), isLatestTrustedVersion(listAppVersions, file), uniqueName,
                  app.hasBilling(), app.hasAdvertising(), app.getBdsFlags()));
        });
      }

      DetailedApp detailedApp =
          new DetailedApp(app.getId(), app.getName(), app.getPackageName(), app.getSize(),
              app.getIcon(), app.getGraphic(), app.getAdded(), app.getModified(), file.isGoodApp(),
              file.getMalware(), appFlags, file.getTags(), file.getUsedFeatures(),
              file.getUsedPermissions(), file.getFilesize(), app.getMd5(), file.getPath(),
              file.getPathAlt(), file.getVercode(), file.getVername(), appDeveloper, app.getStore(),
              appMedia, appStats, app.getObb(), app.getPay(), app.getUrls()
              .getW(), app.isPaid(), isLatestTrustedVersion(listAppVersions, file), uniqueName,
              app.hasBilling(), app.hasAdvertising(), app.getBdsFlags());
      return Observable.just(new DetailedAppRequestResult(detailedApp));
    } else {
      return Observable.error(new IllegalStateException("Could not obtain request from server."));
    }
  }

  private Observable<PaidApp> getPaidApp(long appId) {
    return GetApkInfoRequest.of(appId, bodyInterceptorV3, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, resources)
        .observe(true)
        .flatMap(response -> {
          if (response != null && response.isOk() && response.isPaid()) {
            return Observable.just(response);
          } else {
            return Observable.error(
                new RepositoryItemNotFoundException("No paid app found for app id " + appId));
          }
        });
  }

  private boolean isLatestTrustedVersion(ListAppVersions listAppVersions, File file) {
    if (canCompare(listAppVersions)) {
      boolean isLatestVersion = file.getMd5sum()
          .equals(listAppVersions.getList()
              .get(0)
              .getFile()
              .getMd5sum());
      if (isLatestVersion) {
        return file.getMalware()
            .getRank() == Malware.Rank.TRUSTED;
      }
    }
    return false;
  }

  private boolean canCompare(ListAppVersions listAppVersions) {
    return (listAppVersions != null
        && listAppVersions.getList() != null
        && !listAppVersions.getList()
        .isEmpty());
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
        flagsVotes.add(new FlagsVote(vote.getCount(), map(vote.getType())));
      }
    }
    return flagsVotes;
  }

  private List<RatingVote> mapToRatingsVote(List<GetAppMeta.Stats.Rating.Vote> votes) {
    List<RatingVote> ratingVotes = new ArrayList<>();
    if (ratingVotes != null) {
      for (GetAppMeta.Stats.Rating.Vote vote : votes) {
        ratingVotes.add(new RatingVote(vote.getCount(), vote.getValue()));
      }
    }
    return ratingVotes;
  }

  private List<AppVideo> mapToVideo(List<GetAppMeta.Media.Video> videos) {
    List<AppVideo> appVideos = new ArrayList<>();
    if (videos != null) {
      for (GetAppMeta.Media.Video video : videos) {
        appVideos.add(new AppVideo(video.getThumbnail(), video.getType(), video.getUrl()));
      }
    }
    return appVideos;
  }

  private List<AppScreenshot> mapToScreenShots(List<GetAppMeta.Media.Screenshot> screenshots) {
    List<AppScreenshot> appScreenShots = new ArrayList<>();
    if (screenshots != null) {
      for (GetAppMeta.Media.Screenshot screenshot : screenshots) {
        appScreenShots.add(new AppScreenshot(screenshot.getHeight(), screenshot.getWidth(),
            screenshot.getOrientation(), screenshot.getUrl()));
      }
    }
    return appScreenShots;
  }

  private FlagsVote.VoteType map(GetAppMeta.GetAppMetaFile.Flags.Vote.Type type) {
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

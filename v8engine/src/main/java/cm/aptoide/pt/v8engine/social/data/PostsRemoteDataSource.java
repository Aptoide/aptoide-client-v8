package cm.aptoide.pt.v8engine.social.data;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetUserTimelineRequest;
import cm.aptoide.pt.v8engine.PackageRepository;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

/**
 * Created by jdandrade on 01/08/2017.
 */

public class PostsRemoteDataSource {
  private final String url;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient okhttp;
  private final Converter.Factory converterFactory;
  private final PackageRepository packageRepository;
  private final int latestPackagesCount;
  private final int randomPackagesCount;
  private final TimelineResponseCardMapper mapper;
  private final LinksHandlerFactory linksHandlerFactory;
  private final int limit;
  private final int initialOffset;
  private final TimelineCardFilter postFilter;
  private int currentOffset;
  private boolean loading;
  private int total;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;
  private String cardIdPriority;

  public PostsRemoteDataSource(String url, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient okhttp, Converter.Factory converterFactory, PackageRepository packageRepository,
      int latestPackagesCount, int randomPackagesCount, TimelineResponseCardMapper mapper,
      LinksHandlerFactory linksHandlerFactory, int limit, int initialOffset, int initialTotal,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      TimelineCardFilter postFilter) {
    this.url = url;
    this.bodyInterceptor = bodyInterceptor;
    this.okhttp = okhttp;
    this.converterFactory = converterFactory;
    this.packageRepository = packageRepository;
    this.latestPackagesCount = latestPackagesCount;
    this.randomPackagesCount = randomPackagesCount;
    this.mapper = mapper;
    this.linksHandlerFactory = linksHandlerFactory;
    this.limit = limit;
    this.initialOffset = initialOffset;
    this.total = initialTotal;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.postFilter = postFilter;
  }

  @NonNull private Single<List<Post>> getCards(int limit, int offset) {
    if (loading || (offset >= total)) {
      return Single.just(Collections.emptyList());
    }
    return getPackages().flatMap(packages -> Observable.fromCallable(() -> loading = true)
        .flatMapSingle(
            __ -> GetUserTimelineRequest.of(url, limit, offset, packages, bodyInterceptor, okhttp,
                converterFactory, cardIdPriority, tokenInvalidator, sharedPreferences)
                .observe(true)
                .toSingle())
        .flatMapSingle(timelineResponse -> {
          if (timelineResponse.isOk()) {
            this.currentOffset = timelineResponse.getNextSize();
            this.total = timelineResponse.getTotal();
            return Single.just(timelineResponse);
          }
          return Single.error(new IllegalStateException("Could not obtain timeline from server."));
        })
        .toSingle())
        .doOnError(__ -> loading = false)
        .doOnSuccess(__ -> {
          loading = false;
          cardIdPriority = null;
        })
        .map(timelineResponse -> timelineResponse.getDataList()
            .getList())
        .toObservable()
        .flatMapIterable(list -> list)
        .flatMap(element -> postFilter.filter(element))
        .toList()
        .toSingle()
        .map(timelineResponse -> mapper.map(timelineResponse, linksHandlerFactory));
  }

  private Single<List<String>> getPackages() {
    return Observable.concat(packageRepository.getLatestInstalledPackages(latestPackagesCount),
        packageRepository.getRandomInstalledPackages(randomPackagesCount))
        .toList()
        .toSingle();
  }

  public Single<List<Post>> getNextCards() {
    return getCards(limit, currentOffset);
  }

  public Single<List<Post>> getCards() {
    postFilter.clear();
    return getCards(limit, initialOffset);
  }

  public Single<List<Post>> getCards(String cardId) {
    postFilter.clear();
    cardIdPriority = cardId;
    return getCards(limit, initialOffset);
  }

  public void clearLoading() {
    loading = false;
  }
}

package cm.aptoide.pt.v8engine.social.data;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetTimelineStatsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.LikeCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.PostCommentForTimelineArticle;
import cm.aptoide.pt.dataprovider.ws.v7.ShareCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Single;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class TimelineService {
  private final Long userId;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient okhttp;
  private final Converter.Factory converterFactory;
  private final TimelineResponseCardMapper mapper;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;

  public TimelineService(Long userId, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient okhttp, Converter.Factory converterFactory, TimelineResponseCardMapper mapper,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    this.userId = userId;
    this.bodyInterceptor = bodyInterceptor;
    this.okhttp = okhttp;
    this.converterFactory = converterFactory;
    this.mapper = mapper;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Completable like(String postId) {
    return LikeCardRequest.of(postId, bodyInterceptor, okhttp, converterFactory, tokenInvalidator,
        sharedPreferences)
        .observe()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            return Completable.complete();
          } else {
            return Completable.error(new IllegalStateException(V7.getErrorMessage(response)));
          }
        })
        .toCompletable();
  }

  public Single<Post> getTimelineStats() {
    return GetTimelineStatsRequest.of(bodyInterceptor, userId, okhttp, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .flatMap(timelineResponse -> {
          if (timelineResponse.isOk()) {
            return Single.just(timelineResponse);
          }
          return Single.error(
              new IllegalStateException("Could not obtain timeline stats from server."));
        })
        .map(timelineStats -> mapper.map(timelineStats));
  }

  public Single<String> share(String cardId) {
    return ShareCardRequest.of(cardId, bodyInterceptor, okhttp, converterFactory, tokenInvalidator,
        sharedPreferences)
        .observe()
        .toSingle()
        .flatMap(response -> {
          if (response.isOk()) {
            return Single.just(response.getData()
                .getCardUid());
          }
          return Single.error(new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        });
  }

  public Single<String> shareApp(String cardId, Long storeId) {
    return ShareCardRequest.of(cardId, storeId, okhttp, converterFactory, bodyInterceptor,
        tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .flatMap(response -> {
          if (response.isOk()) {
            return Single.just(response.getData()
                .getCardUid());
          }
          return Single.error(new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        });
  }

  public Completable postComment(String cardId, String commentText) {
    return PostCommentForTimelineArticle.of(cardId, commentText, bodyInterceptor, okhttp,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            return Completable.complete();
          } else {
            return Completable.error(new IllegalStateException(V7.getErrorMessage(response)));
          }
        })
        .toCompletable();
  }
}

package cm.aptoide.pt.social;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.UserFollowingRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;

/**
 * Created by franciscocalado on 11/30/17.
 */

public class SocialInteractionManager {

  private final OkHttpClient okhttp;
  private final Converter.Factory converterFactory;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreference;

  public SocialInteractionManager(OkHttpClient okhttp, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreference) {
    this.okhttp = okhttp;
    this.converterFactory = converterFactory;
    this.bodyInterceptor = bodyInterceptor;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreference = sharedPreference;
  }

  public Completable followUser(Long userId) {
    return UserFollowingRequest.getFollowRequest(userId, bodyInterceptor, okhttp, converterFactory,
        tokenInvalidator, sharedPreference)
        .observe()
        .toCompletable();
  }

  public Completable unfollowUser(Long userId) {
    return UserFollowingRequest.getUnfollowRequest(userId, bodyInterceptor, okhttp,
        converterFactory, tokenInvalidator, sharedPreference)
        .observe()
        .toCompletable();
  }
}

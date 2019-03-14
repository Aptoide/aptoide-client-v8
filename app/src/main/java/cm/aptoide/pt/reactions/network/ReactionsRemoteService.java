package cm.aptoide.pt.reactions.network;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.reactions.SetReactionRequest;
import cm.aptoide.pt.reactions.data.ReactionType;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;

public class ReactionsRemoteService implements ReactionsService {

  private final OkHttpClient okHttpClient;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public ReactionsRemoteService(OkHttpClient okHttpClient,
      BodyInterceptor<BaseBody> bodyInterceptor, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    this.okHttpClient = okHttpClient;
    this.bodyInterceptor = bodyInterceptor;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public Completable setReaction(String id, ReactionType like) {
    return new SetReactionRequest(new SetReactionRequest.Body(id), okHttpClient, converterFactory,
        bodyInterceptor, tokenInvalidator, sharedPreferences).observe()
        .toCompletable();
  }
}
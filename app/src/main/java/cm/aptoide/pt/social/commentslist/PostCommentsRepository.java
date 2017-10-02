package cm.aptoide.pt.social.commentslist;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

/**
 * Created by jdandrade on 28/09/2017.
 */

class PostCommentsRepository {

  private BodyInterceptor<BaseBody> bodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;

  PostCommentsRepository(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Single<List<Comment>> getComments(String postId) {
    return ListCommentsRequest.ofTimeline("", true, postId, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMap(listCommentsResponse -> {
          if (listCommentsResponse.isOk()) {
            return Single.just(listCommentsResponse.getDataList()
                .getList());
          } else {
            return Single.just(null);
          }
        });
  }
}

package cm.aptoide.pt.social.commentslist;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

/**
 * Created by jdandrade on 28/09/2017.
 */

class PostCommentsRepository {

  private final int limit;
  private final int initialOffset;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private int currentOffset;
  private int total;

  PostCommentsRepository(int limit, int initialOffset, int initialTotal,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.limit = limit;
    this.initialOffset = initialOffset;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.total = initialTotal;
  }

  public Single<List<Comment>> getComments(String postId, int offset) {
    if (offset >= total) {
      return Single.just(Collections.emptyList());
    }
    return ListCommentsRequest.ofTimeline("", offset, limit, true, postId, bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMap(listCommentsResponse -> {
          if (listCommentsResponse.isOk()) {
            this.currentOffset = listCommentsResponse.getNextSize();
            this.total = listCommentsResponse.getTotal();
            return Single.just(listCommentsResponse.getDataList()
                .getList());
          } else {
            return Single.error(
                new IllegalStateException("Could not obtain Comments list from server"));
          }
        });
  }

  public Single<List<Comment>> getNextComments(String postId) {
    return getComments(postId, currentOffset);
  }

  public Single<List<Comment>> getComments(String postId) {
    return getComments(postId, initialOffset);
  }

  public Boolean hasMoreComments() {
    return currentOffset < total;
  }
}

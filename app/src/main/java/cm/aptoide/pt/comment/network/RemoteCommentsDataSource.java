package cm.aptoide.pt.comment.network;

import android.content.SharedPreferences;
import cm.aptoide.pt.comment.CommentsDataSource;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.User;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.Order;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

public class RemoteCommentsDataSource implements CommentsDataSource {

  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient okHttpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public RemoteCommentsDataSource(BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient okHttpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    this.bodyInterceptor = bodyInterceptor;
    this.okHttpClient = okHttpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Single<List<Comment>> loadComments(long storeId, boolean invalidateHttpCache) {
    return new ListCommentsRequest(new ListCommentsRequest.Body(invalidateHttpCache, Order.desc, 0),
        bodyInterceptor, okHttpClient, converterFactory, tokenInvalidator, sharedPreferences).
        observe()
        .flatMap(response -> {
          if (response.isOk()) {
            return Observable.just(map(response.getDataList()
                .getList()));
          } else {
            return Observable.error(new IllegalArgumentException(response.getError()
                .getDescription()));
          }
        })
        .toSingle();
  }

  @Override public Single<List<Comment>> loadComments(long storeId) {
    return loadComments(storeId, false);
  }

  @Override public Single<List<Comment>> loadFreshComments(long storeId) {
    return loadComments(storeId, true);
  }

  private List<Comment> map(List<cm.aptoide.pt.dataprovider.model.v7.Comment> networkComments) {
    List<Comment> comments = new ArrayList<>();

    for (cm.aptoide.pt.dataprovider.model.v7.Comment networkComment : networkComments) {
      comments.add(new Comment(networkComment.getId(), networkComment.getBody(), new User(
          networkComment.getUser()
              .getId(), networkComment.getUser()
          .getAvatar(), networkComment.getUser()
          .getName()), networkComment.getStats()
          .getComments(), networkComment.getAdded()));
    }

    return comments;
  }
}

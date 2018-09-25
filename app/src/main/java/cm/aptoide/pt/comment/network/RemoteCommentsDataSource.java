package cm.aptoide.pt.comment.network;

import android.content.SharedPreferences;
import cm.aptoide.pt.comment.CommentsDataSource;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.CommentsResponseModel;
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

  private Single<CommentsResponseModel> loadComments(long storeId, boolean invalidateHttpCache,
      int offset) {
    return new ListCommentsRequest(new ListCommentsRequest.Body(storeId, Order.desc, 0, offset),
        bodyInterceptor, okHttpClient, converterFactory, tokenInvalidator, sharedPreferences).
        observe(invalidateHttpCache)
        .flatMap(response -> {
          if (response.isOk()) {
            return Observable.just(new CommentsResponseModel(map(response.getDataList()
                .getList()), response.getDataList()
                .getNext()));
          } else {
            return Observable.error(new IllegalArgumentException(response.getError()
                .getDescription()));
          }
        })
        .toSingle();
  }

  @Override public Single<CommentsResponseModel> loadComments(long storeId) {
    return loadComments(storeId, false, 0);
  }

  @Override public Single<CommentsResponseModel> loadFreshComments(long storeId) {
    return loadComments(storeId, true, 0);
  }

  @Override public Single<CommentsResponseModel> loadNextComments(long storeId, int offset) {
    return loadComments(storeId, false, offset);
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

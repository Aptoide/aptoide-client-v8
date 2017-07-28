package cm.aptoide.pt.v8engine.timeline.post;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.dataprovider.util.ProcessingException;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.post.CardPreviewRequest;
import cm.aptoide.pt.dataprovider.ws.v7.post.CardPreviewResponse;
import cm.aptoide.pt.dataprovider.ws.v7.post.PostRequest;
import cm.aptoide.pt.dataprovider.ws.v7.post.RelatedAppRequest;
import cm.aptoide.pt.dataprovider.ws.v7.post.RelatedAppResponse;
import cm.aptoide.pt.v8engine.timeline.response.StillProcessingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class PostRemoteAccessor implements PostAccessor {

  private SharedPreferences preferences;
  private BodyInterceptor bodyInterceptor;
  private OkHttpClient client;
  private Converter.Factory converter;
  private TokenInvalidator tokenInvalidator;

  public PostRemoteAccessor(SharedPreferences preferences, BodyInterceptor bodyInterceptor,
      OkHttpClient client, Converter.Factory converter, TokenInvalidator tokenInvalidator) {
    this.preferences = preferences;
    this.bodyInterceptor = bodyInterceptor;
    this.client = client;
    this.converter = converter;
    this.tokenInvalidator = tokenInvalidator;
  }

  /**
   * @return Card inserted in the timeline. Possible types of cards: SOCIAL_APP, SOCIAL_ARTICLE,
   * SOCIAL_VIDEO
   */
  @Override public Completable postOnTimeline(String url, String content, String packageName) {
    return PostRequest.of(url, content, packageName, preferences, bodyInterceptor, client,
        converter, tokenInvalidator)
        .observe()
        .toCompletable();
  }

  /**
   * This service may need a long pooling technique since it can return an error while
   * the web service is processing the request.
   *
   * @return a {@link DataList} of {@link RelatedApp}s found in the url
   * or {@link StillProcessingException} if the system is still processing
   * the request.
   */
  @Override public Single<List<RelatedApp>> getRelatedApps(String url) {
    return handleProcessing(
        RelatedAppRequest.of(url, preferences, client, converter, bodyInterceptor, tokenInvalidator)
            .observe()).map(response -> {
      if (response.getDataList()
          .getCount() <= 0) {
        return Collections.<RelatedApp>emptyList();
      }
      List<RelatedAppResponse.RelatedApp> remoteList = response.getDataList()
          .getList();

      List<RelatedApp> localList = new ArrayList<>(remoteList.size());
      localList.add(convertToLocalRelatedApp(remoteList.get(0), true));
      for (int i = 1; i < remoteList.size(); i++) {
        localList.add(convertToLocalRelatedApp(remoteList.get(i), false));
      }
      return localList;
    })
        .first()
        .toSingle();
  }

  @Override public Single<PostView.PostPreview> getCardPreview(String url) {
    return handleProcessing(
        CardPreviewRequest.of(url, preferences, client, converter, bodyInterceptor,
            tokenInvalidator)
            .observe()).toSingle()
        .map(response -> convertToLocalCardPreview(url, response));
  }

  private <T extends BaseV7Response> Observable<T> handleProcessing(
      Observable<T> requestObservable) {
    return requestObservable.flatMap(relatedAppResponse -> {
      if (relatedAppResponse.getInfo()
          .getStatus()
          .equals(BaseV7Response.Info.Status.Processing)) {
        return Observable.error(new ProcessingException());
      } else {
        return Observable.just(relatedAppResponse);
      }
    })
        .retryWhen(observable -> observable.flatMap(throwable -> {
          if (throwable instanceof ProcessingException) {
            return Observable.timer(1, TimeUnit.SECONDS);
          } else {
            return Observable.<Long>error(throwable);
          }
        }));
  }

  private RelatedApp convertToLocalRelatedApp(RelatedAppResponse.RelatedApp remoteRelatedApp,
      boolean isSelected) {
    return new RelatedApp(remoteRelatedApp.getIcon(), remoteRelatedApp.getName(),
        PostManager.Origin.Remote, isSelected, remoteRelatedApp.getPackageName());
  }

  private PostView.PostPreview convertToLocalCardPreview(String url, CardPreviewResponse response) {
    return new PostView.PostPreview(response.getData()
        .getData()
        .getThumbnail(), response.getData()
        .getData()
        .getTitle(), url);
  }

  static class RelatedApp {
    private final String image;
    private final String name;
    private final PostManager.Origin origin;
    private boolean selected;
    private String packageName;

    RelatedApp(String image, String name, PostManager.Origin origin, boolean selected,
        String packageName) {
      this.image = image;
      this.name = name;
      this.origin = origin;
      this.selected = selected;
      this.packageName = packageName;
    }

    @Override public int hashCode() {
      int result = image.hashCode();
      result = 31 * result + name.hashCode();
      result = 31 * result + origin.hashCode();
      result = 31 * result + (selected ? 1 : 0);
      result = 31 * result + packageName.hashCode();
      return result;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      RelatedApp that = (RelatedApp) o;

      if (selected != that.selected) return false;
      if (!image.equals(that.image)) return false;
      if (!name.equals(that.name)) return false;
      if (origin != that.origin) return false;
      return packageName.equals(that.packageName);
    }

    @Override public String toString() {
      return "{name='" + name + '\'' + '}';
    }

    public String getImage() {
      return image;
    }

    public String getName() {
      return name;
    }

    public PostManager.Origin getOrigin() {
      return origin;
    }

    public boolean isSelected() {
      return selected;
    }

    public void setSelected(boolean selected) {
      this.selected = selected;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }
  }
}

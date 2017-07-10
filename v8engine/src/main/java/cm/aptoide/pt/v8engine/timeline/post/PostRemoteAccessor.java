package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.v8engine.timeline.response.CardPreview;
import cm.aptoide.pt.v8engine.timeline.response.Response;
import cm.aptoide.pt.v8engine.timeline.response.StillProcessingException;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class PostRemoteAccessor implements PostAccessor {

  private static final String TAG = PostRemoteAccessor.class.getSimpleName();
  private final PostWebService postWebService;
  private final PostRequestBuilder requestFactory;

  public PostRemoteAccessor(PostWebService postWebService, PostRequestBuilder requestFactory) {
    this.postWebService = postWebService;
    this.requestFactory = requestFactory;
  }

  /**
   * @return Card inserted in the timeline. Possible types of cards: SOCIAL_APP, SOCIAL_ARTICLE,
   * SOCIAL_VIDEO
   */
  @Override public Completable postOnTimeline(String url, String content, String packageName) {
    return requestFactory.getPostOnTimelineRequest(url, content, packageName)
        .flatMap(postRequest -> postWebService.postInTimeline(postRequest))
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
    return postWebService.getRelatedApps(requestFactory.getRelatedAppsRequest(url))
        .filter(response -> response.getDatalist()
            .getCount() > 0)
        .map(response -> {
          List<cm.aptoide.pt.v8engine.timeline.response.RelatedApp> remoteList =
              response.getDatalist()
                  .getList();

          List<RelatedApp> localList = new ArrayList<>(remoteList.size());
          localList.add(convertToLocalRelatedApp(remoteList.get(0), true));
          for (int i = 1; i < remoteList.size(); i++) {
            localList.add(convertToLocalRelatedApp(remoteList.get(i), false));
          }
          return localList;
        })
        .onErrorResumeNext(err -> {
          // TODO handle StillProcessingException
          return Observable.error(err);
        })
        .first()
        .toSingle();
  }

  @Override public Single<PostView.PostPreview> getCardPreview(String url) {
    return requestFactory.getCardPreviewRequest(url)
        .flatMap(cardPreviewRequest -> postWebService.getCardPreview(cardPreviewRequest))
        .map(response -> convertToLocalCardPreview(response));
  }

  private RelatedApp convertToLocalRelatedApp(
      cm.aptoide.pt.v8engine.timeline.response.RelatedApp remoteRelatedApp, boolean isSelected) {
    return new RelatedApp(remoteRelatedApp.getIcon(), remoteRelatedApp.getName(),
        PostManager.Origin.Remote, isSelected, remoteRelatedApp.getPackageName());
  }

  private PostView.PostPreview convertToLocalCardPreview(Response<CardPreview> response) {
    return new PostView.PostPreview(response.getData()
        .getData()
        .getThumbnail(), response.getData()
        .getData()
        .getTitle());
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

    @Override public String toString() {
      return "{name='" + name + '\'' + '}';
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }
  }
}

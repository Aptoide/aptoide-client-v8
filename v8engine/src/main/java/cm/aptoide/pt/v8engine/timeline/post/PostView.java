package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.v8engine.presenter.View;
import java.util.List;
import rx.Completable;
import rx.Observable;

interface PostView extends View {

  Observable<String> onInputTextChanged();

  Observable<String> shareButtonPressed();

  Observable<Void> cancelButtonPressed();

  Completable showSuccessMessage();

  void showCardPreview(PostPreview suggestion);

  void showCardPreviewLoading();

  void hideCardPreviewLoading();

  void showRelatedAppsLoading();

  void hideRelatedAppsLoading();

  void hideCardPreview();

  void showError(String error);

  void showInvalidTextError();

  void showInvalidPackageError();

  void addRelatedApps(List<PostRemoteAccessor.RelatedApp> relatedApps);

  PostRemoteAccessor.RelatedApp getCurrentSelected();

  void clearRemoteRelated();

  Observable<PostRemoteAccessor.RelatedApp> getClickedView();

  Completable setRelatedAppSelected(PostRemoteAccessor.RelatedApp app);

  void hideCardPreviewTitle();

  class PostPreview {
    private final String image;
    private final String title;

    PostPreview(String image, String title) {
      this.image = image;
      this.title = title;
    }

    public String getImage() {
      return image;
    }

    public String getTitle() {
      return title;
    }
  }
}

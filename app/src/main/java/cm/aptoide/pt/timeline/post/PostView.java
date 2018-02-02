package cm.aptoide.pt.timeline.post;

import cm.aptoide.pt.presenter.View;
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

  void showGenericError();

  void showInvalidTextError();

  void showInvalidPackageError();

  void addRelatedApps(List<PostRemoteAccessor.RelatedApp> relatedApps);

  PostRemoteAccessor.RelatedApp getCurrentSelected();

  void clearRemoteRelated();

  Observable<PostRemoteAccessor.RelatedApp> getClickedView();

  Completable setRelatedAppSelected(PostRemoteAccessor.RelatedApp app);

  void hideCardPreviewTitle();

  void exit();

  void showNoLoginError();

  Observable<Void> getLoginClick();

  void showAppNotFoundError();

  Observable<Void> getAppNotFoundErrorAction();

  void clearAllRelated();

  int getPreviewVisibility();

  void showInvalidUrlError();

  String getExternalUrlToShare();

  boolean isExternalOpen();
}

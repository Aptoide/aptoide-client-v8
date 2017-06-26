package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;

interface PostView extends View {

  Observable<String> onInputTextChanged();

  Observable<String> shareButtonPressed();

  Observable<Void> cancelButtonPressed();

  Completable showSuccessMessage();

  void showCardPreview(PostManager.PostPreview suggestion);

  void showCardPreviewLoading();

  void hideCardPreviewLoading();

  void showRelatedAppsLoading();

  void hideRelatedAppsLoading();

  void hideCardPreview();
}

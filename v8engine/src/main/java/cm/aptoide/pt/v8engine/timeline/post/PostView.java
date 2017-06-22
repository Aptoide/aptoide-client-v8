package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;

interface PostView extends View {

  Observable<String> onInputTextChanged();

  Observable<String> shareButtonPressed();

  Observable<Void> cancelButtonPressed();

  Completable close();

  Completable showSuccessMessage();

  Completable showCardPreview(PostManager.PostPreview suggestion);

  Completable showContainsUrlMessage();

  void showCardPreviewLoading();

  void hideCardPreviewLoading();

  void showRelatedAppsLoading();

  void hideRelatedAppsLoading();
}

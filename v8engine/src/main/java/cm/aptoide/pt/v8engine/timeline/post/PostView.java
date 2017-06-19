package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;

interface PostView extends View {

  Observable<String> onInputTextChanged();

  Observable<String> shareButtonPressed();

  Completable close();

  Completable hideLoading();

  Completable showSuccessMessage();

  Completable showLoading();

  Completable showSuggestion(String suggestion);
}

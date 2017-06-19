package cm.aptoide.pt.v8engine.timeline.createpost;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;

interface CreatePostView extends View {

  Observable<Void> shareButtonPressed();

  String getInputText();
}

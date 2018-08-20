package cm.aptoide.pt.home;

import android.view.View;
import rx.subjects.PublishSubject;

class AppCoinsKnowMoreViewHolder extends AppBundleViewHolder {
  private final PublishSubject<HomeEvent> uiEventsListener;

  public AppCoinsKnowMoreViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener) {
    super(view);
    this.uiEventsListener = uiEventsListener;
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {

  }
}

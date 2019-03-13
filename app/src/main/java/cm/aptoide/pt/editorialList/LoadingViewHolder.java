package cm.aptoide.pt.editorialList;

import android.view.View;
import cm.aptoide.pt.home.EditorialBundleViewHolder;
import cm.aptoide.pt.home.HomeEvent;
import rx.subjects.PublishSubject;

class LoadingViewHolder extends EditorialBundleViewHolder {
  public LoadingViewHolder(View inflate, PublishSubject<HomeEvent> uiEventsListener) {
    super(inflate, uiEventsListener);
  }
}

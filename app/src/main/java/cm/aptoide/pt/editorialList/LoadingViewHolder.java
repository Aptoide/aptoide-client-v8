package cm.aptoide.pt.editorialList;

import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.editorial.CaptionBackgroundPainter;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.home.bundles.editorial.EditorialBundleViewHolder;
import rx.subjects.PublishSubject;

class LoadingViewHolder extends EditorialBundleViewHolder {
  public LoadingViewHolder(View inflate, PublishSubject<HomeEvent> uiEventsListener,
      CaptionBackgroundPainter captionBackgroundPainter, ViewGroup parent) {
    super(inflate, uiEventsListener, captionBackgroundPainter, parent);
  }
}

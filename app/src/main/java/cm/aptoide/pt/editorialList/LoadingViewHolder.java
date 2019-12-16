package cm.aptoide.pt.editorialList;

import android.view.View;
import cm.aptoide.pt.editorial.CaptionBackgroundPainter;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.home.bundles.editorial.EditorialViewHolder;
import rx.subjects.PublishSubject;

class LoadingViewHolder extends EditorialViewHolder {
  public LoadingViewHolder(View inflate, PublishSubject<HomeEvent> uiEventsListener,
      CaptionBackgroundPainter captionBackgroundPainter) {
    super(inflate);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    //Do Nothing
  }
}

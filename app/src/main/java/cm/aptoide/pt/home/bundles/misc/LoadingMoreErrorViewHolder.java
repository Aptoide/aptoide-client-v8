package cm.aptoide.pt.home.bundles.misc;

import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.R;
import cm.aptoide.pt.home.bundles.base.AppBundleViewHolder;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import rx.subjects.PublishSubject;

public class LoadingMoreErrorViewHolder extends AppBundleViewHolder {

  private Button retry;
  private PublishSubject<HomeEvent> uiEventsListener;

  public LoadingMoreErrorViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener) {
    super(view);
    this.retry = view.findViewById(R.id.load_more_retry_button);
    this.uiEventsListener = uiEventsListener;
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    retry.setOnClickListener(__ -> uiEventsListener.onNext(
        new HomeEvent(homeBundle, position, HomeEvent.Type.LOAD_MORE_RETRY)));
  }
}

package cm.aptoide.pt.home;

import android.view.View;
import android.widget.FrameLayout;
import cm.aptoide.pt.R;
import rx.subjects.PublishSubject;

class AppCoinsKnowMoreViewHolder extends AppBundleViewHolder {
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final FrameLayout knowMoreButton;

  public AppCoinsKnowMoreViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener) {
    super(view);
    this.uiEventsListener = uiEventsListener;
    this.knowMoreButton = (FrameLayout) view.findViewById(R.id.know_more_button);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    knowMoreButton.setOnClickListener(view -> uiEventsListener.onNext(
        new HomeEvent(homeBundle, position, HomeEvent.Type.KNOW_MORE)));
  }
}

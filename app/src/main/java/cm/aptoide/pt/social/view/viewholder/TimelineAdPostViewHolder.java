package cm.aptoide.pt.social.view.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import cm.aptoide.pt.R;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.social.data.AdPost;
import cm.aptoide.pt.social.data.AdResponse;
import cm.aptoide.pt.social.data.CardTouchEvent;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 14/08/2017.
 */

public class TimelineAdPostViewHolder extends PostViewHolder<AdPost> {
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final LinearLayout cardLayout;
  private final ProgressBar adLoading;

  public TimelineAdPostViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    super(view, cardTouchEventPublishSubject);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.cardLayout = (LinearLayout) view.findViewById(R.id.card_layout);
    this.adLoading = (ProgressBar) view.findViewById(R.id.timeline_ad_loading);
  }

  @Override public void setPost(AdPost post, int position) {
    this.cardLayout.setVisibility(View.VISIBLE);
    this.cardLayout.removeAllViews();
    this.cardLayout.addView(adLoading);
    this.adLoading.setVisibility(View.VISIBLE);
    post.getAdView()
        .subscribe(adResponse -> {
          if (adResponse.getStatus()
              .equals(AdResponse.Status.ok)) {
            if (adResponse.getView()
                .getParent() == null) {
              cardLayout.addView(adResponse.getView());
              adLoading.setVisibility(View.GONE);
            } else {
              handleNativeAdError(post, position);
            }
          } else {
            handleNativeAdError(post, position);
          }
        }, throwable -> {
          handleNativeAdError(post, position);
          Logger.e(this, throwable);
        });
  }

  private void handleNativeAdError(AdPost post, int position) {
    NativeAdErrorEvent nativeAdErrorEvent =
        new NativeAdErrorEvent(post, CardTouchEvent.Type.ERROR, position);
    cardTouchEventPublishSubject.onNext(nativeAdErrorEvent);
  }
}

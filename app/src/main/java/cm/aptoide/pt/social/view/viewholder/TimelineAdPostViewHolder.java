package cm.aptoide.pt.social.view.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
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
  private NativeAdErrorEvent nativeAdErrorEvent;

  public TimelineAdPostViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    super(view);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.cardLayout = (LinearLayout) view.findViewById(R.id.card_layout);
    this.adLoading = (ProgressBar) view.findViewById(R.id.timeline_ad_loading);
  }

  @Override public void setPost(AdPost post, int position) {
    this.cardLayout.setVisibility(View.VISIBLE);
    this.adLoading.setVisibility(View.VISIBLE);
    this.nativeAdErrorEvent = new NativeAdErrorEvent(post, CardTouchEvent.Type.ERROR, position);
    post.getAdView()
        .subscribe(adResponse -> {
          Toast.makeText(itemView.getContext(), "Response code: " + adResponse.getStatus(),
              Toast.LENGTH_SHORT)
              .show();
          if (adResponse.getStatus()
              .equals(AdResponse.Status.ok)) {
            cardLayout.removeAllViews();
            cardLayout.addView(adResponse.getView());
            adLoading.setVisibility(View.GONE);
          } else {
            cardLayout.setVisibility(View.GONE);
            cardTouchEventPublishSubject.onNext(nativeAdErrorEvent);
          }
        }, throwable -> {
          cardLayout.setVisibility(View.GONE);
          cardTouchEventPublishSubject.onNext(nativeAdErrorEvent);
          Logger.e(this, throwable);
        });
  }
}

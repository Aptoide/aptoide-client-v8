package cm.aptoide.pt.social.view.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import cm.aptoide.pt.R;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.social.data.AdPost;
import cm.aptoide.pt.social.data.AdResponse;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.TimelineAdsRepository;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 14/08/2017.
 */

public class TimelineAdPostViewHolder extends PostViewHolder<AdPost> {
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final LinearLayout cardLayout;
  private final TimelineAdsRepository adsRepository;

  public TimelineAdPostViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject,
      TimelineAdsRepository adsRepository) {
    super(view, cardTouchEventPublishSubject);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.cardLayout = (LinearLayout) view.findViewById(R.id.card_layout);
    this.adsRepository = adsRepository;
  }

  @Override public void setPost(AdPost post, int position) {
    this.cardLayout.setVisibility(View.VISIBLE);
    this.cardLayout.removeAllViews();
    tryToShowAd(post, position);
  }

  private void tryToShowAd(AdPost post, int position) {
    adsRepository.fetchAd(itemView.getContext());
    adsRepository.getAd()
        .subscribe(adResponse -> {
          if (adResponse.getStatus()
              .equals(AdResponse.Status.ok)) {
            if (adResponse.getView()
                .getParent() == null) {
              cardLayout.addView(adResponse.getView());
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

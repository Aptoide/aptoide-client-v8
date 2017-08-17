package cm.aptoide.pt.social.view.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import cm.aptoide.pt.R;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.Post;
import cm.aptoide.pt.social.data.TimelineAdsRepository;
import cm.aptoide.pt.spotandshare.socket.Log;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 14/08/2017.
 */

public class TimelineAdPostViewHolder extends PostViewHolder
    implements MoPubNative.MoPubNativeNetworkListener {
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final LinearLayout cardLayout;
  private final ProgressBar adLoading;

  public TimelineAdPostViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject,
      TimelineAdsRepository timelineAdsRepository) {
    super(view);
    timelineAdsRepository.init(this);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.cardLayout = (LinearLayout) view.findViewById(R.id.card_layout);
    this.adLoading = (ProgressBar) view.findViewById(R.id.timeline_ad_loading);
  }

  @Override public void setPost(Post card, int position) {
    cardLayout.setVisibility(View.VISIBLE);
    adLoading.setVisibility(View.VISIBLE);
  }

  @Override public void onNativeLoad(NativeAd nativeAd) {
    View view = nativeAd.createAdView(itemView.getContext(), null);
    nativeAd.clear(view);
    nativeAd.renderAdView(view);
    nativeAd.prepare(view);
    cardLayout.removeAllViews();
    cardLayout.addView(view);
    adLoading.setVisibility(View.GONE);
  }

  @Override public void onNativeFail(NativeErrorCode nativeErrorCode) {
    cardLayout.setVisibility(View.GONE);
    Log.d(this.getClass()
        .getCanonicalName(), "TimelineAdsRepository.onNativeFail");
  }
}

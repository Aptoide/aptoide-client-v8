package cm.aptoide.pt.ads;

import cm.aptoide.pt.logger.Logger;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import rx.subjects.PublishSubject;

public class MoPubInterstitialAdListener implements MoPubInterstitial.InterstitialAdListener {

  private PublishSubject<InterstitialClick> interstitialClick;

  public MoPubInterstitialAdListener(PublishSubject<InterstitialClick> interstitialClick) {
    this.interstitialClick = interstitialClick;
  }

  @Override public void onInterstitialLoaded(MoPubInterstitial interstitial) {
    interstitialClick.onNext(InterstitialClick.INTERSTITIAL_LOADED);
  }

  @Override
  public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
    Logger.getInstance()
        .e("Mopub_Interstitial", errorCode.toString());
  }

  @Override public void onInterstitialShown(MoPubInterstitial interstitial) {

  }

  @Override public void onInterstitialClicked(MoPubInterstitial interstitial) {
    interstitialClick.onNext(InterstitialClick.INTERSTITIAL_CLICKED);
  }

  @Override public void onInterstitialDismissed(MoPubInterstitial interstitial) {

  }
}

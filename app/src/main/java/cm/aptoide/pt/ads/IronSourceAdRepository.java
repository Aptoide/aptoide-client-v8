package cm.aptoide.pt.ads;

import android.app.Activity;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.logger.Logger;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import rx.subjects.PublishSubject;

public class IronSourceAdRepository {
  private static final String TAG = "IronSourceAdRepository";
  private final Activity activity;
  private final PublishSubject<AdEvent> eventSubject;

  private boolean showInterstitial;

  public IronSourceAdRepository(Activity activity) {
    this.activity = activity;
    this.eventSubject = PublishSubject.create();
    this.showInterstitial = false;
  }

  public void initialize() {
    IronSource.init(activity, BuildConfig.IRONSOURCE_APPLICATION_ID);
    IronSource.setInterstitialListener(new InterstitialListener() {
      @Override public void onInterstitialAdReady() {
        if (showInterstitial) {
          showInterstitialAd();
          showInterstitial = false;
        }
      }

      @Override public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
        Logger.getInstance()
            .e(TAG, "Interstitial Ad failed to load. Reason: " + ironSourceError.getErrorMessage());
      }

      @Override public void onInterstitialAdOpened() {
      }

      @Override public void onInterstitialAdClosed() {
      }

      @Override public void onInterstitialAdShowSucceeded() {
        eventSubject.onNext(AdEvent.IMPRESSION);
      }

      @Override public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
        Logger.getInstance()
            .e(TAG, "Interstitial Ad failed to show. Reason: " + ironSourceError.getErrorMessage());
      }

      @Override public void onInterstitialAdClicked() {
        eventSubject.onNext(AdEvent.CLICK);
      }
    });
  }

  public void loadInterstitialAd() {
    IronSource.loadInterstitial();
  }

  public void showInterstitialAd() {
    if (IronSource.isInterstitialReady()) {
      IronSource.showInterstitial(BuildConfig.IRONSOURCE_APPVIEW_INTERSTITIAL_PROD_PLACEMENT_ID);
    } else {
      showInterstitial = true;
    }
  }

  public PublishSubject<AdEvent> getAdEventSubject() {
    return eventSubject;
  }

  public void onPause() {
    IronSource.onPause(activity);
  }

  public void onResume() {
    IronSource.onResume(activity);
  }
}

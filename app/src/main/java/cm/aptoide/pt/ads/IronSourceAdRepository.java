package cm.aptoide.pt.ads;

import android.app.Activity;
import android.util.Log;
import cm.aptoide.pt.BuildConfig;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import rx.subjects.PublishSubject;

public class IronSourceAdRepository {
  private static final String TAG = "IronSourceAdRepository";
  private final Activity activity;
  private final PublishSubject<Void> impressionSubject;
  private final PublishSubject<Void> clickSubject;

  private boolean showInterstitial;

  public IronSourceAdRepository(Activity activity) {
    this.activity = activity;
    this.clickSubject = PublishSubject.create();
    this.impressionSubject = PublishSubject.create();
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
        Log.i(TAG, "Interstitial Ad failed to load. Reason: " + ironSourceError.getErrorMessage());
      }

      @Override public void onInterstitialAdOpened() {
      }

      @Override public void onInterstitialAdClosed() {
      }

      @Override public void onInterstitialAdShowSucceeded() {
        impressionSubject.onNext(null);
      }

      @Override public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
        Log.i(TAG, "Interstitial Ad failed to show. Reason: " + ironSourceError.getErrorMessage());
      }

      @Override public void onInterstitialAdClicked() {
        clickSubject.onNext(null);
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

  public PublishSubject<Void> getImpressionSubject() {
    return impressionSubject;
  }

  public PublishSubject<Void> getClickSubject() {
    return clickSubject;
  }

  public void onPause() {
    IronSource.onPause(activity);
  }

  public void onResume() {
    IronSource.onResume(activity);
  }
}

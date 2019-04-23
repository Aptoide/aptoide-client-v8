package cm.aptoide.pt.ads;

import cm.aptoide.pt.logger.Logger;
import rx.Single;

public class MoPubConsentManager implements MoPubConsentDialogManager, MoPubConsentDialogView {



  @Override public Single<Boolean> shouldShowConsentDialog() {
    return Single.just(false);
  }

  @Override public void showConsentDialog() {
    Logger.getInstance()
        .d("MoPubConsentManager", "showConsentDialog is disabled");
  }
}

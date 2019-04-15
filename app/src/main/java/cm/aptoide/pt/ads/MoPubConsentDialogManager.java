package cm.aptoide.pt.ads;

import rx.Single;

public interface MoPubConsentDialogManager {

  Single<Boolean> shouldShowConsentDialog();
}

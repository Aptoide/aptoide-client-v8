package cm.aptoide.pt.ads;

import android.support.annotation.NonNull;
import cm.aptoide.pt.logger.Logger;
import com.mopub.common.privacy.ConsentDialogListener;
import com.mopub.common.privacy.PersonalInfoManager;
import com.mopub.mobileads.MoPubErrorCode;
import rx.Single;

public class MoPubConsentManager implements MoPubConsentDialogManager, MoPubConsentDialogView {

  private final PersonalInfoManager personalInfoManager;

  public MoPubConsentManager(PersonalInfoManager personalInfoManager) {
    this.personalInfoManager = personalInfoManager;
  }

  @Override public void showConsentDialog() {
    personalInfoManager.loadConsentDialog(new ConsentDialogListener() {
      @Override public void onConsentDialogLoaded() {
        if (personalInfoManager != null && personalInfoManager.isConsentDialogReady()) {
          personalInfoManager.showConsentDialog();
        }
      }

      @Override public void onConsentDialogLoadFailed(@NonNull MoPubErrorCode moPubErrorCode) {
        Logger.getInstance()
            .d("MoPubConsent",
                "MoPub Consent dialog failed to load due to " + moPubErrorCode.toString());
      }
    });
  }

  @Override public Single<Boolean> shouldShowConsentDialog() {
    return Single.just(personalInfoManager.shouldShowConsentDialog());
  }
}

package cm.aptoide.pt.ads;

import android.support.annotation.NonNull;
import cm.aptoide.pt.logger.Logger;
import com.mopub.common.MoPub;
import com.mopub.common.privacy.ConsentDialogListener;
import com.mopub.common.privacy.PersonalInfoManager;
import com.mopub.mobileads.MoPubErrorCode;
import rx.Single;

public class MoPubConsentManager implements MoPubConsentDialogManager, MoPubConsentDialogView {

  private boolean wasMoPubConsentDialogShown;

  public MoPubConsentManager() {
  }

  @Override public void showConsentDialog() {
    PersonalInfoManager personalInfoManager = MoPub.getPersonalInformationManager();
    personalInfoManager.loadConsentDialog(new ConsentDialogListener() {
      @Override public void onConsentDialogLoaded() {
        if (personalInfoManager != null
            && personalInfoManager.isConsentDialogReady()
            && !wasMoPubConsentDialogShown) {
          wasMoPubConsentDialogShown = true;
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
    PersonalInfoManager personalInfoManager = MoPub.getPersonalInformationManager();
    return Single.just(personalInfoManager.shouldShowConsentDialog());
  }
}

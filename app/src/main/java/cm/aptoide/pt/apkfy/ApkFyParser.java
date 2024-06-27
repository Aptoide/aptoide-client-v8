package cm.aptoide.pt.apkfy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import cm.aptoide.pt.DeepLinkIntentReceiver;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import io.reactivex.Single;

/**
 * Created by neuro on 30-12-2016.
 */

public class ApkFyParser {

  private final Context context;
  private final Intent intent;
  private final SharedPreferences securePreferences;
  private final ApkfyManager apkfyManager;

  public ApkFyParser(Context context, Intent intent, SharedPreferences securePreferences,
      ApkfyManager apkfyManager) {
    this.context = context;
    this.intent = intent;
    this.securePreferences = securePreferences;
    this.apkfyManager = apkfyManager;
  }

  public void run() {
    Single.just(SecurePreferences.shouldRunApkFy(securePreferences))
        .filter(shouldRun -> shouldRun)
        .flatMapSingle(__ -> apkfyManager.getApkfy())
        .doOnSuccess(apkfyModel -> updateApkfy(apkfyModel))
        .doOnError(throwable -> throwable.printStackTrace())
        .subscribe(apkfyModel -> {
        }, throwable -> throwable.printStackTrace());
  }

  private void updateApkfy(ApkfyModel apkfyModel) {
    if (apkfyModel.getAppId() != null) {
      intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT, true);
      intent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_ID_KEY,
          apkfyModel.getAppId());
      if (apkfyModel.getOemId() != null && !apkfyModel.getOemId().isEmpty()) {
        intent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.OEM_ID_KEY, apkfyModel.getOemId());
      }
      intent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.APK_FY, true);
      SecurePreferences.setApkFyRun(securePreferences);
      context.startActivity(intent);
    }
  }
}

package cm.aptoide.pt.utils;

/*
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
*/

/**
 * Created by diogoloureiro on 16/09/16.
 */
public class CrashlyticsUtils {

  private static final String key_language = "Language";
  private static final String key_APKFY_APP_ID = "APKFY_APP_ID";
  private static final String key_ReviewID = "ReviewID";
  private static final String key_Errors = "Errors";

  /*public static void setupCrashlytics(Context context, boolean fabric_configured) {
    Crashlytics crashlyticsKit = new Crashlytics.Builder().core(
        new CrashlyticsCore.Builder().disabled(!fabric_configured).build()).build();
    Fabric.with(context, crashlyticsKit);
  }

  public static void sendCrashlytic(Throwable throwable,
      ArrayList<CrashlyticsSets> crashlyticsSets) {
    runCrashArray(crashlyticsSets);
    if (throwable != null) Crashlytics.logException(throwable);
  }

  public static void sendCrashlytic(Exception e, ArrayList<CrashlyticsSets> crashlyticsSets) {
    runCrashArray(crashlyticsSets);
    if (e != null) Crashlytics.logException(e);
  }

  private static void runCrashArray(ArrayList<CrashlyticsSets> crashlyticsSets) {
    if (crashlyticsSets != null && !crashlyticsSets.isEmpty()) {
      for (CrashlyticsSets c : crashlyticsSets) {
        Crashlytics.setString(c.getKey(), c.getValue());
      }
    }
  }*/
}
package cm.aptoide.pt.aptoidesdk.misc;

import android.text.TextUtils;
import cm.aptoide.pt.aptoidesdk.BuildConfig;
import cm.aptoide.pt.aptoidesdk.ads.RxAptoide;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.Arrays;

/**
 * Created by neuro on 17-11-2016.
 */

public class SdkUtils {
  public static class FileParameters {

    public static String getDownloadQueryString() {
      return TextUtils.join("&",
          Arrays.asList(getAptoideSdkVersion(), getDevice(), getAndroidVersion(), getResolution(),
              getAptoideUUID(), getOemId()));
    }

    private static String getAptoideSdkVersion() {
      return "aptoide_version=aptoide-sdk-" + BuildConfig.VERSION_NAME;
    }

    private static String getDevice() {
      return "device=" + AptoideUtils.SystemU.getModel();
    }

    private static String getAndroidVersion() {
      return "android_version=" + AptoideUtils.SystemU.getRelease();
    }

    private static String getResolution() {
      return "resolution=" + AptoideUtils.ScreenU.getScreenSizePixels();
    }

    private static String getAptoideUUID() {
      return "aptoide_uid=" + new IdsRepositoryImpl(
          SecurePreferencesImplementation.getInstance(AptoideUtils.getContext()),
          AptoideUtils.getContext()).getAptoideClientUUID();
    }

    private static String getOemId() {
      return "oemid=" + RxAptoide.getOemid();
    }
  }
}

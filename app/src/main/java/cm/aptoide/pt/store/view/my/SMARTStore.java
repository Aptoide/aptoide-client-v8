package cm.aptoide.pt.store.view.my;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class SMARTStore {
    public static final String TAG = SMARTStore.class.getSimpleName();

    public static final String USE_RELEASE_APP_STORE_KEY = "Use Release App Store";
    public static final String STORE_COLOR = "red";

    private static final String RELEASE_STORE_NAME = "smarttech-iq";
    private static final String DEBUG_STORE_NAME = "aptoide-test-store";
    private static final boolean IS_DEBUG_BUILD = "userdebug".equals(Build.TYPE);
    private static final String DEFAULT_STORE_NAME = IS_DEBUG_BUILD ? DEBUG_STORE_NAME : RELEASE_STORE_NAME;

    private SMARTStore() {}

    public static String getStoreName(Context context) {
        if (context == null) {
            return DEFAULT_STORE_NAME;
        }
        return isReleaseAppStore(context) ? RELEASE_STORE_NAME : DEBUG_STORE_NAME;
    }

    private static boolean isReleaseAppStore(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            return Settings.Global.getInt(contentResolver, USE_RELEASE_APP_STORE_KEY) == 1;
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error while getting app store env setting", e);
            return !IS_DEBUG_BUILD;
        }
    }
}

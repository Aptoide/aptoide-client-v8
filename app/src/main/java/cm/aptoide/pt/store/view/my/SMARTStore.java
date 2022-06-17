package cm.aptoide.pt.store.view.my;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class SMARTStore {
    private static final String TAG = SMARTStore.class.getSimpleName();

    public static final String FIELD_ID_IA_APP_STORE_ENV = "smart_app_store_env";

    private static final boolean DEBUG = "userdebug".equals(Build.TYPE);
    private static final String STORE_RELEASE_NAME = "smarttech-iq";
    private static final String STORE_DEBUG_NAME = "aptoide-test-store";
    private static final String DEFAULT_STORE_NAME = DEBUG ? STORE_DEBUG_NAME : STORE_RELEASE_NAME;

    public static final String STORE_COLOR = "red";

    private SMARTStore() {}

    public static String getStoreName(Context context) {
        if (context == null) {
            Log.e(TAG, "Cannot get context for " + FIELD_ID_IA_APP_STORE_ENV + ", returning default environment!");
            return DEFAULT_STORE_NAME;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            String name = Settings.Global.getString(context.getContentResolver(), FIELD_ID_IA_APP_STORE_ENV);
            return name == null ? DEFAULT_STORE_NAME : name;
        }
        return DEFAULT_STORE_NAME;
    }
}

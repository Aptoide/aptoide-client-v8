package cm.aptoide.pt.store.view.my;

import android.os.Build;

public class SMARTStore {
    private static final boolean DEBUG = "userdebug".equals(Build.TYPE);

    public static final String STORE_NAME = DEBUG ? "aptoide-test-store" : "smarttech-iq";
    public static final String STORE_COLOR = "red";

    private SMARTStore() {}
}
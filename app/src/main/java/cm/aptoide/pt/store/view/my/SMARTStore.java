package cm.aptoide.pt.store.view.my;

import android.os.Build;

public class SMARTStore {
    private static final boolean DEBUG = "userdebug".equals(Build.TYPE);

    //TODO replace url with original one when provided
    public static final String STORE_NAME = DEBUG ? "smarttech-iq" : "pein95";
    public static final String STORE_COLOR = "red";

    private SMARTStore() {}
}
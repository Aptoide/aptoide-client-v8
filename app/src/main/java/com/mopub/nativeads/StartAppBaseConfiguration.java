package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.BuildConfig;
import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import java.util.Map;

public class StartAppBaseConfiguration extends BaseAdapterConfiguration {

  private final String NETWORK_SDK_VERSION = "3.10.1";

  @NonNull @Override public String getAdapterVersion() {
    return NETWORK_SDK_VERSION + ".0";
  }

  @Nullable @Override public String getBiddingToken(@NonNull Context context) {
    return null;
  }

  @NonNull @Override public String getMoPubNetworkName() {
    return "startapp";
  }

  @NonNull @Override public String getNetworkSdkVersion() {
    return NETWORK_SDK_VERSION;
  }

  @Override public void initializeNetwork(@NonNull Context context,
      @Nullable Map<String, String> configuration,
      @NonNull OnNetworkInitializationFinishedListener listener) {
    StartAppSDK.init(context, BuildConfig.MOPUB_STARTAPP_APPLICATION_ID, false);
    StartAppAd.disableSplash();
    listener.onNetworkInitializationFinished(this.getClass(),
        MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
  }
}

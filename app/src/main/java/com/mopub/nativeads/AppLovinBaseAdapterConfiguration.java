package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.BuildConfig;
import com.applovin.sdk.AppLovinSdk;
import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.Map;

public class AppLovinBaseAdapterConfiguration extends BaseAdapterConfiguration {

  @NonNull @Override public String getAdapterVersion() {
    return BuildConfig.APPLOVIN_ADAPTER_VERSION;
  }

  @Nullable @Override public String getBiddingToken(@NonNull Context context) {
    return null;
  }

  @NonNull @Override public String getMoPubNetworkName() {
    return "applovin";
  }

  @NonNull @Override public String getNetworkSdkVersion() {
    return BuildConfig.APPLOVIN_SDK_VERSION;
  }

  @Override public void initializeNetwork(@NonNull Context context,
      @Nullable Map<String, String> configuration,
      @NonNull OnNetworkInitializationFinishedListener listener) {
    AppLovinSdk.initializeSdk(context);
    listener.onNetworkInitializationFinished(this.getClass(),
        MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
  }
}

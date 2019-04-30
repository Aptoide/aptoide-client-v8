package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.BuildConfig;
import com.inmobi.sdk.InMobiSdk;
import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import java.util.Map;

public class InMobiBaseAdapterConfiguration extends BaseAdapterConfiguration {

  @NonNull @Override public String getAdapterVersion() {
    return "7.2.6.0";
  }

  @Nullable @Override public String getBiddingToken(@NonNull Context context) {
    return null;
  }

  @NonNull @Override public String getMoPubNetworkName() {
    return "InMobi";
  }

  @NonNull @Override public String getNetworkSdkVersion() {
    return "7.2.6";
  }

  @Override public void initializeNetwork(@NonNull Context context,
      @Nullable Map<String, String> configuration,
      @NonNull OnNetworkInitializationFinishedListener listener) {
    InMobiSdk.init(context, BuildConfig.MOPUB_INMOBI_ACCOUNT_ID);
  }
}

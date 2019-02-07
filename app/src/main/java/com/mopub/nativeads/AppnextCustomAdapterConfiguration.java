package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import java.util.Map;

public class AppnextCustomAdapterConfiguration extends BaseAdapterConfiguration {
  @NonNull @Override public String getAdapterVersion() {
    return null;
  }

  @Nullable @Override public String getBiddingToken(@NonNull Context context) {
    return null;
  }

  @NonNull @Override public String getMoPubNetworkName() {
    return null;
  }

  @NonNull @Override public String getNetworkSdkVersion() {
    return null;
  }

  @Override public void initializeNetwork(@NonNull Context context,
      @Nullable Map<String, String> configuration,
      @NonNull OnNetworkInitializationFinishedListener listener) {

  }
}

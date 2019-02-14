package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.appnext.base.Appnext;
import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.Map;

public class AppnextCustomAdapterConfiguration extends BaseAdapterConfiguration {
  @NonNull @Override public String getAdapterVersion() {
    return "2.4.4.472";
  }

  @Nullable @Override public String getBiddingToken(@NonNull Context context) {
    return null;
  }

  @NonNull @Override public String getMoPubNetworkName() {
    return "appnext";
  }

  @NonNull @Override public String getNetworkSdkVersion() {
    return "2.4.4";
  }

  @Override public void initializeNetwork(@NonNull Context context,
      @Nullable Map<String, String> configuration,
      @NonNull OnNetworkInitializationFinishedListener listener) {
    Appnext.init(context);
    listener.onNetworkInitializationFinished(this.getClass(),
        MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
  }
}

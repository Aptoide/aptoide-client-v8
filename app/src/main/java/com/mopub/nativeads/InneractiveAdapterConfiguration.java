package com.mopub.nativeads;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.fyber.inneractive.sdk.external.InneractiveAdManager;
import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.common.Preconditions;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.Map;

import static com.fyber.inneractive.sdk.external.InneractiveMediationDefs.REMOTE_KEY_APP_ID;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.CUSTOM_WITH_THROWABLE;

public class InneractiveAdapterConfiguration extends BaseAdapterConfiguration {

  private static final String MOPUB_NETWORK_NAME = "Fyber";

  /**
   * 4-digit versioning scheme, of which the leftmost 3 digits correspond to the network SDK
   * version,
   * and the last digit denotes the minor version number referring to an adapter release
   */
  @NonNull @Override public String getAdapterVersion() {
    return InneractiveAdManager.getVersion() + ".0";
  }

  @Nullable @Override public String getBiddingToken(@NonNull Context context) {
    return null;
  }

  @NonNull @Override public String getMoPubNetworkName() {
    return MOPUB_NETWORK_NAME;
  }

  @NonNull @Override public String getNetworkSdkVersion() {
    return InneractiveAdManager.getVersion();
  }

  @Override public void initializeNetwork(@NonNull Context context,
      @Nullable Map<String, String> configuration,
      @NonNull OnNetworkInitializationFinishedListener listener) {

    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(listener);

    boolean initializeNetworkSuccess = false;

    synchronized (InneractiveAdapterConfiguration.class) {
      try {
        if (configuration != null && !TextUtils.isEmpty(configuration.get(REMOTE_KEY_APP_ID))) {
          final String appId = configuration.get(REMOTE_KEY_APP_ID);
          InneractiveAdManager.initialize(context, appId);
          initializeNetworkSuccess = true;
        }
      } catch (Exception e) {
        MoPubLog.log(CUSTOM_WITH_THROWABLE,
            "Initializing Inneractive has encountered " + "an exception.", e);
      }
    }

    if (initializeNetworkSuccess) {
      listener.onNetworkInitializationFinished(InneractiveAdapterConfiguration.class,
          MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
    } else {
      listener.onNetworkInitializationFinished(InneractiveAdapterConfiguration.class,
          MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
    }
  }
}
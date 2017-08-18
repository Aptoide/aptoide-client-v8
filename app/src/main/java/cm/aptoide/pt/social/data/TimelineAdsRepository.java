package cm.aptoide.pt.social.data;

import android.content.Context;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;
import java.util.EnumSet;

/**
 * Created by jdandrade on 14/08/2017.
 */

public class TimelineAdsRepository {
  private final Context context;

  public TimelineAdsRepository(Context context) {
    this.context = context;
  }

  public void init(MoPubNative.MoPubNativeNetworkListener moPubNativeNetworkListener) {
    MoPubNative moPubNative =
        new MoPubNative(context, BuildConfig.MOPUB_NATIVE_AD_UNIT_ID, moPubNativeNetworkListener);

    ViewBinder viewBinder =
        new ViewBinder.Builder(R.layout.mopub_native_ad).mainImageId(R.id.timeline_ad_image)
            .iconImageId(R.id.card_image)
            .titleId(R.id.card_title)
            .textId(R.id.timeline_ad_description)
            .callToActionId(R.id.timeline_ad_button)
            .build();

    MoPubStaticNativeAdRenderer adRenderer = new MoPubStaticNativeAdRenderer(viewBinder);
    moPubNative.registerAdRenderer(adRenderer);
    moPubNative.makeRequest(new RequestParameters.Builder().desiredAssets(
        EnumSet.of(RequestParameters.NativeAdAsset.TITLE, RequestParameters.NativeAdAsset.TEXT,
            RequestParameters.NativeAdAsset.MAIN_IMAGE, RequestParameters.NativeAdAsset.ICON_IMAGE,
            RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT))
        .build());
  }
}

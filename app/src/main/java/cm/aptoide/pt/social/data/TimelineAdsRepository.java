package cm.aptoide.pt.social.data;

import android.content.Context;
import android.view.View;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;
import java.util.EnumSet;
import rx.Single;

/**
 * Created by jdandrade on 14/08/2017.
 */

public class TimelineAdsRepository {

  private final Context context;
  private final BehaviorRelay<AdResponse> ad;

  public TimelineAdsRepository(Context context, BehaviorRelay<AdResponse> ad) {
    this.context = context;
    this.ad = ad;
  }

  public void fetchAd() {
    MoPubNative moPubNative = new MoPubNative(context, BuildConfig.MOPUB_NATIVE_AD_UNIT_ID,
        new MoPubNative.MoPubNativeNetworkListener() {
          @Override public void onNativeLoad(NativeAd nativeAd) {
            View view = nativeAd.createAdView(context, null);
            nativeAd.clear(view);
            nativeAd.renderAdView(view);
            nativeAd.prepare(view);
            ad.call(new AdResponse(view, AdResponse.Status.ok));
          }

          @Override public void onNativeFail(NativeErrorCode nativeErrorCode) {
            ad.call(new AdResponse(null, AdResponse.Status.error));
          }
        });
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

  public Single<AdResponse> getAd() {
    return ad.first()
        .toSingle();
  }
}

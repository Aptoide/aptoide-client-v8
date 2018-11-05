package cm.aptoide.pt.ads;

import android.content.Context;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.app.AppNextAdResult;
import com.appnext.core.AppnextError;
import com.appnext.nativeads.NativeAd;
import com.appnext.nativeads.NativeAdListener;
import com.appnext.nativeads.NativeAdRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import rx.subjects.PublishSubject;

public class AppNextAdRepository {

  private final Context context;
  private PublishSubject<AppNextAdResult> clickSubject;

  public AppNextAdRepository(Context context) {
    this.context = context;
    this.clickSubject = PublishSubject.create();
  }

  public PublishSubject<AppNextAdResult> loadAd(List<String> keywords) {
    PublishSubject<AppNextAdResult> subject = PublishSubject.create();
    NativeAd nativeAd = new NativeAd(context, BuildConfig.APPNEXT_PLACEMENT_ID);
    nativeAd.setAdListener(new NativeAdListener() {
      @Override public void onAdLoaded(final NativeAd nativeAd) {
        super.onAdLoaded(nativeAd);
        subject.onNext(new AppNextAdResult(nativeAd));
        subject.onCompleted();
      }

      @Override public void onAdClicked(NativeAd nativeAd) {
        super.onAdClicked(nativeAd);
        clickSubject.onNext(new AppNextAdResult(nativeAd));
      }

      @Override public void onError(NativeAd nativeAd, AppnextError appnextError) {
        super.onError(nativeAd, appnextError);
        subject.onNext(new AppNextAdResult(appnextError));
        subject.onCompleted();
      }

      @Override public void adImpression(NativeAd nativeAd) {
        super.adImpression(nativeAd);
      }
    });
    nativeAd.loadAd(
        new NativeAdRequest().setCachingPolicy(NativeAdRequest.CachingPolicy.STATIC_ONLY)
            .setCategories(getCategory(keywords)));
    return subject;
  }

  public PublishSubject<AppNextAdResult> clickAd() {
    return clickSubject;
  }

  public String getCategory(List<String> keywords) {
    if (keywords == null) return "";
    for (String s : keywords) {
      s = s.substring(0, 1)
          .toUpperCase() + s.substring(1)
          .toLowerCase();
      try {
        s = URLEncoder.encode(s, "utf-8");
      } catch (UnsupportedEncodingException e) {
        // Ignore
      }
      switch (s) {
        case "Action":
          return s;
        case "Adventure":
          return s;
        case "Arcade":
          return s;
        case "Arcade%20%26%20Action":
          return s;
        case "Board":
          return s;
        case "Books":
        case "Reference":
        case "Books%20%26%20Reference":
          return s;
        case "Brain%20%26%20Puzzle":
          return s;
        case "Business":
          return s;
        case "Card":
          return s;
        case "Cards%20%26%20Casino":
          return s;
        case "Casino":
          return s;
        case "Casual":
          return s;
        case "Comics":
          return s;
        case "Communication":
          return "Communications";
        case "Education":
          return s;
        case "Educational":
          return s;
        case "Entertainment":
          return s;
        case "Family":
          return s;
        case "Finance":
          return s;
        case "Health":
        case "Health%20%26%20Fitness":
          return s;
        case "Demo":
        case "Libraries%20%26%20Demo":
          return s;
        case "Lifestyle":
          return s;
        case "Live%20Wallpaper":
          return s;
        case "Multimedia":
        case "Media%20%26%20Video":
          return "Media%20%26%20Video";
        case "Medical":
          return s;
        case "Music":
          return s;
        case "Music%20%26%20Audio":
          return s;
        case "News & Weather":
        case "News%20%26%20Magazines":
          return "News%20%26%20Magazines";
        case "Personalization":
          return s;
        case "Photography":
          return s;
        case "Productivity":
          return s;
        case "Puzzle":
          return s;
        case "Racing":
          return s;
        case "Role%20Playing":
          return s;
        case "Shopping":
          return s;
        case "Simulation":
          return s;
        case "Social":
          return s;
        case "Sports":
          return s;
        case "Sports%20Games":
          return s;
        case "Strategy":
          return s;
        case "Tools":
          return s;
        case "Travel":
        case "Travel%20%26%20Local":
          return "Travel%20%26%20Local";
        case "Trivia":
          return s;
        case "Weather":
          return s;
        case "Word":
          return s;
      }
    }
    return "";
  }
}

package cm.aptoide.pt.ads;

import android.content.Context;

import com.appnext.core.AppnextError;
import com.appnext.nativeads.NativeAd;
import com.appnext.nativeads.NativeAdListener;
import com.appnext.nativeads.NativeAdRequest;

import java.util.List;

import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.app.AppNextAdResult;
import rx.subjects.PublishSubject;

public class AppNextAdRepository {

//    private final PublishSubject<AppNextAdResult> subject;
    private final Context context;

    public AppNextAdRepository(Context context, PublishSubject<AppNextAdResult> publishSubject){
        this.context = context;
//        this.subject = publishSubject;
    }

    public PublishSubject<AppNextAdResult> loadAd(List<String> keywords){
        PublishSubject<AppNextAdResult> subject = PublishSubject.create();
        String placementId = BuildConfig.APPNEXT_PLACEMENT_ID;
        NativeAd nativeAd = new NativeAd(context, BuildConfig.APPNEXT_PLACEMENT_ID);
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(final NativeAd nativeAd) {
                super.onAdLoaded(nativeAd);
                subject.onNext(new AppNextAdResult(nativeAd));
                subject.onCompleted();
            }

            @Override
            public void onAdClicked(NativeAd nativeAd) {
                super.onAdClicked(nativeAd);
            }

            @Override
            public void onError(NativeAd nativeAd, AppnextError appnextError) {
                super.onError(nativeAd, appnextError);
                subject.onNext(new AppNextAdResult(appnextError));
                subject.onCompleted();
            }

            @Override
            public void adImpression(NativeAd nativeAd) {
                super.adImpression(nativeAd);
            }
        });
        nativeAd.loadAd(new NativeAdRequest()
                .setCachingPolicy(NativeAdRequest.CachingPolicy.STATIC_ONLY)
                .setCategories(getCategory(keywords)));
        return subject;
    }

    public String getCategory(List<String> keywords){
        for(String s : keywords){
            s = s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
            switch (s){
                case "Action":
                    return s;
                case "Adventure":
                    return s;
                case "Arcade":
                    return s;
                case "Arcade & Action":
                    return s;
                case "Board":
                    return s;
                case "Books & Reference":
                    return s;
                case "Brain & Puzzle":
                    return s;
                case "Business":
                    return s;
                case "Card":
                    return s;
                case "Cards & Casino":
                    return s;
                case "Casino":
                    return s;
                case "Casual":
                    return s;
                case "Comics":
                    return s;
                case "Communications":
                    return s;
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
                case "Health & Fitness":
                    return s;
                case "Libraries & Demo":
                    return s;
                case "Lifestyle":
                    return s;
                case "Live Wallpaper":
                    return s;
                case "Media & Video":
                    return s;
                case "Medical":
                    return s;
                case "Music":
                    return s;
                case "Music & Audio":
                    return s;
                case "News & Magazines":
                    return s;
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
                case "Role Playing":
                    return s;
                case "Shopping":
                    return s;
                case "Simulation":
                    return s;
                case "Social":
                    return s;
                case "Sports":
                    return s;
                case "Sports Games":
                    return s;
                case "Strategy":
                    return s;
                case "Tools":
                    return s;
                case "Travel & Local":
                    return s;
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

package cm.aptoide.pt.ads;

import android.content.Context;
import cm.aptoide.pt.ads.data.AppodealAdResult;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;
import java.util.List;
import rx.subjects.PublishSubject;

public class AppodealAdRepository {
  private final Context context;
  private PublishSubject<AppodealAdResult> clickSubject;

  public AppodealAdRepository(Context context) {
    this.context = context;
    this.clickSubject = PublishSubject.create();
  }

  public PublishSubject<AppodealAdResult> loadAd(List<String> keywords) {
    PublishSubject<AppodealAdResult> subject = PublishSubject.create();
    Appodeal.setNativeCallbacks(new NativeCallbacks() {
      @Override public void onNativeLoaded() {
        subject.onNext(new AppodealAdResult());
        subject.onCompleted();
      }

      @Override public void onNativeFailedToLoad() {
        subject.onNext(new AppodealAdResult());
        subject.onCompleted();
      }

      @Override public void onNativeShown(NativeAd nativeAd) {

      }

      @Override public void onNativeClicked(NativeAd nativeAd) {
        clickSubject.onNext(new AppodealAdResult(nativeAd));
      }

      @Override public void onNativeExpired() {

      }
    });
    return clickAd();
  }

  public PublishSubject<AppodealAdResult> clickAd() {
    return clickSubject;
  }
}

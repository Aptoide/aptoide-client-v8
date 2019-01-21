package cm.aptoide.pt.abtesting.experiments;

public class MoPubInterstitialAdExperiment {

  public MoPubInterstitialAdExperiment() {
  }

  public Observable<Boolean> loadInterstitial() {
    return Observable.just(false);
  }
}

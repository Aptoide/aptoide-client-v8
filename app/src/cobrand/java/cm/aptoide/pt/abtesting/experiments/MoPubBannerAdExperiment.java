package cm.aptoide.pt.abtesting.experiments;

public class MoPubInterstitialAdExperiment {

  public MoPubInterstitialAdExperiment() {
  }

  public Observable<Boolean> loadBanner() {
    return Observable.just(false);
  }
}

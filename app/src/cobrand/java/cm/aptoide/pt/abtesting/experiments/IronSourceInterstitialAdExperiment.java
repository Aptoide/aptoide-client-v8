package cm.aptoide.pt.abtesting.experiments;

import cm.aptoide.pt.abtesting.Experiment;
import rx.Observable;
import rx.Single;

/**
 * Dummy class
 */
public class IronSourceInterstitialAdExperiment {

  public IronSourceInterstitialAdExperiment() {
  }

  public Observable<Experiment> loadInterstitial() {
    return Observable.just(new Experiment());
  }

  public Single<Experiment> showInterstitial() {
    return Single.just(new Experiment());
  }

  public Observable<Boolean> recordAdImpression() {
    return Observable.just(false);
  }

  public Observable<Boolean> recordAdClick() {
    return Observable.just(false);
  }
}

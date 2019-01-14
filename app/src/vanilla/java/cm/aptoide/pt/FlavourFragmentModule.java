package cm.aptoide.pt;

import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.abtesting.experiments.IronSourceInterstitialAdExperiment;
import cm.aptoide.pt.ads.IronSourceAdRepository;
import cm.aptoide.pt.ads.IronSourceAnalytics;
import cm.aptoide.pt.view.FragmentScope;
import dagger.Module;
import dagger.Provides;
import rx.android.schedulers.AndroidSchedulers;

@Module public class FlavourFragmentModule {

  public FlavourFragmentModule() {
  }

  @FragmentScope @Provides
  IronSourceInterstitialAdExperiment providesIronSourceInterstitialAdExperiment(
      ABTestManager abTestManager, IronSourceAdRepository ironSourceAdRepository,
      IronSourceAnalytics ironSourceAnalytics) {
    return new IronSourceInterstitialAdExperiment(abTestManager, AndroidSchedulers.mainThread(),
        ironSourceAdRepository, ironSourceAnalytics);
  }
}

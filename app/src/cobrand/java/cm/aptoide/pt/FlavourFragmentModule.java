package cm.aptoide.pt;

import cm.aptoide.pt.abtesting.experiments.IronSourceInterstitialAdExperiment;
import cm.aptoide.pt.view.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module public class FlavourFragmentModule {

  public FlavourFragmentModule() {
  }

  @FragmentScope @Provides
  IronSourceInterstitialAdExperiment providesIronSourceInterstitialAdExperiment() {
    return new IronSourceInterstitialAdExperiment();
  }
}

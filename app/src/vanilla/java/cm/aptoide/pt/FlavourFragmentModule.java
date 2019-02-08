package cm.aptoide.pt;

import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.abtesting.experiments.MoPubInterstitialAdExperiment;
import cm.aptoide.pt.ads.MoPubAnalytics;
import cm.aptoide.pt.view.FragmentScope;
import cm.aptoide.pt.view.wizard.WizardFragmentProvider;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module public class FlavourFragmentModule {

  public FlavourFragmentModule() {
  }

  @FragmentScope @Provides MoPubInterstitialAdExperiment providesMoPubInterstitialAdExperiment(
      @Named("ab-test") ABTestManager abTestManager, MoPubAnalytics moPubAnalytics) {
    return new MoPubInterstitialAdExperiment(abTestManager, moPubAnalytics);
  }

  @FragmentScope @Provides WizardFragmentProvider providesWizardFragmentProvider() {
    return new WizardFragmentProvider();
  }
}

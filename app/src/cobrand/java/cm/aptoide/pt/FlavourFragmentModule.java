package cm.aptoide.pt;

import cm.aptoide.pt.abtesting.experiments.MoPubInterstitialAdExperiment;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.view.FragmentScope;
import cm.aptoide.pt.view.wizard.WizardFragmentProvider;
import dagger.Module;
import dagger.Provides;

@Module public class FlavourFragmentModule {

  public FlavourFragmentModule() {
  }

  @FragmentScope @Provides MoPubInterstitialAdExperiment providesMoPubInterstitialAdExperiment() {
    return new MoPubInterstitialAdExperiment();
  }

  @FragmentScope @Provides WizardFragmentProvider providesWizardFragmentProvider(
      ThemeManager themeManager) {
    return new WizardFragmentProvider(themeManager);
  }
}

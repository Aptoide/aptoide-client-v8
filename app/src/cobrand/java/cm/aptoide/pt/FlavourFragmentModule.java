package cm.aptoide.pt;

import cm.aptoide.pt.abtesting.experiments.IronSourceInterstitialAdExperiment;
import cm.aptoide.pt.view.FragmentScope;
import cm.aptoide.pt.view.wizard.WizardManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module public class FlavourFragmentModule {

  public FlavourFragmentModule() {
  }

  @FragmentScope @Provides
  IronSourceInterstitialAdExperiment providesIronSourceInterstitialAdExperiment() {
    return new IronSourceInterstitialAdExperiment();
  }

  @FragmentScope @Provides WizardManager providesWizardManager(@Named("aptoide-theme") String theme) {
    return new WizardManager(theme);
  }

}

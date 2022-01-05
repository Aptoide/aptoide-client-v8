package cm.aptoide.pt;

import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.view.FragmentScope;
import cm.aptoide.pt.view.wizard.WizardFragmentProvider;
import dagger.Module;
import dagger.Provides;

@Module public class FlavourFragmentModule {

  public FlavourFragmentModule() {
  }

  @FragmentScope @Provides WizardFragmentProvider providesWizardFragmentProvider(
      ThemeManager themeManager) {
    return new WizardFragmentProvider(themeManager);
  }
}

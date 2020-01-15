package cm.aptoide.pt;

import cm.aptoide.pt.view.FragmentScope;
import cm.aptoide.pt.view.wizard.WizardFragmentProvider;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module public class FlavourFragmentModule {

  public FlavourFragmentModule() {
  }

  @FragmentScope @Provides WizardFragmentProvider providesWizardFragmentProvider(
      @Named("theme-attribute-provider") ThemeAttributeProvider themeAttributeProvider) {
    return new WizardFragmentProvider(themeAttributeProvider);
  }
}

package cm.aptoide.pt;

import cm.aptoide.pt.view.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module public class FlavourFragmentModule {

  public FlavourFragmentModule() {
  }

  @FragmentScope @Provides MoPubInterstitialAdExperiment providesMoPubInterstitialAdExperiment() {
    return new MoPubInterstitialAdExperiment();
  }
}

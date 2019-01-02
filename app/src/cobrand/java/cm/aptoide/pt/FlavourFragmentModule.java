package cm.aptoide.pt;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.abtesting.experiments.IronSourceInterstitialAdExperiment;
import cm.aptoide.pt.view.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module public class FlavourFragmentModule {

  private final Fragment fragment;

  public FlavourFragmentModule(Fragment fragment) {
    this.fragment = fragment;
  }

  @FragmentScope @Provides
  IronSourceInterstitialAdExperiment providesIronSourceInterstitialAdExperiment() {
    return new IronSourceInterstitialAdExperiment();
  }
}

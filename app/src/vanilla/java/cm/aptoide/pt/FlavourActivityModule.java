package cm.aptoide.pt;

import android.support.v7.app.AppCompatActivity;
import dagger.Module;

@Module public class FlavourActivityModule {

  private final AppCompatActivity activity;

  public FlavourActivityModule(AppCompatActivity activity) {
    this.activity = activity;
  }
}

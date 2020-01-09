package cm.aptoide.pt.view;

import android.os.Bundle;
import cm.aptoide.pt.themes.ThemeManager;
import javax.inject.Inject;

public abstract class ThemedActivityView extends ActivityView {
  public @Inject ThemeManager themeManager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActivityComponent().inject(this);
    themeManager.resetToBaseTheme();
  }
}

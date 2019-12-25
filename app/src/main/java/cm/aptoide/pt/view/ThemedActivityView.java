package cm.aptoide.pt.view;

import android.content.res.Configuration;
import android.os.Bundle;
import javax.inject.Inject;
import javax.inject.Named;

public abstract class ThemedActivityView extends ActivityView {
  public @Inject @Named("aptoide-theme") String theme;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActivityComponent().inject(this);
    setSystemTheme(getResources().getConfiguration());
  }

  private void setSystemTheme(Configuration configuration) {
    int currentNightMode = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;
    switch (currentNightMode) {
      case Configuration.UI_MODE_NIGHT_NO:
        setLightTheme();
        break;
      case Configuration.UI_MODE_NIGHT_YES:
        setDarkTheme();
        break;
    }
  }

  private void setLightTheme() {
    ThemeUtils.setStatusBarThemeColor(this, theme);
    ThemeUtils.setAptoideTheme(this, theme);
  }

  private void setDarkTheme() {
    ThemeUtils.setStatusBarThemeColor(this, ThemeUtils.DARK_THEME);
    ThemeUtils.setAptoideTheme(this, ThemeUtils.DARK_THEME);
  }
}

package cm.aptoide.pt.view;

import android.os.Bundle;
import javax.inject.Inject;
import javax.inject.Named;

public abstract class ThemedActivityView extends ActivityView {
  public @Inject @Named("aptoide-theme") String theme;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActivityComponent().inject(this);
    ThemeUtils.setStatusBarThemeColor(this, theme);
    ThemeUtils.setAptoideTheme(this, "dark");
  }
}

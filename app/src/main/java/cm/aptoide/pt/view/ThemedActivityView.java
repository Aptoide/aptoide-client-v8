package cm.aptoide.pt.view;

import android.os.Bundle;
import javax.inject.Inject;
import javax.inject.Named;

public class ThemedActivityView extends ActivityView {
  @Inject @Named("aptoide-theme") String theme;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ThemeUtils.setStatusBarThemeColor(this, theme);
    ThemeUtils.setAptoideTheme(this, theme);
  }
}

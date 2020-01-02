package cm.aptoide.pt.app;

import android.content.res.Resources;
import android.util.TypedValue;

public class ThemeAttributeProvider {

  private Resources.Theme theme;

  public ThemeAttributeProvider(Resources.Theme theme) {
    this.theme = theme;
  }

  public TypedValue getAttributeForTheme(int attributeResourceId) {
    TypedValue value = new TypedValue();
    theme.resolveAttribute(attributeResourceId, value, true);
    return value;
  }
}

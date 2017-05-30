package cm.aptoide.pt.spotandshareandroid.transference;

import android.graphics.drawable.Drawable;

/**
 * Created by filipegoncalves on 08-02-2017.
 */

public class AppViewModel {

  private Drawable imageIcon;
  private String appName;
  private String packageName;//ID
  private boolean selected;

  public AppViewModel(Drawable imageIcon, String appName, String packageName, boolean selected) {
    this.imageIcon = imageIcon;
    this.appName = appName;
    this.packageName = packageName;
    this.selected = selected;
  }

  public Drawable getImageIcon() {
    return imageIcon;
  }

  public String getAppName() {
    return appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}

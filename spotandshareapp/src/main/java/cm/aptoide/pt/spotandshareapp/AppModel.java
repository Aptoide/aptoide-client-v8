package cm.aptoide.pt.spotandshareapp;

/**
 * Created by filipe on 19-06-2017.
 */

import android.graphics.drawable.Drawable;

/**
 * Class to represent apps to be shown on App selection view
 */
public class AppModel {

  private String appName;
  private String packageName;
  private String filePath;
  private Drawable appIcon;
  private final DrawableToBitmapMapper mapper;
  private String obbsFilePath;
  private boolean isSelected;

  public AppModel(String appName, String packageName, String filePath, String obbsFilePath,
      Drawable appIcon, DrawableToBitmapMapper mapper) {
    this.appName = appName;
    this.packageName = packageName;
    this.filePath = filePath;
    this.obbsFilePath = obbsFilePath;
    this.appIcon = appIcon;
    this.mapper = mapper;
  }

  public String getAppName() {
    return appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getFilePath() {
    return filePath;
  }

  public String getObbsFilePath() {
    return obbsFilePath;
  }

  public Drawable getAppIconAsDrawable() {
    return appIcon;
  }

  public byte[] getAppIconAsByteArray() {
    return mapper.convertDrawableToBitmap(appIcon);
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }
}

package cm.aptoide.pt.spotandshareapp;

/**
 * Created by filipe on 19-06-2017.
 */

import android.graphics.drawable.Drawable;

public class AppModel {

  private String appName;
  private String packageName;
  private String filePath;
  private Drawable appIconAsDrawable;
  private byte[] appIconByteArray;
  private final DrawableBitmapMapper mapper;
  private String obbsFilePath;
  private boolean isSelected;

  public AppModel(String appName, String packageName, String filePath, String obbsFilePath,
      Drawable appIconDrawable, DrawableBitmapMapper mapper) {
    this.appName = appName;
    this.packageName = packageName;
    this.filePath = filePath;
    this.obbsFilePath = obbsFilePath;
    this.appIconAsDrawable = appIconDrawable;
    this.mapper = mapper;
  }

  public AppModel(String appName, String packageName, String filePath, String obbsFilePath,
      byte[] appIconByteArray, DrawableBitmapMapper mapper) {
    this.appName = appName;
    this.packageName = packageName;
    this.filePath = filePath;
    this.obbsFilePath = obbsFilePath;
    this.appIconByteArray = appIconByteArray;
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
    return appIconAsDrawable != null ? appIconAsDrawable
        : mapper.convertBitmapToDrawable(appIconByteArray);
  }

  public byte[] getAppIconAsByteArray() {
    return appIconByteArray != null ? appIconByteArray
        : mapper.convertDrawableToBitmap(appIconAsDrawable);
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }
}

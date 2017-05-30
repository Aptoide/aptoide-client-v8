package cm.aptoide.pt.spotandshareandroid.transference;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by filipegoncalves on 29-07-2016.
 */
public class App implements Parcelable {

  public static final Creator<App> CREATOR = new Creator<App>() {
    @Override public App createFromParcel(Parcel in) {
      return new App(in);
    }

    @Override public App[] newArray(int size) {
      return new App[size];
    }
  };
  private transient Drawable imageIcon;
  private String appName;
  private String filePath;
  private String packageName;
  private String obbsFilePath;

  public App(Drawable imageIcon, String appName, String packageName, String filePath) {
    this.imageIcon = imageIcon;
    this.appName = appName;
    this.packageName = packageName;
    this.filePath = filePath;
    this.obbsFilePath = "noObbs";
  }

  protected App(Parcel in) {
    appName = in.readString();
    filePath = in.readString();
    packageName = in.readString();
    obbsFilePath = in.readString();
  }

  public Drawable getImageIcon() {
    return imageIcon;
  }

  public void setImageIcon(Drawable imageIcon) {
    this.imageIcon = imageIcon;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getFilePath() {
    return filePath;
  }

  public String getObbsFilePath() {
    return obbsFilePath;
  }

  public void setObbsFilePath(String obbsFilePath) {
    this.obbsFilePath = obbsFilePath;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(appName);
    dest.writeString(filePath);
    dest.writeString(packageName);
    dest.writeString(obbsFilePath);
  }
}

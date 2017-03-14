package cm.aptoide.pt.spotandshareandroid;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by filipegoncalves on 29-07-2016.
 */
public class App
    implements Parcelable { //highwaygridviewappItem - represents the "BIG" app item - shareApps

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
  private boolean selected;
  private boolean isOnChat; //boolean for final confirmation (is sent)
  private boolean initialCard;//boolean for initial (is sending)
  private String fromOutside;
  private String obbsFilePath;

  public App(Drawable imageIcon, String appName, String packageName, String filePath,
      String fromOutside) {
    this.imageIcon = imageIcon;
    this.appName = appName;
    this.packageName = packageName;
    this.filePath = filePath;
    this.fromOutside = fromOutside;//bool is not serializable. Could use Boolean?
    this.obbsFilePath = "noObbs";
  }

  protected App(Parcel in) {
    appName = in.readString();
    filePath = in.readString();
    packageName = in.readString();
    selected = in.readByte() != 0;
    isOnChat = in.readByte() != 0;
    initialCard = in.readByte() != 0;
    fromOutside = in.readString();
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

  public boolean getSelected() {
    return selected;
  }

  public void setSelected(boolean param) {
    selected = param;
  }

  public boolean isInitialCard() {
    return initialCard;
  }

  public void setInitialCard(boolean initialCard) {
    this.initialCard = initialCard;
  }

  public boolean isOnChat() {
    return isOnChat;
  }

  public void setOnChat(boolean onChat) {
    isOnChat = onChat;
  }

  public String getFromOutside() {
    return fromOutside;
  }

  public void setFromOutside(String fromOutside) {
    this.fromOutside = fromOutside;
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
    dest.writeByte((byte) (selected ? 1 : 0));
    dest.writeByte((byte) (isOnChat ? 1 : 0));
    dest.writeByte((byte) (initialCard ? 1 : 0));
    dest.writeString(fromOutside);
    dest.writeString(obbsFilePath);
  }
}

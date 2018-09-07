package cm.aptoide.pt.app.view;

import cm.aptoide.pt.dataprovider.model.v7.Obb;
import java.util.Collections;
import java.util.List;

/**
 * Created by D01 on 29/08/2018.
 */

class EditorialViewModel {

  private final List<EditorialContent> contentList;
  private final String cardType;
  private final long appId;
  private final String appName;
  private final String packageName;
  private final long size;
  private final String icon;
  private final String graphic;
  private final Obb obb;
  private final long storeId;
  private final String storeName;
  private final String storeTheme;
  private final String vername;
  private final int vercode;
  private final String path;
  private final String backgroundImage;
  private final String pathAlt;
  private final String md5;
  private final boolean loading;
  private final Error error;

  public EditorialViewModel(List<EditorialContent> contentList, String cardType, long appId,
      String appName, String packageName, long size, String icon, String graphic, Obb obb,
      long storeId, String storeName, String storeTheme, String vername, int vercode, String path,
      String backgroundImage, String pathAlt, String md5) {
    this.contentList = contentList;
    this.cardType = cardType;
    this.appId = appId;
    this.appName = appName;
    this.packageName = packageName;
    this.size = size;
    this.icon = icon;
    this.graphic = graphic;
    this.obb = obb;
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeTheme = storeTheme;
    this.vername = vername;
    this.vercode = vercode;
    this.path = path;
    this.backgroundImage = backgroundImage;
    this.pathAlt = pathAlt;
    this.md5 = md5;
    this.loading = false;
    this.error = null;
  }

  public EditorialViewModel(boolean loading) {
    contentList = Collections.emptyList();
    cardType = null;
    this.loading = loading;
    error = null;
    this.appId = -1;
    this.appName = null;
    this.packageName = null;
    this.size = -1;
    this.icon = null;
    this.graphic = null;
    this.obb = null;
    this.storeId = -1;
    this.storeName = null;
    this.storeTheme = null;
    this.vername = null;
    this.vercode = -1;
    this.path = null;
    this.backgroundImage = null;
    this.pathAlt = null;
    this.md5 = null;
  }

  public EditorialViewModel(Error error) {
    contentList = Collections.emptyList();
    cardType = null;
    this.loading = false;
    this.error = error;
    this.appId = -1;
    this.appName = null;
    this.packageName = null;
    this.size = -1;
    this.icon = null;
    this.graphic = null;
    this.obb = null;
    this.storeId = -1;
    this.storeName = null;
    this.storeTheme = null;
    this.vername = null;
    this.vercode = -1;
    this.path = null;
    this.backgroundImage = null;
    this.pathAlt = null;
    this.md5 = null;
  }

  public boolean hasContent() {
    return contentList != null && !contentList.isEmpty();
  }

  public EditorialContent getContent(int position) {
    return contentList.get(position);
  }

  public List<EditorialContent> getContentList() {
    return contentList;
  }

  public String getCardType() {
    return cardType;
  }

  public long getAppId() {
    return appId;
  }

  public String getAppName() {
    return appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public long getSize() {
    return size;
  }

  public String getIcon() {
    return icon;
  }

  public String getGraphic() {
    return graphic;
  }

  public long getStoreId() {
    return storeId;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getStoreTheme() {
    return storeTheme;
  }

  public String getVername() {
    return vername;
  }

  public int getVercode() {
    return vercode;
  }

  public String getPath() {
    return path;
  }

  public String getBackgroundImage() {
    return backgroundImage;
  }

  public boolean hasBackgroundImage() {
    return backgroundImage != null && !backgroundImage.equals("");
  }

  public boolean isLoading() {
    return loading;
  }

  public Error getError() {
    return error;
  }

  public boolean hasError() {
    return error != null;
  }

  public Obb getObb() {
    return obb;
  }

  public String getPathAlt() {
    return pathAlt;
  }

  public String getMd5() {
    return md5;
  }

  public enum Error {
    NETWORK, GENERIC
  }
}

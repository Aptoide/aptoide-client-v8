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
  private final String caption;
  private final String background;
  private final String title;
  private final long appId;
  private final String appName;
  private final float rating;
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
  private final String pathAlt;
  private final String md5;
  private final int placeHolderPosition;
  private final boolean loading;
  private final Error error;

  public EditorialViewModel(List<EditorialContent> contentList, String cardType, String title,
      long appId, String caption, String appName, float rating, String packageName, long size,
      String icon, String graphic, Obb obb, long storeId, String storeName, String storeTheme,
      String versionName, int versionCode, String path, String background, String pathAlt,
      String md5, int placeHolderPosition) {
    this.contentList = contentList;
    this.cardType = cardType;
    this.title = title;
    this.appId = appId;
    this.caption = caption;
    this.appName = appName;
    this.rating = rating;
    this.packageName = packageName;
    this.size = size;
    this.icon = icon;
    this.graphic = graphic;
    this.obb = obb;
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeTheme = storeTheme;
    this.vername = versionName;
    this.vercode = versionCode;
    this.path = path;
    this.background = background;
    this.pathAlt = pathAlt;
    this.md5 = md5;
    this.placeHolderPosition = placeHolderPosition;
    this.loading = false;
    this.error = null;
  }

  public EditorialViewModel(boolean loading) {
    contentList = Collections.emptyList();
    cardType = null;
    title = null;
    this.loading = loading;
    error = null;
    this.appId = -1;
    this.caption = null;
    this.appName = null;
    this.rating = -1;
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
    this.background = null;
    this.pathAlt = null;
    this.md5 = null;
    this.placeHolderPosition = -1;
  }

  public EditorialViewModel(Error error) {
    contentList = Collections.emptyList();
    cardType = null;
    title = null;
    this.loading = false;
    this.error = error;
    this.appId = -1;
    this.caption = null;
    this.appName = null;
    this.rating = -1;
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
    this.background = null;
    this.pathAlt = null;
    this.md5 = null;
    this.placeHolderPosition = -1;
  }

  public EditorialViewModel(List<EditorialContent> editorialContentList, String cardType,
      String title, String caption, String background, int placeHolderPosition) {

    contentList = editorialContentList;
    this.cardType = cardType;
    this.caption = caption;
    this.title = title;
    this.background = background;
    this.placeHolderPosition = placeHolderPosition;
    this.appId = -1;
    this.appName = null;
    this.rating = -1;
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
    this.pathAlt = null;
    this.md5 = null;
    this.loading = false;
    this.error = null;
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
    return background;
  }

  public boolean hasBackgroundImage() {
    return background != null && !background.equals("");
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

  public float getRating() {
    return rating;
  }

  public int getPlaceHolderPosition() {
    return placeHolderPosition;
  }

  public String getCaption() {
    return caption;
  }

  public String getBackground() {
    return background;
  }

  public String getTitle() {
    return title;
  }

  public enum Error {
    NETWORK, GENERIC
  }
}

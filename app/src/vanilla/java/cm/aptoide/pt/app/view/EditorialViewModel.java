package cm.aptoide.pt.app.view;

import cm.aptoide.pt.dataprovider.model.v7.Obb;
import java.util.Collections;
import java.util.List;

import static cm.aptoide.pt.dataprovider.model.v7.EditorialCard.Content;
import static cm.aptoide.pt.dataprovider.model.v7.EditorialCard.Media;

/**
 * Created by D01 on 29/08/2018.
 */

class EditorialViewModel {

  private final List<Content> contentList;
  private final String cardType;
  private final long appId;
  private final String appName;
  private final String packageName;
  private final long size;
  private final String icon;
  private final String graphic;
  private final String uptype;
  private final Obb obb;
  private final long storeId;
  private final String storeName;
  private final String storeAvatar;
  private final String storeTheme;
  private final String vername;
  private final int vercode;
  private final long fileSize;
  private final String path;
  private final String backgroundImage;
  private final boolean loading;
  private final Error error;

  public EditorialViewModel(List<Content> contentList, String cardType, long appId, String appName,
      String packageName, long size, String icon, String graphic, String uptype, Obb obb,
      long storeId, String storeName, String storeAvatar, String storeTheme, String vername,
      int vercode, long fileSize, String path, String backgroundImage) {
    this.contentList = contentList;
    this.cardType = cardType;
    this.appId = appId;
    this.appName = appName;
    this.packageName = packageName;
    this.size = size;
    this.icon = icon;
    this.graphic = graphic;
    this.uptype = uptype;
    this.obb = obb;
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeAvatar = storeAvatar;
    this.storeTheme = storeTheme;
    this.vername = vername;
    this.vercode = vercode;
    this.fileSize = fileSize;
    this.path = path;
    this.backgroundImage = backgroundImage;
    this.loading = false;
    this.error = null;
  }

  public EditorialViewModel() {
    contentList = Collections.emptyList();
    cardType = null;
    this.loading = false;
    error = null;
    this.appId = -1;
    this.appName = null;
    this.packageName = null;
    this.size = -1;
    this.icon = null;
    this.graphic = null;
    this.uptype = null;
    this.obb = null;
    this.storeId = -1;
    this.storeName = null;
    this.storeAvatar = null;
    this.storeTheme = null;
    this.vername = null;
    this.vercode = -1;
    this.fileSize = -1;
    this.path = null;
    this.backgroundImage = null;
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
    this.uptype = null;
    this.obb = null;
    this.storeId = -1;
    this.storeName = null;
    this.storeAvatar = null;
    this.storeTheme = null;
    this.vername = null;
    this.vercode = -1;
    this.fileSize = -1;
    this.path = null;
    this.backgroundImage = null;
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
    this.uptype = null;
    this.obb = null;
    this.storeId = -1;
    this.storeName = null;
    this.storeAvatar = null;
    this.storeTheme = null;
    this.vername = null;
    this.vercode = -1;
    this.fileSize = -1;
    this.path = null;
    this.backgroundImage = null;
  }

  public boolean hasContent() {
    return contentList != null && !contentList.isEmpty();
  }

  public Content getContent(int position) {
    return contentList.get(position);
  }

  public List<Content> getContentList() {
    return contentList;
  }

  public String getTitle(int position) {
    return contentList.get(position)
        .getTitle();
  }

  public String getMessage(int position) {
    return contentList.get(position)
        .getMessage();
  }

  public List<Media> getMedia(int position) {
    return contentList.get(position)
        .getMedia();
  }

  public boolean hasMedia(int position) {
    List<Media> media = getMedia(position);
    return media != null && !media.isEmpty();
  }

  public String getMediaType(int contentPosition, int mediaPosition) {
    return getMedia(contentPosition).get(mediaPosition)
        .getType();
  }

  public boolean isImage(int contentPosition, int mediaPosition) {
    return getMediaType(contentPosition, mediaPosition).equals("image");
  }

  public boolean isVideo(int contentPosition, int mediaPosition) {
    return getMediaType(contentPosition, mediaPosition).equals("video");
  }

  public boolean isListOfMedia(int contentPosition) {
    return getMedia(contentPosition).size() > 1;
  }

  public String getMediaDescription(int contentPosition, int mediaPosition) {
    return getMedia(contentPosition).get(mediaPosition)
        .getDescription();
  }

  public boolean hasMediaDescription(int contentPosition, int mediaPosition) {
    return !getMediaDescription(contentPosition, mediaPosition).equals("");
  }

  public String getMediaUrl(int contentPosition, int mediaPosition) {
    return getMedia(contentPosition).get(mediaPosition)
        .getUrl();
  }

  public String getContentType(int position) {
    return contentList.get(position)
        .getType();
  }

  public boolean isNormalType(String cardType) {
    return cardType.equals("normal");
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

  public String getUptype() {
    return uptype;
  }

  public long getStoreId() {
    return storeId;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getStoreAvatar() {
    return storeAvatar;
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

  public long getFileSize() {
    return fileSize;
  }

  public String getPath() {
    return path;
  }

  public String getBackgroundImage() {
    return backgroundImage;
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

  public enum Error {
    NETWORK, GENERIC
  }
}

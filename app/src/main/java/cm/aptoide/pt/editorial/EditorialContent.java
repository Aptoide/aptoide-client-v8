package cm.aptoide.pt.editorial;

import cm.aptoide.pt.aab.Split;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import java.util.List;

/**
 * Created by D01 on 31/08/2018.
 */

public class EditorialContent {

  private final String title;
  private final List<EditorialMedia> media;
  private final String message;
  private final String type;
  private final long id;
  private final String name;
  private final String icon;
  private final float avg;
  private final String packageName;
  private final long size;
  private final String graphic;
  private final Obb obb;
  private final long storeId;
  private final String storeName;
  private final String verName;
  private final int verCode;
  private final String path;
  private final String pathAlt;
  private final String md5sum;
  private final String actionTitle;
  private final String url;
  private final int position;
  private final boolean isPlaceHolder;
  private final List<Split> splits;
  private final List<String> requiredSplits;
  private List<String> bdsFlags;
  private boolean hasAppc;
  private String rank;

  public EditorialContent(String title, List<EditorialMedia> media, String message, String type,
      long id, String name, String icon, float avg, String packageName, long size, String graphic,
      Obb obb, long storeId, String storeName, String verName, int verCode, String path,
      String pathAlt, String md5sum, String actionTitle, String url, int position,
      List<Split> splits, List<String> requiredSplits, boolean hasAppc, String rank,
      List<String> bdsFlags) {
    this.title = title;
    this.media = media;
    this.message = message;
    this.type = type;
    this.id = id;
    this.name = name;
    this.icon = icon;
    this.avg = avg;
    this.packageName = packageName;
    this.size = size;
    this.graphic = graphic;
    this.obb = obb;
    this.storeId = storeId;
    this.storeName = storeName;
    this.verName = verName;
    this.verCode = verCode;
    this.path = path;
    this.pathAlt = pathAlt;
    this.md5sum = md5sum;
    this.actionTitle = actionTitle;
    this.url = url;
    this.position = position;
    this.splits = splits;
    this.requiredSplits = requiredSplits;
    this.rank = rank;
    this.isPlaceHolder = true;
    this.hasAppc = hasAppc;
    this.bdsFlags = bdsFlags;
  }

  public EditorialContent(String title, List<EditorialMedia> media, String message, String type,
      String actionTitle, String url, int position) {
    this.title = title;
    this.media = media;
    this.message = message;
    this.type = type;
    this.actionTitle = actionTitle;
    this.url = url;
    this.position = position;
    this.isPlaceHolder = false;
    id = -1;
    name = "";
    icon = null;
    avg = 0;
    packageName = "";
    size = 0;
    graphic = "";
    obb = null;
    storeId = -1;
    storeName = "";
    verName = "";
    verCode = -1;
    path = "";
    pathAlt = "";
    md5sum = "";
    splits = null;
    requiredSplits = null;
    hasAppc = false;
    rank = "";
    bdsFlags = null;
  }

  public EditorialContent(String title, List<EditorialMedia> media, String message, String type,
      long id, String name, String icon, float avg, String packageName, long size, String graphic,
      Obb obb, long storeId, String storeName, String verName, int verCode, String path,
      String pathAlt, String md5sum, int position, List<Split> splits, List<String> requiredSplits,
      boolean hasAppc, String rank, List<String> bdsFlags) {
    this.title = title;
    this.media = media;
    this.message = message;
    this.type = type;
    this.id = id;
    this.name = name;
    this.icon = icon;
    this.avg = avg;
    this.packageName = packageName;
    this.size = size;
    this.graphic = graphic;
    this.obb = obb;
    this.storeId = storeId;
    this.storeName = storeName;
    this.verName = verName;
    this.verCode = verCode;
    this.path = path;
    this.pathAlt = pathAlt;
    this.md5sum = md5sum;
    this.position = position;
    this.splits = splits;
    this.requiredSplits = requiredSplits;
    this.hasAppc = hasAppc;
    this.rank = rank;
    this.isPlaceHolder = true;
    this.bdsFlags = bdsFlags;
    actionTitle = "";
    url = "";
  }

  public EditorialContent(String title, List<EditorialMedia> media, String message, String type,
      int position) {
    this.title = title;
    this.media = media;
    this.message = message;
    this.type = type;
    this.position = position;
    this.isPlaceHolder = false;
    id = -1;
    name = "";
    icon = null;
    avg = 0;
    packageName = "";
    size = 0;
    graphic = "";
    obb = null;
    storeId = -1;
    storeName = "";
    verName = "";
    verCode = -1;
    path = "";
    pathAlt = "";
    md5sum = "";
    actionTitle = "";
    url = "";
    this.splits = null;
    this.requiredSplits = null;
    this.bdsFlags = null;
    hasAppc = false;
    rank = "";
  }

  public String getMessage() {
    return message;
  }

  public boolean hasMessage() {
    return message != null && !message.equals("");
  }

  public String getType() {
    return type;
  }

  public boolean isPlaceHolderType() {
    return isPlaceHolder;
  }

  public List<EditorialMedia> getMedia() {
    return media;
  }

  public boolean hasMedia() {
    return media != null && !media.isEmpty();
  }

  public boolean hasListOfMedia() {
    return hasMedia() && media.size() > 1;
  }

  public String getTitle() {
    return title;
  }

  public boolean hasTitle() {
    return title != null && !title.equals("");
  }

  public boolean hasAnyMediaDescription() {
    for (EditorialMedia editorialMedia : media) {
      if (editorialMedia.hasDescription()) {
        return true;
      }
    }
    return false;
  }

  public String getAppName() {
    return name;
  }

  public String getIcon() {
    return icon;
  }

  public long getId() {
    return id;
  }

  public float getRating() {
    return avg;
  }

  public String getPackageName() {
    return packageName;
  }

  public long getSize() {
    return size;
  }

  public String getGraphic() {
    return graphic;
  }

  public Obb getObb() {
    return obb;
  }

  public long getStoreId() {
    return storeId;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getVerName() {
    return verName;
  }

  public int getVerCode() {
    return verCode;
  }

  public String getPath() {
    return path;
  }

  public String getPathAlt() {
    return pathAlt;
  }

  public String getMd5sum() {
    return md5sum;
  }

  public String getActionTitle() {
    return actionTitle;
  }

  public String getActionUrl() {
    return url;
  }

  public boolean hasAction() {
    return !actionTitle.equals("");
  }

  public int getPosition() {
    return position;
  }

  public List<Split> getSplits() {
    return this.splits;
  }

  public List<String> getRequiredSplits() {
    return this.requiredSplits;
  }

  public boolean hasAppc() {
    return hasAppc;
  }

  public String getRank() {
    return rank;
  }

  public List<String> getBdsFlags() {
    return bdsFlags;
  }
}

package cm.aptoide.pt.editorial;

import cm.aptoide.pt.aab.Split;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.editorial.EditorialEvent.Type;
import java.util.List;

public class EditorialDownloadEvent {
  private final Type button;
  private final String appName;
  private final String packageName;
  private final String md5sum;
  private final String icon;
  private final String verName;
  private final int verCode;
  private final String path;
  private final String pathAlt;
  private final Obb obb;
  private final DownloadModel.Action action;
  private final long appId;
  private final long size;
  private final String trustedBadge;
  private final String storeName;
  private final List<Split> splits;
  private final List<String> requiredSplits;

  private final List<String> bdsFlags;

  public EditorialDownloadEvent(Type button, String appName, String packageName, String md5sum,
      String icon, String verName, int verCode, String path, String pathAlt, Obb obb, long size,
      List<Split> splits, List<String> requiredSplits, List<String> bdsFlags) {
    this.button = button;
    this.appName = appName;
    this.packageName = packageName;
    this.md5sum = md5sum;
    this.icon = icon;
    this.verName = verName;
    this.verCode = verCode;
    this.path = path;
    this.pathAlt = pathAlt;
    this.obb = obb;
    this.trustedBadge = "";
    this.storeName = "";
    this.appId = -1;
    this.action = null;
    this.size = size;
    this.splits = splits;
    this.requiredSplits = requiredSplits;
    this.bdsFlags = bdsFlags;
  }

  public EditorialDownloadEvent(EditorialViewModel editorialViewModel,
      DownloadModel.Action action) {
    super();
    this.button = null;
    this.appName = editorialViewModel.getBottomCardAppName();
    this.packageName = editorialViewModel.getBottomCardPackageName();
    this.md5sum = editorialViewModel.getBottomCardMd5();
    this.icon = editorialViewModel.getBottomCardIcon();
    this.verName = editorialViewModel.getBottomCardVersionName();
    this.verCode = editorialViewModel.getBottomCardVersionCode();
    this.path = editorialViewModel.getBottomCardPath();
    this.pathAlt = editorialViewModel.getBottomCardPathAlt();
    this.obb = editorialViewModel.getBottomCardObb();
    this.appId = editorialViewModel.getBottomCardAppId();
    this.size = editorialViewModel.getBottomCardSize();
    this.splits = editorialViewModel.getBottomCardSplits();
    this.requiredSplits = editorialViewModel.getBottomCardRequiredSplits();
    this.bdsFlags = editorialViewModel.getBdsFlags();
    this.action = action;
    this.storeName = editorialViewModel.getStoreName();
    this.trustedBadge = editorialViewModel.getRank();
  }

  public EditorialDownloadEvent(Type button, String packageName, String md5, int verCode,
      long appId) {
    this.button = button;
    this.appName = "";
    this.packageName = packageName;
    this.md5sum = md5;
    this.icon = "";
    this.verName = "";
    this.size = 0;
    this.verCode = verCode;
    this.path = "";
    this.pathAlt = "";
    this.obb = null;
    this.appId = appId;
    this.splits = null;
    this.requiredSplits = null;
    this.bdsFlags = null;
    this.action = null;
    this.trustedBadge = "";
    this.storeName = "";
  }

  public EditorialDownloadEvent(Type button, String packageName, String md5, int verCode,
      long appId, DownloadModel.Action action) {
    this.button = button;
    this.appName = "";
    this.packageName = packageName;
    this.md5sum = md5;
    this.icon = "";
    this.verName = "";
    this.size = 0;
    this.verCode = verCode;
    this.path = "";
    this.pathAlt = "";
    this.obb = null;
    this.appId = appId;
    this.splits = null;
    this.requiredSplits = null;
    this.bdsFlags = null;
    this.trustedBadge = "";
    this.storeName = "";
    this.action = action;
  }

  public EditorialDownloadEvent(Type button, String appName, String packageName, String md5sum,
      String icon, String verName, int verCode, String path, String pathAlt, Obb obb,
      DownloadModel.Action action, long size, List<Split> splits, List<String> requiredSplits,
      String trustedBadge, String storeName, List<String> bdsFlags) {
    this.button = button;
    this.appName = appName;
    this.packageName = packageName;
    this.md5sum = md5sum;
    this.icon = icon;
    this.verName = verName;
    this.verCode = verCode;
    this.path = path;
    this.pathAlt = pathAlt;
    this.obb = obb;
    this.trustedBadge = trustedBadge;
    this.storeName = storeName;
    this.appId = -1;
    this.action = action;
    this.size = size;
    this.splits = splits;
    this.requiredSplits = requiredSplits;
    this.bdsFlags = bdsFlags;
  }

  public Type getClickType() {
    return button;
  }

  public String getAppName() {
    return appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getMd5() {
    return md5sum;
  }

  public String getIcon() {
    return icon;
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

  public Obb getObb() {
    return obb;
  }

  public DownloadModel.Action getAction() {
    return action;
  }

  public long getAppId() {
    return appId;
  }

  public long getSize() {
    return size;
  }

  public List<Split> getSplits() {
    return this.splits;
  }

  public List<String> getRequiredSplits() {
    return this.requiredSplits;
  }

  public String getTrustedBadge() {
    return trustedBadge;
  }

  public String getStoreName() {
    return storeName;
  }

  public List<String> getBdsFlags() {
    return bdsFlags;
  }
}

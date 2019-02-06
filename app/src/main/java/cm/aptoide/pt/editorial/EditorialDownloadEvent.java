package cm.aptoide.pt.editorial;

import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.editorial.EditorialEvent.Type;

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

  public EditorialDownloadEvent(Type button, String appName, String packageName, String md5sum,
      String icon, String verName, int verCode, String path, String pathAlt, Obb obb) {
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
    this.appId = -1;
    this.action = null;
  }

  public EditorialDownloadEvent(EditorialDownloadEvent editorialDownloadEvent,
      DownloadModel.Action action) {
    super();
    this.button = editorialDownloadEvent.getClickType();
    this.appName = editorialDownloadEvent.getAppName();
    this.packageName = editorialDownloadEvent.getPackageName();
    this.md5sum = editorialDownloadEvent.getMd5();
    this.icon = editorialDownloadEvent.getIcon();
    this.verName = editorialDownloadEvent.getVerName();
    this.verCode = editorialDownloadEvent.getVerCode();
    this.path = editorialDownloadEvent.getPath();
    this.pathAlt = editorialDownloadEvent.getPathAlt();
    this.obb = editorialDownloadEvent.getObb();
    this.appId = -1;
    this.action = action;
  }

  public EditorialDownloadEvent(EditorialViewModel editorialViewModel,
      DownloadModel.Action action) {
    super();
    this.button = null;
    this.appName = editorialViewModel.getBottomCardAppName();
    this.packageName = editorialViewModel.getBottomCardPackageName();
    this.md5sum = editorialViewModel.getBottomCardMd5();
    this.icon = editorialViewModel.getBottomCardIcon();
    this.verName = editorialViewModel.getBottomCardVername();
    this.verCode = editorialViewModel.getBottomCardVercode();
    this.path = editorialViewModel.getBottomCardPath();
    this.pathAlt = editorialViewModel.getBottomCardPathAlt();
    this.obb = editorialViewModel.getBottomCardObb();
    this.appId = editorialViewModel.getBottomCardAppId();
    this.action = action;
  }

  public EditorialDownloadEvent(Type button, String packageName, String md5, int verCode,
      long appId) {
    this.button = button;
    this.appName = "";
    this.packageName = packageName;
    this.md5sum = md5;
    this.icon = "";
    this.verName = "";
    this.verCode = verCode;
    this.path = "";
    this.pathAlt = "";
    this.obb = null;
    this.appId = appId;
    this.action = null;
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
}

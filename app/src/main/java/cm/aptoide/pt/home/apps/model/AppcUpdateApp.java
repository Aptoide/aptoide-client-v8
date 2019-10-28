package cm.aptoide.pt.home.apps.model;

public class AppcUpdateApp extends UpdateApp {

  private boolean hasPromotion;
  private float appcReward;

  public AppcUpdateApp(String name, String md5, String icon, String packageName, int progress,
      boolean isIndeterminate, String version, int versionCode, Status updateStatus, long appId,
      boolean hasPromotion, float appcReward) {
    super(name, md5, icon, packageName, progress, isIndeterminate, version, versionCode,
        updateStatus, appId);
    this.hasPromotion = hasPromotion;
    this.appcReward = appcReward;
  }

  public boolean hasPromotion() {
    return hasPromotion;
  }

  public float getAppcReward() {
    return appcReward;
  }

  @Override public Type getType() {
    return Type.APPC_MIGRATION;
  }
}

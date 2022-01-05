package cm.aptoide.pt.view.app;

public class ApplicationGraphic extends Application {
  private final String featureGraphic;

  public ApplicationGraphic(String name, String icon, float rating, int downloads,
      String packageName, long appId, String tag, boolean hasBilling, String featureGraphic) {
    super(name, icon, rating, downloads, packageName, appId, tag, hasBilling);
    this.featureGraphic = featureGraphic;
  }

  public String getFeatureGraphic() {
    return featureGraphic;
  }
}

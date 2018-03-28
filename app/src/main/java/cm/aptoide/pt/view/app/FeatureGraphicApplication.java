package cm.aptoide.pt.view.app;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class FeatureGraphicApplication extends Application {

  private final String featureGraphic;

  public FeatureGraphicApplication(String name, String icon, float rating, int downloads,
      String packageName, long appId, String featureGraphic, String tag, String storeName) {
    super(name, icon, rating, downloads, packageName, appId, tag, storeName);
    this.featureGraphic = featureGraphic;
  }

  public String getFeatureGraphic() {
    return featureGraphic;
  }
}

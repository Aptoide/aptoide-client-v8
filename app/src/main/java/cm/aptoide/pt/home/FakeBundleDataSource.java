package cm.aptoide.pt.home;

import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.FeatureGraphicApplication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 14/03/2018.
 */

public class FakeBundleDataSource implements BundleDataSource {

  @Override public Single<HomeBundlesModel> loadFreshHomeBundles() {
    return getHomeBundles();
  }

  @Override public Single<HomeBundlesModel> loadNextHomeBundles(int offset, int limit) {
    return loadFreshHomeBundles();
  }

  @Override public boolean hasMore(Integer offset) {
    return true;
  }

  public Single<HomeBundlesModel> getHomeBundles() {
    return Single.just(new HomeBundlesModel(getFakeBundles(), false, 0));
  }

  public List<HomeBundle> getFakeBundles() {
    List<Application> appsList = new ArrayList<>();
    String icon = "https://placeimg.com/640/480/any";
    Application aptoide = new Application("Aptoide", icon, 0, 1000, "cm.aptoide.pt", 300, "", "");
    appsList.add(aptoide);
    Application facebook =
        new Application("Facebook", icon, (float) 4.2, 1000, "katana.facebook.com", 30, "", "");
    appsList.add(facebook);
    appsList.add(aptoide);
    appsList.add(facebook);
    appsList.add(aptoide);
    appsList.add(facebook);
    appsList.add(aptoide);
    appsList.add(facebook);

    List<Application> appsForEditorsList = new ArrayList<>();
    FeatureGraphicApplication aptoideFeatureGraphic =
        new FeatureGraphicApplication("Aptoide", icon, 0, 1000, "cm.aptoide.pt", 300, icon, "", "");
    appsList.add(aptoideFeatureGraphic);
    FeatureGraphicApplication facebookFeatureGraphic =
        new FeatureGraphicApplication("Facebook", icon, (float) 4.2, 1000, "katana.facebook.com",
            30, icon, "", "");
    appsForEditorsList.add(facebookFeatureGraphic);
    appsForEditorsList.add(aptoideFeatureGraphic);
    appsForEditorsList.add(facebookFeatureGraphic);
    appsForEditorsList.add(aptoideFeatureGraphic);
    appsForEditorsList.add(facebookFeatureGraphic);
    appsForEditorsList.add(aptoideFeatureGraphic);
    appsForEditorsList.add(facebookFeatureGraphic);
    appsForEditorsList.add(aptoideFeatureGraphic);
    appsForEditorsList.add(facebookFeatureGraphic);
    AppBundle appBundle =
        new AppBundle("As escolhas do filipe", appsForEditorsList, AppBundle.BundleType.EDITORS,
            null, "");
    AppBundle appBundle1 =
        new AppBundle("piores apps locais", appsList, AppBundle.BundleType.APPS, null, "");
    AppBundle appBundle2 =
        new AppBundle("um pouco melhor apps", appsList, AppBundle.BundleType.APPS, null, "");

    List<HomeBundle> appBundles = new ArrayList<>();
    appBundles.add(appBundle);
    appBundles.add(appBundle1);
    appBundles.add(appBundle2);
    appBundles.add(
        new AdBundle("Highlighted", new AdsTagWrapper(Collections.emptyList(), ""), null, ""));
    List<Application> apps = new ArrayList<>();
    apps.add(new Application("asf wallet",
        "http://pool.img.aptoide.com/asf-store/ace60f6352f6dd9289843b5b0b2ab3d4_icon.png", 5,
        1000000, "asf.wallet.android.com", 36057221, "", ""));
    appBundles.add(new SocialBundle(apps, HomeBundle.BundleType.SOCIAL, null, "TAG",
        "http://pool.img.aptoide.com/asf-store/3bf5adf05843f9f28c486d5ddef8f873_ravatar.jpg",
        "asf-store"));
    return appBundles;
  }
}

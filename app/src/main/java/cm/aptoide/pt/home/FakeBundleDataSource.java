package cm.aptoide.pt.home;

import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.FeatureGraphicApplication;
import java.util.ArrayList;
import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 14/03/2018.
 */

public class FakeBundleDataSource implements BundleDataSource {

  @Override public Single<List<HomeBundle>> getFreshHomeBundles() {
    return Single.just(getFakeBundles());
  }

  @Override public Single<List<HomeBundle>> getNextHomeBundles() {
    return getFreshHomeBundles();
  }

  @Override public boolean hasMorePosts() {
    return false;
  }

  public List<HomeBundle> getFakeBundles() {
    List<Application> tmp = new ArrayList<>();
    String icon = "https://placeimg.com/640/480/any";
    Application aptoide = new Application("Aptoide", icon, 0, 1000, "cm.aptoide.pt", 300);
    tmp.add(aptoide);
    Application facebook =
        new Application("Facebook", icon, (float) 4.2, 1000, "katana.facebook.com", 30);
    tmp.add(facebook);
    tmp.add(aptoide);
    tmp.add(facebook);
    tmp.add(aptoide);
    tmp.add(facebook);
    tmp.add(aptoide);
    tmp.add(facebook);

    List<Application> tmp1 = new ArrayList<>();
    FeatureGraphicApplication aptoideFeatureGraphic =
        new FeatureGraphicApplication("Aptoide", icon, 0, 1000, "cm.aptoide.pt", 300, icon);
    tmp.add(aptoideFeatureGraphic);
    FeatureGraphicApplication facebookFeatureGraphic =
        new FeatureGraphicApplication("Facebook", icon, (float) 4.2, 1000, "katana.facebook.com",
            30, icon);
    tmp1.add(facebookFeatureGraphic);
    tmp1.add(aptoideFeatureGraphic);
    tmp1.add(facebookFeatureGraphic);
    tmp1.add(aptoideFeatureGraphic);
    tmp1.add(facebookFeatureGraphic);
    tmp1.add(aptoideFeatureGraphic);
    tmp1.add(facebookFeatureGraphic);
    tmp1.add(aptoideFeatureGraphic);
    tmp1.add(facebookFeatureGraphic);
    AppBundle appBundle =
        new AppBundle("As escolhas do filipe", tmp1, AppBundle.BundleType.EDITORS, null, "");
    AppBundle appBundle1 =
        new AppBundle("piores apps locais", tmp, AppBundle.BundleType.APPS, null, "");
    AppBundle appBundle2 =
        new AppBundle("um pouco melhor apps", tmp, AppBundle.BundleType.APPS, null, "");

    List<HomeBundle> appBundles = new ArrayList<>();
    appBundles.add(appBundle);
    appBundles.add(appBundle1);
    appBundles.add(appBundle2);
    return appBundles;
  }
}

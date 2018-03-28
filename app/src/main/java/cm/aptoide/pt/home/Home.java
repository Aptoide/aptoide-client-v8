package cm.aptoide.pt.home;

import android.support.annotation.NonNull;
import cm.aptoide.pt.view.app.Application;
import java.util.ArrayList;
import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class Home {

  private final BundlesRepository bundlesRepository;

  public Home(BundlesRepository bundlesRepository) {
    this.bundlesRepository = bundlesRepository;
  }

  public Single<HomeBundlesModel> loadHomeBundles() {
    return bundlesRepository.loadHomeBundles();
  }

  public Single<HomeBundlesModel> loadFreshHomeBundles() {
    return bundlesRepository.loadFreshHomeBundles();
  }

  public Single<HomeBundlesModel> loadNextHomeBundles() {
    return bundlesRepository.loadNextHomeBundles();
  }

  public boolean hasMore() {
    return bundlesRepository.hasMore();
  }

  @NonNull private List<HomeBundle> addFakeSocialBundleTo(HomeBundlesModel homeBundlesModel) {
    List<HomeBundle> homeBundles = new ArrayList<>();
    List<Application> apps = new ArrayList<>();
    apps.add(new Application("asf wallet",
        "http://pool.img.aptoide.com/asf-store/ace60f6352f6dd9289843b5b0b2ab3d4_icon.png", 5,
        1000000, "asf.wallet.android.com", 36057221, "", ""));
    homeBundles.add(new SocialBundle(apps, HomeBundle.BundleType.SOCIAL, null, "TAG",
        "http://pool.img.aptoide.com/asf-store/3bf5adf05843f9f28c486d5ddef8f873_ravatar.jpg",
        "asf-store"));
    homeBundles.addAll(homeBundlesModel.getList());
    return homeBundles;
  }
}

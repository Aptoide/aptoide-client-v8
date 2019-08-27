package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v7.Event;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Single;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by D01 on 25/06/2018.
 */

public class BundlesRepositoryTest {
  private static final String HOME_BUNDLE_KEY = "Home_Bundle";
  @Mock private BundleDataSource bundleDataSource;
  @Mock private Event event;
  private BundlesRepository bundlesRepository;

  @Before public void setupBundlesRepositoryTest() {
    MockitoAnnotations.initMocks(this);
    bundlesRepository =
        new BundlesRepository(bundleDataSource, new HashMap<>(), new HashMap<>(), 5);
  }

  @Test public void loadHomeBundlesNoCacheTest() {
    //When the manager asks for bundles and there's no cached bundles, then it should call the bundleDataSource for more apps
    when(bundleDataSource.loadNextHomeBundles(0, 5, HOME_BUNDLE_KEY)).thenReturn(
        Single.just(new HomeBundlesModel(true)));
    bundlesRepository.loadHomeBundles();
    verify(bundleDataSource).loadNextHomeBundles(0, 5, HOME_BUNDLE_KEY);
  }

  @Test public void loadHomeBundlesWithCacheTest() {
    HomeBundle homeBundle =
        new AppBundle("title", Collections.emptyList(), HomeBundle.BundleType.APPS, event, "tag",
            "tag-more");
    List<HomeBundle> bundles = new ArrayList<>();
    bundles.add(homeBundle);
    Map<String, List<HomeBundle>> cachedBundles = new HashMap<>();
    cachedBundles.put(HOME_BUNDLE_KEY, bundles);
    bundlesRepository = new BundlesRepository(bundleDataSource, cachedBundles, new HashMap<>(), 5);

    //When the manager asks for bundles then it should return a model with the cached bundles
    bundlesRepository.loadHomeBundles()
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(bundles);
  }

  @Test public void loadFreshHomeBundlesTest() {
    HomeBundle homeBundle =
        new AppBundle("title", Collections.emptyList(), HomeBundle.BundleType.APPS, event, "tag",
            "tag-more");
    List<HomeBundle> bundles = new ArrayList<>();
    bundles.add(homeBundle);
    Map<String, List<HomeBundle>> cachedBundles = new HashMap<>();
    cachedBundles.put(HOME_BUNDLE_KEY, bundles);
    bundlesRepository = new BundlesRepository(bundleDataSource, cachedBundles, new HashMap<>(), 5);

    HomeBundle freshHomeBundle =
        new AppBundle("title", Collections.emptyList(), HomeBundle.BundleType.APPS, event, "tag",
            "tag-more");
    List<HomeBundle> freshBundles = new ArrayList<>();
    freshBundles.add(freshHomeBundle);
    //When it requests apps to the bundleDataSource then return a list of apps
    when(bundleDataSource.loadFreshHomeBundles(HOME_BUNDLE_KEY)).thenReturn(
        Single.just(new HomeBundlesModel(freshBundles, false, 0)));
    //Then it should return the requested bundle list
    bundlesRepository.loadFreshHomeBundles()
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(freshBundles);
    //and cache should have been updated with only the new bundle
    bundlesRepository.loadHomeBundles()
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(freshBundles);
  }

  @Test public void loadNextHomeBundlesNoCacheTest() {
    HomeBundle freshHomeBundle =
        new AppBundle("title", Collections.emptyList(), HomeBundle.BundleType.APPS, event, "tag",
            "tag-more");
    List<HomeBundle> freshBundles = new ArrayList<>();
    freshBundles.add(freshHomeBundle);
    //When it requests apps to the bundleDataSource then return a list of apps
    when(bundleDataSource.loadNextHomeBundles(0, 5, HOME_BUNDLE_KEY)).thenReturn(
        Single.just(new HomeBundlesModel(freshBundles, false, 0)));
    //Then it should return the requested bundle list
    bundlesRepository.loadNextHomeBundles()
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(freshBundles);
    //and cache should have been updated with only the new bundle
    bundlesRepository.loadHomeBundles()
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(freshBundles);
  }

  @Test public void loadNextHomeBundlesWithCacheTest() {
    HomeBundle homeBundle =
        new AppBundle("title", Collections.emptyList(), HomeBundle.BundleType.APPS, event, "tag",
            "tag-more");
    List<HomeBundle> bundles = new ArrayList<>();
    bundles.add(homeBundle);
    Map<String, List<HomeBundle>> cachedBundles = new HashMap<>();
    cachedBundles.put(HOME_BUNDLE_KEY, bundles);
    bundlesRepository = new BundlesRepository(bundleDataSource, cachedBundles, new HashMap<>(), 5);

    HomeBundle freshHomeBundle =
        new AppBundle("title", Collections.emptyList(), HomeBundle.BundleType.APPS, event, "tag",
            "tag-more");
    List<HomeBundle> freshBundles = new ArrayList<>();
    freshBundles.add(freshHomeBundle);
    //When it requests apps to the bundleDataSource then return a list of apps
    when(bundleDataSource.loadNextHomeBundles(0, 5, HOME_BUNDLE_KEY)).thenReturn(
        Single.just(new HomeBundlesModel(freshBundles, false, 0)));
    //Then it should return the requested bundle list
    bundlesRepository.loadNextHomeBundles()
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(freshBundles);
    //and cache should have been updated with the previous bundles and the new one
    bundles.addAll(freshBundles);
    bundlesRepository.loadHomeBundles()
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(bundles);
  }

  @Test public void homeHasMoreTest() {
    //When then manager asks if there's more bundles to be requested
    when(bundleDataSource.hasMore(0, HOME_BUNDLE_KEY)).thenReturn(false);
    bundlesRepository.hasMore();
    //Then it should call a method from the BundleDataSource to verify that
    verify(bundleDataSource).hasMore(0, HOME_BUNDLE_KEY);
  }

  @Test public void hasMoreWithKeyTest() {
    //When then manager asks if there's more bundles to be requested with a given title
    when(bundleDataSource.hasMore(0, "title")).thenReturn(false);
    bundlesRepository.hasMore("title");
    //Then it should call a method from the BundleDataSource to verify that
    verify(bundleDataSource).hasMore(0, "title");
  }

  @Test public void loadBundlesNoCacheTest() {
    //When the manager asks for bundles and there's no cached bundles, then it should call the bundleDataSource for more apps
    when(bundleDataSource.loadNextBundleForEvent("url", 0, "title", 5)).thenReturn(
        Single.just(new HomeBundlesModel(true)));
    bundlesRepository.loadBundles("title", "url");
    verify(bundleDataSource).loadNextBundleForEvent("url", 0, "title", 5);
  }

  @Test public void loadBundlesWithCacheTest() {
    HomeBundle homeBundle =
        new AppBundle("title", Collections.emptyList(), HomeBundle.BundleType.APPS, event, "tag",
            "tag-more");
    List<HomeBundle> bundles = new ArrayList<>();
    bundles.add(homeBundle);
    Map<String, List<HomeBundle>> cachedBundles = new HashMap<>();
    cachedBundles.put("title", bundles);
    bundlesRepository = new BundlesRepository(bundleDataSource, cachedBundles, new HashMap<>(), 5);

    //When the manager asks for bundles then it should return a model with the cached bundles
    bundlesRepository.loadBundles("title", "url")
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(bundles);
  }

  @Test public void loadNextBundlesForEventNoCacheTest() {
    HomeBundle freshHomeBundle =
        new AppBundle("title", Collections.emptyList(), HomeBundle.BundleType.APPS, event, "tag",
            "tag-more");
    List<HomeBundle> freshBundles = new ArrayList<>();
    freshBundles.add(freshHomeBundle);
    //When it requests apps to the bundleDataSource then return a list of apps
    when(bundleDataSource.loadNextBundleForEvent("url", 0, "title", 5)).thenReturn(
        Single.just(new HomeBundlesModel(freshBundles, false, 0)));
    //Then it should return the requested bundle list
    bundlesRepository.loadNextBundles("title", "url")
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(freshBundles);
    //and cache should have been updated with only the new bundle
    bundlesRepository.loadBundles("title", "url")
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(freshBundles);
  }

  @Test public void loadNextBundlesForEventWithCacheTest() {
    HomeBundle homeBundle =
        new AppBundle("title", Collections.emptyList(), HomeBundle.BundleType.APPS, event, "tag",
            "tag-more");
    List<HomeBundle> bundles = new ArrayList<>();
    bundles.add(homeBundle);
    Map<String, List<HomeBundle>> cachedBundles = new HashMap<>();
    cachedBundles.put("title", bundles);
    bundlesRepository = new BundlesRepository(bundleDataSource, cachedBundles, new HashMap<>(), 5);

    HomeBundle freshHomeBundle =
        new AppBundle("title", Collections.emptyList(), HomeBundle.BundleType.APPS, event, "tag",
            "tag-more");
    List<HomeBundle> freshBundles = new ArrayList<>();
    freshBundles.add(freshHomeBundle);
    //When it requests apps to the bundleDataSource then return a list of apps
    when(bundleDataSource.loadNextBundleForEvent("url", 0, "title", 5)).thenReturn(
        Single.just(new HomeBundlesModel(freshBundles, false, 0)));
    //Then it should return the requested bundle list
    bundlesRepository.loadNextBundles("title", "url")
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(freshBundles);
    //and cache should have been updated with the previous bundles and the new one
    bundles.addAll(freshBundles);
    bundlesRepository.loadBundles("title", "url")
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(bundles);
  }

  @Test public void loadFreshBundlesTest() {
    HomeBundle homeBundle =
        new AppBundle("title", Collections.emptyList(), HomeBundle.BundleType.APPS, event, "tag",
            "tag-more");
    List<HomeBundle> bundles = new ArrayList<>();
    bundles.add(homeBundle);
    Map<String, List<HomeBundle>> cachedBundles = new HashMap<>();
    cachedBundles.put("title", bundles);
    bundlesRepository = new BundlesRepository(bundleDataSource, cachedBundles, new HashMap<>(), 5);

    HomeBundle freshHomeBundle =
        new AppBundle("title", Collections.emptyList(), HomeBundle.BundleType.APPS, event, "tag",
            "tag-more");
    List<HomeBundle> freshBundles = new ArrayList<>();
    freshBundles.add(freshHomeBundle);
    //When it requests apps to the bundleDataSource then return a list of apps
    when(bundleDataSource.loadFreshBundleForEvent("url", "title")).thenReturn(
        Single.just(new HomeBundlesModel(freshBundles, false, 0)));
    //Then it should return the requested bundle list
    bundlesRepository.loadFreshBundles("title", "url")
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(freshBundles);
    //and cache should have been updated with only the new bundle
    bundlesRepository.loadBundles("title", "url")
        .map(HomeBundlesModel::getList)
        .test()
        .assertValue(freshBundles);
  }

  @Test public void loadABundleWithAnErrorTest() {
    //When the manager request new fresh bundles, then it should request bundles to the bundleDataSource and return a generic error
    when(bundleDataSource.loadFreshHomeBundles(HOME_BUNDLE_KEY)).thenReturn(
        Single.just(new HomeBundlesModel(HomeBundlesModel.Error.GENERIC)));
    //then it should return a model with a GenericError
    bundlesRepository.loadFreshHomeBundles()
        .map(HomeBundlesModel::getError)
        .test()
        .assertValue(HomeBundlesModel.Error.GENERIC);
  }
}

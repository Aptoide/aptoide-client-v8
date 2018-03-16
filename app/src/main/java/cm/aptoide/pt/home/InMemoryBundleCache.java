package cm.aptoide.pt.home;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdandrade on 16/03/2018.
 */

public class InMemoryBundleCache {
  private List<HomeBundle> cachedBundles;
  private boolean cacheIsDirty;

  public InMemoryBundleCache(ArrayList<HomeBundle> bundles, boolean cacheIsDirty) {
    this.cachedBundles = bundles;
    this.cacheIsDirty = cacheIsDirty;
  }

  public boolean isCacheDirty() {
    return cacheIsDirty;
  }

  public List<HomeBundle> getHomeBundles() {
    return cachedBundles;
  }

  public boolean hasBundles() {
    return !cachedBundles.isEmpty();
  }

  public void addToCache(List<HomeBundle> homeBundles) {
    cachedBundles.addAll(homeBundles);
  }

  public void updateCache(List<HomeBundle> homeBundles) {
    this.cachedBundles = homeBundles;
  }

  public void addToCache(HomeBundlesModel homeBundlesModel) {

  }

  public void updateCache(HomeBundlesModel homeBundlesModel) {
  }
}

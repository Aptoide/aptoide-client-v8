package cm.aptoide.pt.aptoidesdk.ads;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import cm.aptoide.pt.aptoidesdk.Ad;
import cm.aptoide.pt.aptoidesdk.Aptoide;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by neuro on 07-11-2016.
 */

public class StoredAdsTests {

  private Context context;
  private StoredAdsManager storedAdsManager;

  @Test public void readWriteAds() throws Exception {

    init();

    Aptoide.integrate(context, "dummyoem");
    List<Ad> ads = Aptoide.getAds(3, false);

    AptoideAd ad1 = (AptoideAd) ads.get(0);
    AptoideAd ad2 = (AptoideAd) ads.get(1);

    Assert.assertEquals(0, storedAdsManager.size());
    storedAdsManager.addAd(ad1);
    Assert.assertEquals(1, storedAdsManager.size());
    storedAdsManager.addAd(ad2);
    Assert.assertEquals(2, storedAdsManager.size());

    assertAdsPresent(ad1, ad2);

    storedAdsManager.reload();
    assertAdsPresent(ad1, ad2);

    storedAdsManager.clear();
    assertAdsCleared();
  }

  private void init() {
    context = InstrumentationRegistry.getTargetContext();
    AptoideUtils.SystemU.clearApplicationData(context);

    storedAdsManager = StoredAdsManager.getInstance(context);
  }

  private void assertAdsPresent(AptoideAd... ads) {
    for (AptoideAd ad : ads) {
      if (!assertAdPresent(ad)) {
        throw new RuntimeException("Ad " + ad.getName() + " not present in StoredAdsManager!");
      }
    }
  }

  private boolean assertAdPresent(AptoideAd ad) {
    Collection<AptoideAd> all = storedAdsManager.getAll();

    for (AptoideAd tmp : all) {
      if (tmp.equals(ad)) {
        return true;
      }
    }

    return false;
  }

  private void assertAdsCleared() {
    Assert.assertEquals(0, storedAdsManager.size());
  }
}

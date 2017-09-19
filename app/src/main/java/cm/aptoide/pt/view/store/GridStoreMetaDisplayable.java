package cm.aptoide.pt.view.store;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.store.GetHomeMeta;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;
import java.util.Collections;
import java.util.List;

/**
 * Created by neuro on 04-08-2016.
 */
public class GridStoreMetaDisplayable extends DisplayablePojo<GetHomeMeta> {

  private StoreCredentialsProvider storeCredentialsProvider;
  private StoreAnalytics storeAnalytics;

  public GridStoreMetaDisplayable() {
  }

  public GridStoreMetaDisplayable(GetHomeMeta pojo,
      StoreCredentialsProvider storeCredentialsProvider, StoreAnalytics storeAnalytics) {
    super(pojo);
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.storeAnalytics = storeAnalytics;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_store_meta;
  }

  public List<Store.SocialChannel> getSocialLinks() {
    return getPojo().getData()
        .getStore()
        .getSocialChannels() == null ? Collections.EMPTY_LIST : getPojo().getData()
        .getStore()
        .getSocialChannels();
  }

  public StoreCredentialsProvider getStoreCredentialsProvider() {
    return storeCredentialsProvider;
  }

  public String getStoreUserName() {
    return storeCredentialsProvider.get(getStoreName())
        .getUsername();
  }

  public String getStoreName() {
    return getPojo().getData()
        .getStore()
        .getName();
  }

  public String getStorePassword() {
    return storeCredentialsProvider.get(getStoreName())
        .getPasswordSha1();
  }

  public StoreAnalytics getStoreAnalytics() {
    return storeAnalytics;
  }
}

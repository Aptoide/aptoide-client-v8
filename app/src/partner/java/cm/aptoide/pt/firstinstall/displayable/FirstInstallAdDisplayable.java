package cm.aptoide.pt.firstinstall.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created by diogoloureiro on 09/10/2017.
 */

public class FirstInstallAdDisplayable extends DisplayablePojo<MinimalAd> {

  @Getter private String tag;

  public FirstInstallAdDisplayable() {
  }

  public FirstInstallAdDisplayable(MinimalAd minimalAd, String tag) {
    super(minimalAd);
    this.tag = tag;
  }

  @Override protected Configs getConfig() {
    return new Configs(3, false);
  }

  @Override public int getViewLayout() {
    return R.layout.first_install_ad_displayable;
  }
}

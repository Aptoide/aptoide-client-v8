package cm.aptoide.pt.firstinstall.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created by diogoloureiro on 09/10/2017.
 */

public class FirstInstallAdDisplayable extends DisplayablePojo<MinimalAd> {

  private String tag;
  private boolean isSelected;

  public FirstInstallAdDisplayable() {
  }

  public FirstInstallAdDisplayable(MinimalAd minimalAd, String tag, boolean isSelected) {
    super(minimalAd);
    this.tag = tag;
    this.isSelected = isSelected;
  }

  @Override protected Configs getConfig() {
    return new Configs(3, false);
  }

  @Override public int getViewLayout() {
    return R.layout.first_install_ad_displayable;
  }

  public String getTag() {
    return tag;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }
}

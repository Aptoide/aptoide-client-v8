package cm.aptoide.pt.firstinstall.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created by diogoloureiro on 09/10/2017.
 */

public class FirstInstallAppDisplayable extends DisplayablePojo<App> {

  private boolean isSelected;

  public FirstInstallAppDisplayable() {

  }

  public FirstInstallAppDisplayable(App pojo, boolean isSelected) {
    super(pojo);
    this.isSelected = isSelected;
  }

  @Override protected Configs getConfig() {
    return new Configs(3, false);
  }

  @Override public int getViewLayout() {
    return R.layout.first_install_app_displayable;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }
}

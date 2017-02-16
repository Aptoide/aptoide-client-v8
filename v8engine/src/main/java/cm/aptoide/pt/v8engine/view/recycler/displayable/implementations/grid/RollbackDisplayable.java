/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.navigation.NavigationManagerV4;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import rx.Observable;

/**
 * Created by sithengineer on 14/06/16.
 */
public class RollbackDisplayable extends DisplayablePojo<Rollback> {

  private Installer installManager;

  public RollbackDisplayable() {
  }

  public RollbackDisplayable(Installer installManager, Rollback pojo) {
    this(installManager, pojo, false);
  }

  private RollbackDisplayable(Installer installManager, Rollback pojo, boolean fixedPerLineCount) {
    super(pojo);
    this.installManager = installManager;
  }

  public Download getDownloadFromPojo() {
    return new DownloadFactory().create(getPojo());
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.rollback_row;
  }

  public void install(NavigationManagerV4 navigationManager) {
    openAppview(navigationManager);
  }

  public void openAppview(NavigationManagerV4 navigationManager) {
    navigationManager.navigateTo(
        V8Engine.getFragmentProvider().newAppViewFragment(getPojo().getMd5()));
  }

  public Observable<Void> uninstall(Context context, Download appDownload) {
    return installManager.uninstall(context,
        appDownload.getFilesToDownload().get(0).getPackageName(), appDownload.getVersionName());
  }

  public void downgrade(NavigationManagerV4 navigationManager) {
    openAppview(navigationManager);
  }

  public void update(NavigationManagerV4 navigationManager) {
    openAppview(navigationManager);
  }
}

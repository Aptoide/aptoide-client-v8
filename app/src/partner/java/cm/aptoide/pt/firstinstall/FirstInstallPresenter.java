package cm.aptoide.pt.firstinstall;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.repository.request.RequestFactory;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.DisplayablesFactory;
import cm.aptoide.pt.view.recycler.displayable.GridAdDisplayable;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by diogoloureiro on 02/10/2017.
 *
 * First install Presenter implementation
 */

public class FirstInstallPresenter implements Presenter {

  private FirstInstallView view;
  private CrashReport crashReport;
  private Context context;
  private String storeName;
  private String url;

  private RequestFactory requestFactoryCdnPool;
  protected AptoideAccountManager accountManager;
  protected StoreUtilsProxy storeUtilsProxy;
  protected InstalledRepository installedRepository;
  protected StoreAnalytics storeAnalytics;
  protected StoreRepository storeRepository;
  protected String storeTheme;

  private AdsRepository adsRepository;

  FirstInstallPresenter(FirstInstallView view, CrashReport crashReport,
      RequestFactory requestFactoryCdnPool, Context context, String storeName, String url) {
    this.view = view;
    this.crashReport = crashReport;
    this.requestFactoryCdnPool = requestFactoryCdnPool;
    this.context = context;
    this.storeName = storeName;
    this.url = url;
    adsRepository = ((AptoideApplication) context.getApplicationContext()).getAdsRepository();

    // TODO: 03/10/2017 instantiate all variables with the constructor
  }

  @Override public void present() {
    handleInstallAllClick();
    handleCloseClick();

    getFirstInstallWidget();
    getAds();
  }

  @Override public void saveState(Bundle state) {
    //does nothing
  }

  @Override public void restoreState(Bundle state) {
    //does nothing
  }

  private void handleInstallAllClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> installAllClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(installAllClick -> {
        }, crashReport::log);
  }

  private void handleCloseClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> closeClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(closeClick -> {
        }, crashReport::log);
  }

  private Observable<Void> installAllClick() {
    return view.installAllClick();
  }

  private Observable<Void> closeClick() {
    return view.closeClick();
  }

  private void getFirstInstallWidget() {
    requestFactoryCdnPool.newStoreWidgets(url, storeName, StoreContext.first_install)
        .observe(true)
        .subscribeOn(Schedulers.newThread())
        .observeOn(Schedulers.io())
        .flatMap(this::parseDisplayables)
        .subscribe(displayables -> view.addFirstInstallDisplayables(displayables, true),
            crashReport::log);
  }

  private void getAds() {
    adsRepository.getAdsFromFirstInstall(3)
        .subscribeOn(Schedulers.newThread())
        .observeOn(Schedulers.io())
        .flatMap(this::parseDisplayables)
        .subscribe(displayables -> view.addFirstInstallDisplayables(displayables, true));
  }

  private Observable<List<Displayable>> parseDisplayables(GetStoreWidgets getStoreWidgets) {
    return Observable.from(getStoreWidgets.getDataList()
        .getList())
        .concatMapEager(wsWidget -> DisplayablesFactory.parse(wsWidget, storeTheme, storeRepository,
            StoreContext.first_install, context, accountManager, storeUtilsProxy,
            (WindowManager) context.getSystemService(Context.WINDOW_SERVICE),
            context.getResources(), installedRepository, storeAnalytics))
        .toList()
        .first();
  }

  private Observable<List<Displayable>> parseDisplayables(List<MinimalAd> minimalAds) {
    List<Displayable> displayables = new LinkedList<>();
    for (MinimalAd minimalAd : minimalAds) {
      displayables.add(new GridAdDisplayable(minimalAd, "tagexample"));
    }
    return Observable.just(displayables);
  }
}

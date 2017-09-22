/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/06/2016.
 */

package cm.aptoide.pt.view.search.result;

import cm.aptoide.pt.R;
import cm.aptoide.pt.abtesting.ABTest;
import cm.aptoide.pt.abtesting.SearchTabOptions;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.ListSearchApps;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;
import rx.functions.Action0;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchDisplayable extends DisplayablePojo<ListSearchApps.SearchAppsApp> {

  @Getter private Action0 clickCallback;
  private String query;

  public SearchDisplayable() {
  }

  public SearchDisplayable(ListSearchApps.SearchAppsApp searchAppsApp) {
    super(searchAppsApp);
  }

  public SearchDisplayable(ListSearchApps.SearchAppsApp searchAppsApp,
      ABTest<SearchTabOptions> searchAbTest, boolean addSubscribedStores,
      boolean hasMultipleFragments, String query) {
    super(searchAppsApp);
    this.query = query;
    if (searchAbTest != null) {
      clickCallback = () -> {
        if (isConvert(searchAbTest, addSubscribedStores, hasMultipleFragments)) {
          searchAbTest.convert()
              .subscribe(success -> {
              }, throwable -> {
                CrashReport.getInstance()
                    .log(throwable);
              });
        }
      };
    }
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.search_app_row;
  }

  private boolean isConvert(ABTest<SearchTabOptions> searchAbTest, boolean addSubscribedStores,
      boolean hasMultipleFragments) {
    return hasMultipleFragments && (addSubscribedStores == (searchAbTest.alternative()
        == SearchTabOptions.FOLLOWED_STORES));
  }

  public String getQuery() {
    return query;
  }
}

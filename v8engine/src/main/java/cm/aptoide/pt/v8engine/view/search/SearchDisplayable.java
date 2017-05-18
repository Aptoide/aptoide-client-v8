/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/06/2016.
 */

package cm.aptoide.pt.v8engine.view.search;

import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.abtesting.ABTest;
import cm.aptoide.pt.v8engine.abtesting.SearchTabOptions;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;
import rx.functions.Action0;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchDisplayable extends DisplayablePojo<ListSearchApps.SearchAppsApp> {

  @Getter private Action0 clickCallback;

  public SearchDisplayable() {
  }

  public SearchDisplayable(ListSearchApps.SearchAppsApp searchAppsApp,
      ABTest<SearchTabOptions> searchAbTest, boolean addSubscribedStores,
      boolean hasMultipleFragments) {
    super(searchAppsApp);
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
}

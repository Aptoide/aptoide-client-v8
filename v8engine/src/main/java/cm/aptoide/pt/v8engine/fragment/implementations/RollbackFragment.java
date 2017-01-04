/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.RollbackAccessor;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.AptoideBaseFragment;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FooterRowDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RollbackDisplayable;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sithengineer on 14/06/16.
 */
public class RollbackFragment extends AptoideBaseFragment<BaseAdapter> {

  private static final String TAG = RollbackFragment.class.getSimpleName();
  private TextView emptyData;
  private Installer installManager;

  public RollbackFragment() {
  }

  public static Fragment newInstance() {
    return new RollbackFragment();
  }

  @Override public void setupToolbar() {
    super.setupToolbar();
    if (toolbar != null) {
      ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
      bar.setDisplayHomeAsUpEnabled(true);
      bar.setTitle(R.string.rollback);
    }
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_clear, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    } else if (itemId == R.id.menu_clear) {
      //DeprecatedDatabase.RollbackQ.deleteAll(realm);
      AccessorFactory.getAccessorFor(Rollback.class).removeAll();
      clearDisplayables();
      finishLoading();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    Logger.d(TAG, "refresh rollbacks? " + (create ? "yes" : "no"));
    AptoideUtils.ThreadU.runOnUiThread(this::fetchRollbacks);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_with_toolbar;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    emptyData = (TextView) view.findViewById(R.id.empty_data);
    setHasOptionsMenu(true);

    installManager = new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK);
  }

  @UiThread private void fetchRollbacks() {
    RollbackAccessor rollbackAccessor = AccessorFactory.getAccessorFor(Rollback.class);
    rollbackAccessor.getConfirmedRollbacks()
        .observeOn(Schedulers.computation())
        .map(rollbacks -> createDisplayables(rollbacks))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(rollbacks -> {
          if (rollbacks == null || rollbacks.isEmpty()) {
            emptyData.setText(AptoideUtils.StringU.getFormattedString(R.string.no_rollbacks_msg,
                Application.getConfiguration().getMarketName()));
            emptyData.setVisibility(View.VISIBLE);
          } else {
            emptyData.setVisibility(View.GONE);
            setDisplayables(rollbacks);
          }
          finishLoading();
        });
  }

  private List<Displayable> createDisplayables(List<Rollback> rollbacks) {
    List<Displayable> displayables = new LinkedList<>();
    long lastDay = 0;
    for (int i = 0; i < rollbacks.size(); i++) {
      Rollback rollback = rollbacks.get(i);
      long daysAgo = TimeUnit.MILLISECONDS.toDays(rollback.getTimestamp());
      if (lastDay != daysAgo) {
        lastDay = daysAgo;
        displayables.add(new FooterRowDisplayable(
            new SimpleDateFormat("dd-MM-yyyy", AptoideUtils.LocaleU.DEFAULT).format(
                rollback.getTimestamp())));
      }
      displayables.add(new RollbackDisplayable(installManager, rollback));
    }

    Calendar.getInstance(AptoideUtils.LocaleU.DEFAULT);
    return displayables;
  }
}

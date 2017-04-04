package cm.aptoide.pt.v8engine.view.updates.excluded;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.fragment.AptoideBaseFragment;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class ExcludedUpdatesFragment extends AptoideBaseFragment<BaseAdapter> {

  private static final String TAG = ExcludedUpdatesFragment.class.getSimpleName();
  private TextView emptyData;

  public ExcludedUpdatesFragment() {
  }

  public static ExcludedUpdatesFragment newInstance() {
    return new ExcludedUpdatesFragment();
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_with_toolbar;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    emptyData = (TextView) view.findViewById(R.id.empty_data);
    setHasOptionsMenu(true);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    Logger.d(TAG, "refresh excluded updates? " + (create ? "yes" : "no"));
    fetchExcludedUpdates();
  }

  private void fetchExcludedUpdates() {
    UpdateAccessor updateAccessor = AccessorFactory.getAccessorFor(Update.class);
    updateAccessor.getAll(true)
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(excludedUpdates -> {
          if (excludedUpdates == null || excludedUpdates.isEmpty()) {
            emptyData.setText(R.string.no_excluded_updates_msg);
            emptyData.setVisibility(View.VISIBLE);
            clearDisplayables();
            finishLoading();
          } else {
            emptyData.setVisibility(View.GONE);
            List<ExcludedUpdateDisplayable> displayables = new ArrayList<>();
            for (Update excludedUpdate : excludedUpdates) {
              displayables.add(new ExcludedUpdateDisplayable(excludedUpdate));
            }
            clearDisplayables().addDisplayables(displayables, true);
          }
        }, t -> {
          CrashReport.getInstance().log(t);
          emptyData.setText(R.string.no_excluded_updates_msg);
          emptyData.setVisibility(View.VISIBLE);
          clearDisplayables();
          finishLoading();
        });
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(R.string.excluded_updates);
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_excluded_updates_fragment, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }

    if (itemId == R.id.menu_restore_updates) {
      // get all selected ExcludedUpdates and restore them in the updates
      LinkedList<Update> excludedUpdatesToRestore = new LinkedList<>();
      BaseAdapter adapter = getAdapter();
      for (int i = 0; i < adapter.getItemCount(); ++i) {
        ExcludedUpdateDisplayable displayable =
            ((ExcludedUpdateDisplayable) adapter.getDisplayable(i));
        if (displayable.isSelected()) {
          excludedUpdatesToRestore.add(displayable.getPojo());
        }
      }

      if (excludedUpdatesToRestore.size() == 0) {
        ShowMessage.asSnack(emptyData, R.string.no_excluded_updates_selected);
        return true;
      }

      // restore updates and remove them from excluded
      //@Cleanup Realm realm = DeprecatedDatabase.get();
      //realm.beginTransaction();
      //for (Update e : excludedUpdatesToRestore) {
      //  e.setExcluded(false);
      //}
      //realm.copyToRealmOrUpdate(excludedUpdatesToRestore);
      //realm.commitTransaction();

      UpdateAccessor updateAccessor = AccessorFactory.getAccessorFor(Update.class);
      Observable.from(excludedUpdatesToRestore)
          .doOnNext(update -> update.setExcluded(false))
          .toList()
          .subscribe(updates -> updateAccessor.insertAll(updates), err -> {
            CrashReport.getInstance().log(err);
          });

      return true;
    }

    if (itemId == R.id.menu_select_all) {
      BaseAdapter adapter = getAdapter();
      for (int i = 0; i < adapter.getItemCount(); ++i) {
        ((ExcludedUpdateDisplayable) adapter.getDisplayable(i)).setSelected(true);
        adapter.notifyDataSetChanged();
      }
      return true;
    }

    if (itemId == R.id.menu_select_none) {
      BaseAdapter adapter = getAdapter();
      for (int i = 0; i < adapter.getItemCount(); ++i) {
        ((ExcludedUpdateDisplayable) adapter.getDisplayable(i)).setSelected(false);
        adapter.notifyDataSetChanged();
      }
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}

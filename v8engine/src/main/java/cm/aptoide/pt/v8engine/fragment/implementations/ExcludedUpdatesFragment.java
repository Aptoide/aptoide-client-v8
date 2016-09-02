/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ExcludedUpdateDisplayable;
import io.realm.Realm;
import lombok.Cleanup;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 21/06/16.
 */
public class ExcludedUpdatesFragment extends GridRecyclerFragment {

	private static final String TAG = ExcludedUpdatesFragment.class.getSimpleName();
	private TextView emptyData;
	private Subscription subscription;

	public ExcludedUpdatesFragment() {
	}

	public static ExcludedUpdatesFragment newInstance() {
		return new ExcludedUpdatesFragment();
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		Logger.d(TAG, "refresh excluded updates? " + (refresh ? "yes" : "no"));
		fetchExcludedUpdates();
	}

	@Override
	public int getContentViewId() {
		return R.layout.fragment_with_toolbar;
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		emptyData = (TextView) view.findViewById(R.id.empty_data);
		setHasOptionsMenu(true);
	}

	@Override
	public void setupToolbar() {
		super.setupToolbar();
		if (toolbar != null) {
			ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle(R.string.excluded_updates);
		}
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_excluded_updates_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if (itemId == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}

		if (itemId == R.id.menu_restore_updates) {
			// get all selected ExcludedUpdates and restore them in the updates
			LinkedList<Update> excludedUpdatesToRestore = new LinkedList<>();
			BaseAdapter adapter = getAdapter();
			for (int i = 0 ; i < adapter.getItemCount() ; ++i) {
				ExcludedUpdateDisplayable displayable = ((ExcludedUpdateDisplayable) adapter.getDisplayable(i));
				if (displayable.isSelected()) {
					excludedUpdatesToRestore.add(displayable.getPojo());
				}
			}

			if (excludedUpdatesToRestore.size() == 0) {
				ShowMessage.asSnack(emptyData, R.string.no_excluded_updates_selected);
				return true;
			}

			// restore updates and remove them from excluded
			@Cleanup Realm realm = DeprecatedDatabase.get();
			realm.beginTransaction();
			for (Update e : excludedUpdatesToRestore) {
				e.setExcluded(false);
			}
			realm.copyToRealmOrUpdate(excludedUpdatesToRestore);
			realm.commitTransaction();

			return true;
		}

		if (itemId == R.id.menu_select_all) {
			BaseAdapter adapter = getAdapter();
			for (int i = 0 ; i < adapter.getItemCount() ; ++i) {
				((ExcludedUpdateDisplayable) adapter.getDisplayable(i)).setSelected(true);
				adapter.notifyDataSetChanged();
			}
			return true;
		}

		if (itemId == R.id.menu_select_none) {
			BaseAdapter adapter = getAdapter();
			for (int i = 0 ; i < adapter.getItemCount() ; ++i) {
				((ExcludedUpdateDisplayable) adapter.getDisplayable(i)).setSelected(false);
				adapter.notifyDataSetChanged();
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void fetchExcludedUpdates() {
		subscription = DeprecatedDatabase.UpdatesQ.getAll(realm, true)
				.asObservable()
				.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.observeOn(AndroidSchedulers.mainThread())
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
						setDisplayables(displayables);
					}
				}, t -> {
					Logger.e(TAG, t);
					emptyData.setText(R.string.no_excluded_updates_msg);
					emptyData.setVisibility(View.VISIBLE);
					clearDisplayables();
					finishLoading();
				});
	}
}

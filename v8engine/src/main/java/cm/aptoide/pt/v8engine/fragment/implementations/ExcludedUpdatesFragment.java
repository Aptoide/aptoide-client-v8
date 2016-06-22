/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 22/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.ExcludedUpdate;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ExcludedUpdateDisplayable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 21/06/16.
 */
public class ExcludedUpdatesFragment extends GridRecyclerSwipeFragment {

	private Subscription subscription;

	public static ExcludedUpdatesFragment newInstance() {
		return new ExcludedUpdatesFragment();
	}

	@Override
	public void load(boolean refresh) {
		fetchExcludedUpdates();
	}

	@Override
	public int getContentViewId() {
		return R.layout.fragment_excluded_updates;
	}

	@Override
	public void setupToolbar() {
		super.setupToolbar();
		if (toolbar != null) {
			((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	// FIXME on home selected, go home

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_empty, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int i = item.getItemId();

		if (i == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void fetchExcludedUpdates() {
		subscription = Database.ExcludedUpdatesQ.getAll(realm)
				.asObservable()
				.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(excludedUpdates -> {
					// TODO
					List<ExcludedUpdateDisplayable> displayables = new ArrayList<>();
					for (final ExcludedUpdate excludedUpdate : excludedUpdates) {
						displayables.add(new ExcludedUpdateDisplayable(excludedUpdate));
					}
					setDisplayables(displayables);
				});
	}
}

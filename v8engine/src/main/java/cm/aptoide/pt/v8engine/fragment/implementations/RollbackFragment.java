/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 23/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RollbackDisplayable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 14/06/16.
 */
public class RollbackFragment extends GridRecyclerSwipeFragment {

	private static final String TAG = RollbackFragment.class.getSimpleName();
	private Subscription rollbackSubscription;
	private List<Displayable> rollbackDisplayables;

	public RollbackFragment() {
		rollbackDisplayables = new ArrayList<>();
	}

	public static Fragment newInstance() {
		return new RollbackFragment();
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_clear, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if (itemId == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		} else if (itemId == R.id.menu_clear) {
			ShowMessage.asSnack(this, "TO DO: clear");
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void load(boolean refresh) {
		Logger.d(TAG, String.format("refresh rollbacks? %s", refresh ? "yes" : "no"));
		fetchRollbacks();
	}

	private void fetchRollbacks() {
		rollbackSubscription = Database.RollbackQ.getAll(realm)
				.asObservable()
				.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(rollbacks -> {
					rollbackDisplayables.clear();
					for (final Rollback rollback : rollbacks) {
						rollbackDisplayables.add(new RollbackDisplayable(rollback));
					}

					setDisplayables(rollbackDisplayables);

					finishLoading();
				});
	}
}

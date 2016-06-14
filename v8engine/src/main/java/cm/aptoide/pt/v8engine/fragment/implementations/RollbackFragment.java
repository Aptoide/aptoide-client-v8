/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 14/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.support.v4.app.Fragment;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RollbackDisplayable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 14/06/16.
 */
public class RollbackFragment extends GridRecyclerSwipeFragment {

	public static Fragment newInstance() {
		return new RollbackFragment();
	}

	private Subscription rollbackSubscription;
	private List<Displayable> rollbackDisplayables;

	public RollbackFragment() {
		rollbackDisplayables = new ArrayList<>();
	}

	@Override
	public void load(boolean refresh) {
		if(rollbackSubscription==null) {
			rollbackSubscription = Database.RollbackQ.getAll(realm)
					.asObservable()
					.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe( rollbacks -> {
						rollbackDisplayables.clear();
						for (final Rollback rollback : rollbacks) {
							rollbackDisplayables.add(new RollbackDisplayable(rollback));
						}

						setDisplayables(rollbackDisplayables);

						finishLoading();
					});
		}
	}
}

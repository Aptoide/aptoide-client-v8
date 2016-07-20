/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle.FragmentEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.HeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RollbackDisplayable;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 14/06/16.
 */
public class RollbackFragment extends GridRecyclerFragment {

	private static final String TAG = RollbackFragment.class.getSimpleName();
	private static final DateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
	private TextView emptyData;
	private Subscription subscription;

	public RollbackFragment() {
	}

	public static Fragment newInstance() {
		return new RollbackFragment();
	}

	@Override
	public void setupToolbar() {
		super.setupToolbar();
		if (toolbar != null) {
			ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle(R.string.rollback);
		}
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
	public void load(boolean refresh, Bundle savedInstanceState) {
		Logger.d(TAG, String.format("refresh rollbacks? %s", refresh ? "yes" : "no"));
		fetchRollbacks();
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

	private void fetchRollbacks() {
		subscription = Database.RollbackQ.getAll(realm).sort(Rollback.TIMESTAMP, Sort.ASCENDING)
				.asObservable()
				.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(rollbacks -> {

					if (rollbacks == null || rollbacks.isEmpty()) {

						emptyData.setText(R.string.no_rollbacks_msg);
						emptyData.setVisibility(View.VISIBLE);
					} else {

						emptyData.setVisibility(View.GONE);
						sortRollbacksAndAdd(rollbacks);
					}

					finishLoading();
				});
	}

	private String timestampAsStringFromLong(long timestamp) {
		return FORMAT.format(new Date(timestamp));
	}

	private long timestampAsLongFromString(String timestamp) {
		try {
			return FORMAT.parse(timestamp).getTime();
		} catch (ParseException ex) {
			Logger.e(TAG, "", ex);
			throw new RuntimeException(ex);
		}
	}

	// FIXME slow method. could this be improved ??
	private void sortRollbacksAndAdd(RealmResults<Rollback> rollbacks) {
		// group by timestamp
		TreeMap<String,List<Displayable>> arrayOfDisplayables = new TreeMap<>(new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				long lhsDate = timestampAsLongFromString(lhs);
				long rhsDate = timestampAsLongFromString(rhs);
				return ((int) (lhsDate - rhsDate));
			}
		});

		String timestampAsString = null;
		List<Displayable> displayables = null;
		for (Rollback rollback : rollbacks) {
			timestampAsString = timestampAsStringFromLong(rollback.getTimestamp());
			displayables = arrayOfDisplayables.get(timestampAsString);
			if (displayables == null) {
				displayables = new LinkedList<>();
				arrayOfDisplayables.put(timestampAsString, displayables);
			}
			displayables.add(new RollbackDisplayable(rollback));
		}

		// display headers and content
		List<Displayable> displayablesToShow = new LinkedList<>();
		for (Map.Entry<String,List<Displayable>> arrayOfDisplayablesEntry : arrayOfDisplayables.entrySet()) {
			displayablesToShow.add(new HeaderDisplayable(arrayOfDisplayablesEntry.getKey()));
			displayablesToShow.addAll(arrayOfDisplayablesEntry.getValue());
		}
		setDisplayables(displayablesToShow);
	}
}

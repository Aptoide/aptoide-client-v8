/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 13/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.InstalledAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdateDisplayable;
import io.realm.RealmResults;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 16-05-2016.
 */
public class UpdatesFragment extends GridRecyclerSwipeFragment {

	private List<Displayable> updatesDisplayablesList = new LinkedList<>();
	private List<Displayable> installedDisplayablesList = new LinkedList<>();
	private Subscription installedSubscription;
	private Subscription updatesSubscription;

	public static UpdatesFragment newInstance() {
		UpdatesFragment fragment = new UpdatesFragment();
		return fragment;
	}

	@Override
	public void load(boolean refresh) {
		fetchUpdates();
		fetchInstalled();
	}

	@Override
	public void reload() {
		super.reload();
		DataproviderUtils.checkUpdates(listAppsUpdates -> {
			if (listAppsUpdates.getList().size() == 0) {
				finishLoading();
				ShowMessage.asSnack(getView(), R.string.no_updates_available_retoric);
			}
			if (listAppsUpdates.getList().size() == updatesDisplayablesList.size() - 1) {
				ShowMessage.asSnack(getView(), R.string.no_new_updates_available);
			}
		});
	}

	private void fetchUpdates() {
		if (updatesSubscription == null || updatesSubscription.isUnsubscribed()) {
			updatesSubscription = Database.UpdatesQ.getAll(realm, false)
					.asObservable()
					.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(updates -> {

						if (updates.size() == updatesDisplayablesList.size() - 1) {
							finishLoading();
						} else {
							updatesDisplayablesList.clear();

							if (updates.size() > 0) {
								updatesDisplayablesList.add(new GridHeaderDisplayable(new GetStoreWidgets.WSWidget().setTitle(AptoideUtils.StringU
										.getResString(R.string.updates))));

								for (Update update : updates) {
									updatesDisplayablesList.add(UpdateDisplayable.create(update));
								}
							}

							setDisplayables();
						}
					});
		}
	}

	private void fetchInstalled() {
		if (installedSubscription == null || installedSubscription.isUnsubscribed()) {
			RealmResults<Installed> realmResults = Database.InstalledQ.getAll(realm);
			installedSubscription = realmResults.asObservable()
					.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
					.subscribe(installeds -> {
						installedDisplayablesList.clear();

						installedDisplayablesList.add(new GridHeaderDisplayable(new GetStoreWidgets.WSWidget().setTitle(AptoideUtils.StringU
								.getResString(R.string.installed_tab))));

						RealmResults<Installed> all = realmResults;
						for (int i = all.size() - 1; i >= 0; i--) {
							if (!Database.UpdatesQ.contains(all.get(i).getPackageName(), realm)) {
								installedDisplayablesList.add(new InstalledAppDisplayable(all.get(i)));
							}
						}

						setDisplayables();
					});
			if (realmResults.size() == 0) {
				finishLoading();
			}
			finishLoading();
		}
	}

	private void setDisplayables() {
		LinkedList<Displayable> displayables = new LinkedList<>();
		displayables.addAll(updatesDisplayablesList);
		displayables.addAll(installedDisplayablesList);
		setDisplayables(displayables);
	}
}

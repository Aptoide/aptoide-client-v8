/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cm.aptoide.pt.dataprovider.util.ObservableUtils;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.dynamicget.WSWidgetsUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.store.GetStoreTabs;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablesFactory;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 29-04-2016.
 */
public class StoreTabGridRecyclerFragment extends GridRecyclerSwipeFragment {

	private GetStoreTabs.Tab.Event.Type type;
	private GetStoreTabs.Tab.Event.Name name;
	private String action;
	private List<Displayable> displayables;

	public static StoreTabGridRecyclerFragment newInstance(GetStoreTabs.Tab.Event event) {
		Bundle args = new Bundle();

		args.putString(BundleCons.TYPE, event.getType().toString());
		args.putString(BundleCons.NAME, event.getName().toString());
		// todo: apagar martelada!
		if (event.getAction() != null && event.getAction().contains("getStore")) {
			event.setAction(event.getAction().replace("context/store", "context/home"));
		}
		args.putString(BundleCons.ACTION, event.getAction());

		StoreTabGridRecyclerFragment fragment = new StoreTabGridRecyclerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected void loadBundle(Bundle args) {
		type = GetStoreTabs.Tab.Event.Type.valueOf(args.getString(BundleCons.TYPE));
		name = GetStoreTabs.Tab.Event.Name.valueOf(args.getString(BundleCons.NAME));
		action = args.getString(BundleCons.ACTION);
	}

	@Override
	public void load(boolean refresh) {
		if (refresh) {
			String url = action != null ? action.replace(V7.BASE_HOST, "") : null;

			switch (name) {
				case getStore:
					caseGetStore(url);
					break;
				case getStoreWidgets:
					caseGetStoreWidgets(url);
					break;
				case getReviews:
					//todo
					break;
				case getApkComments:
					//todo
					break;
			}
		} else {
			setDisplayables(displayables);
		}
	}

	private Subscription caseGetStore(String url) {
		return ObservableUtils.retryOnTicket(GetStoreRequest.ofAction(url).observe())
				.observeOn(Schedulers.io())
				.subscribe(getStore -> {

					// Load sub nodes
					List<GetStoreWidgets.WSWidget> list = getStore.getNodes()
							.getWidgets()
							.getDatalist()
							.getList();
					CountDownLatch countDownLatch = new CountDownLatch(list.size());

					Observable.from(list)
							.forEach(wsWidget -> WSWidgetsUtils.loadInnerNodes(wsWidget,
									countDownLatch, throwable -> finishLoading(throwable)));

					try {
						countDownLatch.await(5, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					displayables = DisplayablesFactory.parse(getStore.getNodes()
							.getWidgets());
					setDisplayables(displayables);
				}, throwable -> finishLoading(throwable));
	}

	private Subscription caseGetStoreWidgets(String url) {
		return ObservableUtils.retryOnTicket(GetStoreWidgetsRequest.ofAction(url).observe())
				.observeOn(Schedulers.io())
				.subscribe(getStoreWidgets -> {

					// Load sub nodes
					List<GetStoreWidgets.WSWidget> list = getStoreWidgets.getDatalist().getList();
					CountDownLatch countDownLatch = new CountDownLatch(list.size());

					Observable.from(list)
							.forEach(wsWidget -> WSWidgetsUtils.loadInnerNodes(wsWidget,
									countDownLatch, throwable -> finishLoading(throwable)));

					try {
						countDownLatch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					displayables = DisplayablesFactory.parse(getStoreWidgets);
					setDisplayables(displayables);
				}, throwable -> finishLoading(throwable));
	}

	@Override
	protected void setupToolbar() {

	}

	private static class BundleCons {

		public static final String TYPE = "type";
		public static final String NAME = "name";
		public static final String ACTION = "action";
	}
}

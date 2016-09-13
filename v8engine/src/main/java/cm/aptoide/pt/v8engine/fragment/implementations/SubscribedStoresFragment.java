/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.LinkedList;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.dialog.AddStoreDialog;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SubscribedStoreDisplayable;
import io.realm.RealmResults;
import rx.Observable;

/**
 * Created by neuro on 11-05-2016.
 */
public class SubscribedStoresFragment extends GridRecyclerFragmentWithDecorator {

	private FloatingActionButton addStoreButton;

	public static SubscribedStoresFragment newInstance() {
		SubscribedStoresFragment fragment = new SubscribedStoresFragment();
		return fragment;
	}

	@Override
	public void setupViews() {
		super.setupViews();

		addStoreButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				new AddStoreDialog().show(((FragmentActivity) getContext())
						.getSupportFragmentManager(), "addStoreDialog");
			}
		});
		/*RxView.clicks(addStoreButton).subscribe(view ->{
			new AddStoreDialog().show(((FragmentActivity) getContext())
					.getSupportFragmentManager(), "addStoreDialog");
		});*/

		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy){
				/*Log.d("lou",dy+"");
				if (dy > 0 && addStoreButton.getTranslationY() > 0 && addStoreButton.isShown()) {
					addStoreButton.setTranslationY(addStoreButton.getTranslationY()+dy);
				}*/
			}
		});

	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {

		Observable<RealmResults<Store>> realmResultsObservable = DeprecatedDatabase.StoreQ.getAll(realm).asObservable();

		realmResultsObservable.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.subscribe(stores -> {

					LinkedList<Displayable> displayables = new LinkedList<>();

					for (Store store : stores) {
						displayables.add(new SubscribedStoreDisplayable(store));
					}

					// Add the final row as a button
					//displayables.add(new AddMoreStoresDisplayable());

					setDisplayables(displayables);
				});
	}

	@Override
	public int getContentViewId() {
		return R.layout.store_recycler_fragment;
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		addStoreButton = (FloatingActionButton) view.findViewById(R.id.fabAddStore);
	}
}

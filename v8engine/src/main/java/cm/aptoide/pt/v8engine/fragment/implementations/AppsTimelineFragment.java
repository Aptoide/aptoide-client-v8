/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 18/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;

import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.repository.TimelineCardFilter;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.dataprovider.util.ErrorUtils;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.Datalist;
import cm.aptoide.pt.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.model.v7.timeline.Recommendation;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.model.v7.timeline.Video;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.install.provider.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.repository.PackageRepository;
import cm.aptoide.pt.v8engine.repository.TimelineRepository;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.ProgressBarDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.RecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FeatureDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.VideoDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.RxEndlessRecyclerView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class AppsTimelineFragment extends GridRecyclerSwipeFragment {

	public static final int SEARCH_LIMIT = 20;
	private static final String ACTION_KEY = "ACTION";
	private static final String PACKAGE_LIST_KEY = "PACKAGE_LIST";
	private DownloadServiceHelper downloadManager;
	private DownloadFactory downloadFactory;
	private SpannableFactory spannableFactory;
	private LinksHandlerFactory linksHandlerFactory;
	private DateCalculator dateCalculator;
	private boolean loading;
	private int offset;
	private int total;
	private Subscription subscription;
	private TimelineRepository timelineRepository;
	private PackageRepository packageRepository;
	private List<String> packages;
	private InstallManager installManager;

	public static AppsTimelineFragment newInstance(String action) {
		AppsTimelineFragment fragment = new AppsTimelineFragment();

		final Bundle args = new Bundle();
		args.putString(ACTION_KEY, action);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dateCalculator = new DateCalculator();
		spannableFactory = new SpannableFactory();
		downloadFactory = new DownloadFactory();
		linksHandlerFactory = new LinksHandlerFactory();
		packageRepository = new PackageRepository(getContext().getPackageManager());
		final PermissionManager permissionManager = new PermissionManager();
		downloadManager = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), permissionManager);
		installManager = new InstallManager(permissionManager, getContext().getPackageManager(), new DownloadInstallationProvider(downloadManager));
		timelineRepository = new TimelineRepository(getArguments().getString(ACTION_KEY),
				new TimelineCardFilter(new TimelineCardFilter.TimelineCardDuplicateFilter(new HashSet<>()),
						AccessorFactory.getAccessorFor(Installed.class)));
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		super.load(refresh, savedInstanceState);

		if (subscription != null) {
			subscription.unsubscribe();
		}

		final Observable<List<String>> packagesObservable;
		final Observable<Datalist<Displayable>> displayableObservable;
		if (refresh) {
			if (savedInstanceState != null && savedInstanceState.getStringArray(PACKAGE_LIST_KEY) != null) {
				packages = Arrays.asList(savedInstanceState.getStringArray(PACKAGE_LIST_KEY));
				packagesObservable = Observable.just(packages);
			} else {
				packagesObservable = refreshPackages();
			}
			displayableObservable = packagesObservable.flatMap(packages ->  Observable.concat(getFreshDisplayables(refresh, packages), getNextDisplayables(packages)));
		} else {

			if (packages != null) {
				packagesObservable = Observable.just(packages);
			} else {
				packagesObservable = refreshPackages();
			}

			if (adapter.getItemCount() == 0) {
				displayableObservable = packagesObservable.flatMap(packages ->  Observable.concat(getFreshDisplayables(refresh, packages), getNextDisplayables(packages)));
			} else {
				displayableObservable = packagesObservable.flatMap(packages ->  getNextDisplayables(packages));
			}
		}

		subscription = displayableObservable
				.<Datalist<Displayable>> compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(items -> addItems(items), throwable -> finishLoading((Throwable) throwable));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (packages != null) {
			outState.putStringArray(PACKAGE_LIST_KEY, packages.toArray(new String[packages.size()]));
		}
		super.onSaveInstanceState(outState);
	}

	@NonNull
	private Observable<List<String>> refreshPackages() {
		return Observable.concat(packageRepository.getLatestInstalledPackages(10), packageRepository.getRandomInstalledPackages(10))
				.toList()
				.doOnNext(packages -> setPackages(packages));
	}

	private void setPackages(List<String> packages) {
		this.packages = packages;
	}

	@NonNull
	private Observable<Datalist<Displayable>> getFreshDisplayables(boolean refresh, List<String> packages) {
		return getDisplayableList(packages, 0, refresh)
				.doOnNext(item -> getAdapter().clearDisplayables())
				.doOnUnsubscribe(() -> finishLoading());
	}

	private Observable<Datalist<Displayable>> getNextDisplayables(List<String> packages) {
		return RxEndlessRecyclerView.loadMore(recyclerView, getAdapter())
				.filter(item -> onStartLoadNext())
				.concatMap(item -> getDisplayableList(packages, getOffset(), false))
				.delay(1, TimeUnit.SECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.retryWhen(errors -> errors
						.delay(1, TimeUnit.SECONDS)
						.observeOn(AndroidSchedulers.mainThread())
						.filter(error -> onStopLoadNext(error)))
				.doOnUnsubscribe(() -> removeLoading())
				.subscribeOn(AndroidSchedulers.mainThread());
	}


	@NonNull
	private Observable<Datalist<Displayable>> getDisplayableList(List<String> packages, int offset, boolean refresh) {
		return timelineRepository.getTimelineCards(SEARCH_LIMIT, offset, packages, refresh)
				.flatMap(datalist -> Observable.just(datalist).flatMapIterable(dataList -> dataList.getList())
						.map(card -> cardToDisplayable(card, dateCalculator, spannableFactory, downloadFactory,
								downloadManager, linksHandlerFactory))
						.toList().map(list -> createDisplayableDataList(datalist, list)));
	}

	private Datalist<Displayable> createDisplayableDataList(Datalist<TimelineCard> datalist, List<Displayable> list) {
		Datalist<Displayable> displayableDataList = new Datalist<>();
		displayableDataList.setNext(datalist.getNext());
		displayableDataList.setCount(datalist.getCount());
		displayableDataList.setHidden(datalist.getHidden());
		displayableDataList.setTotal(datalist.getTotal());
		displayableDataList.setLimit(datalist.getLimit());
		displayableDataList.setOffset(datalist.getOffset());
		displayableDataList.setLoaded(datalist.isLoaded());
		displayableDataList.setList(list);
		return displayableDataList;
	}

	private void showErrorSnackbar(Throwable error) {
		@StringRes int errorString;
		if (ErrorUtils.isNoNetworkConnection(error)) {
			errorString = R.string.fragment_social_timeline_no_connection;
		} else {
			errorString = R.string.fragment_social_timeline_general_error;
		}
		Snackbar.make(getView(), errorString, Snackbar.LENGTH_SHORT).show();
	}

	private boolean isTotal() {
		return offset >= total;
	}

	public void setTotal(Datalist<Displayable> dataList) {
		if (dataList != null && dataList.getTotal() != 0) {
			total = dataList.getTotal();
		}
	}

	private int getOffset() {
		return offset;
	}

	private void setOffset(Datalist<Displayable> dataList) {
		if (dataList != null && dataList.getNext() != 0) {
			offset = dataList.getNext();
		}
	}

	private void addLoading() {
		if (!loading) {
			this.loading = true;
			adapter.addDisplayable(new ProgressBarDisplayable(true));
		}
	}

	private void removeLoading() {
		if (loading) {
			loading = false;
			adapter.popDisplayable();
		}
	}

	private boolean isLoading() {
		return loading;
	}

	private void addItems(Datalist<Displayable> data) {
		removeLoading();
		addDisplayables(data.getList());
		setTotal(data);
		setOffset(data);
	}

	@NonNull
	private boolean onStopLoadNext(Throwable error) {
		if (isLoading()) {
			showErrorSnackbar(error);
			removeLoading();
			return true;
		}
		return false;
	}

	@NonNull
	private boolean onStartLoadNext() {
		if (!isTotal() && !isLoading()) {
			addLoading();
			return true;
		}
		else if(isTotal()){
			//TODO - When you reach the end of the endless?
			// Snackbar.make(getView(), "No more cards!", Snackbar.LENGTH_SHORT).show();
		}
		return false;
	}

	@NonNull
	private Displayable cardToDisplayable(TimelineCard card, DateCalculator dateCalculator, SpannableFactory spannableFactory, DownloadFactory downloadFactory,
			DownloadServiceHelper downloadManager, LinksHandlerFactory linksHandlerFactory) {
		if (card instanceof Article) {
			return ArticleDisplayable.from((Article) card, dateCalculator, spannableFactory,
					linksHandlerFactory);
		} else  if (card instanceof Video) {
			return VideoDisplayable.from((Video) card, dateCalculator, spannableFactory,
					linksHandlerFactory);
		} else if (card instanceof Feature) {
			return FeatureDisplayable.from((Feature) card, dateCalculator, spannableFactory);
		} else if (card instanceof StoreLatestApps) {
			return StoreLatestAppsDisplayable.from((StoreLatestApps) card, dateCalculator);
		} else if (card instanceof AppUpdate) {
			return AppUpdateDisplayable.from((AppUpdate) card, spannableFactory, downloadFactory, downloadManager, installManager, dateCalculator);
		} else if (card instanceof Recommendation) {
			return RecommendationDisplayable.from((Recommendation) card, dateCalculator, spannableFactory);
		}
		throw new IllegalArgumentException("Only articles, features, store latest apps, app updates and videos supported.");
	}

}

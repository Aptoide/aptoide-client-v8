/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cm.aptoide.pt.dataprovider.PackageRepository;
import cm.aptoide.pt.dataprovider.TimelineRepository;
import cm.aptoide.pt.dataprovider.util.ErrorUtils;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.model.v7.Datalist;
import cm.aptoide.pt.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.ProgressBarDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FeatureDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.RxEndlessRecyclerView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class AppsTimelineFragment extends GridRecyclerSwipeFragment {

	public static final int SEARCH_LIMIT = 7;
	private static final String ACTION_KEY = "ACTION";
	private AptoideDownloadManager downloadManager;
	private DownloadFactory downloadFactory;
	private SpannableFactory spannableFactory;
	private DateCalculator dateCalculator;
	private boolean loading;
	private int offset;
	private Subscription subscription;
	private TimelineRepository timelineRepository;
	private PackageRepository packageRepository;
	private List<String> packages;

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
		downloadManager = AptoideDownloadManager.getInstance();
		packageRepository = new PackageRepository(getContext().getPackageManager());
		timelineRepository = new TimelineRepository(getArguments().getString(ACTION_KEY), new TimelineRepository.TimelineCardDuplicateFilter(new HashSet<>()));
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getAdapter().getItemCount() > 0 && (subscription == null || subscription.isUnsubscribed()) && packages != null) {
			subscription = getNextDisplayables(packages)
					.<List<Displayable>>compose(bindUntilEvent(FragmentEvent.PAUSE))
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(displayables -> addDisplayables(displayables), throwable -> throwable.printStackTrace());
		} else {
			load(false);
		}
	}

	@Override
	public void load(boolean refresh) {
		if (subscription != null) {
			subscription.unsubscribe();
		}
		subscription = getPackages().flatMap(packages -> Observable.concat(getFreshDisplayables(refresh, packages), getNextDisplayables(packages)))
				.<List<Displayable>>compose(bindUntilEvent(FragmentEvent.PAUSE))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(displayables -> addDisplayables(displayables), throwable -> finishLoading((Throwable) throwable));
	}

	@NonNull
	private Observable<List<String>> getPackages() {
		return Observable.concat(packageRepository.getLatestInstalledPackages(5), packageRepository.getRandomInstalledPackages(5))
				.toList()
				.doOnNext(packages -> setPackages(packages));
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	@NonNull
	private Observable<List<Displayable>> getFreshDisplayables(boolean refresh, List<String> packages) {
		return getDisplayableList(packages, 0, refresh)
				.doOnNext(item -> getAdapter().clearDisplayables())
				.doOnNext(item -> finishLoading())
				.doOnUnsubscribe(() -> finishLoading());
	}

	private Observable<List<Displayable>> getNextDisplayables(List<String> packages) {
		return RxEndlessRecyclerView.loadMore(recyclerView, getAdapter())
				.filter(item -> !isLoading())
				.doOnNext(item -> addLoading())
				.concatMap(item -> getDisplayableList(packages, offset, false))
				.delay(1, TimeUnit.SECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(item -> removeLoading())
				.retryWhen(errors -> errors
						.delay(1, TimeUnit.SECONDS)
						.observeOn(AndroidSchedulers.mainThread())
						.filter(item -> isLoading())
						.doOnNext(error -> showErrorSnackbar(error)))
				.doOnUnsubscribe(() -> removeLoading())
				.subscribeOn(AndroidSchedulers.mainThread());
	}

	@NonNull
	private Observable<List<Displayable>> getDisplayableList(List<String> packages, int offset, boolean refresh) {
		return timelineRepository.getTimelineCards(SEARCH_LIMIT, offset, packages, refresh)
				.doOnNext(datalist -> setOffset(datalist))
				.flatMapIterable(datalist -> datalist.getList())
				.map(card -> cardToDisplayable(card, dateCalculator, spannableFactory, downloadFactory, downloadManager))
				.toList();
	}

	private void showErrorSnackbar(Throwable error) {
		removeLoading();
		@StringRes int errorString;
		if (ErrorUtils.isNoNetworkConnection(error)) {
			errorString = R.string.fragment_social_timeline_no_connection;
		} else {
			errorString = R.string.fragment_social_timeline_general_error;
		}
		Snackbar.make(getView(), errorString, Snackbar.LENGTH_SHORT).show();
	}

	private void setOffset(Datalist<TimelineCard> datalist) {
		if (datalist != null && datalist.getNext() != 0) {
			offset = datalist.getNext();
		}
	}

	private void addLoading() {
		this.loading = true;
		adapter.addDisplayable(new ProgressBarDisplayable());
	}

	private void removeLoading() {
		if (loading) {
			this.loading = false;
			adapter.popDisplayable();
		}
	}

	private boolean isLoading() {
		return loading;
	}

	@NonNull
	private Displayable cardToDisplayable(TimelineCard card, DateCalculator dateCalculator, SpannableFactory spannableFactory, DownloadFactory downloadFactory,
	                                      AptoideDownloadManager downloadManager) {
		if (card instanceof Article) {
			return ArticleDisplayable.from((Article) card, dateCalculator, spannableFactory);
		} else if (card instanceof Feature) {
			return FeatureDisplayable.from((Feature) card, dateCalculator, spannableFactory);
		} else if (card instanceof StoreLatestApps) {
			return StoreLatestAppsDisplayable.from((StoreLatestApps) card, dateCalculator);
		} else if (card instanceof AppUpdate) {
			return AppUpdateDisplayable.from((AppUpdate) card, spannableFactory, downloadFactory, downloadManager);
		}
		throw new IllegalArgumentException("Only articles, features, store latest apps and app updates supported.");
	}

}

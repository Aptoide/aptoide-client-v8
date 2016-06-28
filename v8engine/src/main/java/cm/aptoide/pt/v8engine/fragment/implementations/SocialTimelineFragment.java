package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cm.aptoide.pt.dataprovider.util.ErrorUtils;
import cm.aptoide.pt.dataprovider.ws.v7.GetUserTimelineRequest;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
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
public class SocialTimelineFragment extends GridRecyclerSwipeFragment {

	public static final int SEARCH_LIMIT = 4;
	private SpannableFactory spannableFactory;
	private DateCalculator dateCalculator;
	private boolean loading;
	private int offset;
	private Subscription subscription;

	public static SocialTimelineFragment newInstance() {
		SocialTimelineFragment fragment = new SocialTimelineFragment();
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dateCalculator = new DateCalculator();
		spannableFactory = new SpannableFactory();
	}

	@Override
	public void load(boolean refresh) {
		if (subscription != null) {
			subscription.unsubscribe();
		}
		subscription = Observable.concat(
				GetUserTimelineRequest.of(SEARCH_LIMIT, 0).observe(refresh)
					.observeOn(AndroidSchedulers.mainThread())
					.doOnNext(item -> adapter.clearDisplayables()),
				RxEndlessRecyclerView.loadMore(recyclerView, getAdapter())
						.filter(item -> !isLoading())
						.doOnNext(item -> addLoading())
						.concatMap(item -> GetUserTimelineRequest.of(SEARCH_LIMIT, offset).observe())
						.delay(1, TimeUnit.SECONDS)
						.retryWhen(errors -> errors.delay(1, TimeUnit.SECONDS)
								.observeOn(AndroidSchedulers.mainThread())
								.doOnNext(error -> showErrorSnackbar(error)))
						.subscribeOn(AndroidSchedulers.mainThread()))
				.<GetUserTimeline> compose(bindUntilEvent(FragmentEvent.PAUSE))
				.filter(item -> item.getDatalist() != null)
				.doOnNext(item -> setOffset(item))
				.flatMapIterable(getUserTimeline -> getUserTimeline.getDatalist().getList())
				.filter(timelineItem -> timelineItem != null)
				.map(timelineItem -> timelineItem.getData())
				.filter(item -> (item instanceof Article || item instanceof Feature || item instanceof StoreLatestApps || item instanceof App))
				.map(item -> itemToDisplayable(item, dateCalculator, spannableFactory))
				.buffer(2, TimeUnit.SECONDS, SEARCH_LIMIT)
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(item -> removeLoading())
				.subscribe(displayables -> addDisplayables((List<Displayable>) displayables),
						throwable -> finishLoading((Throwable) throwable));
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

	private void setOffset(GetUserTimeline item) {
		offset = item.getDatalist().getNext();
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
	private Displayable itemToDisplayable(Object item, DateCalculator dateCalculator, SpannableFactory
			spannableFactory) {

		if (item instanceof Article) {
			return ArticleDisplayable.from((Article) item, dateCalculator, spannableFactory);
		} else if (item instanceof Feature) {
			return FeatureDisplayable.from((Feature) item, dateCalculator);
		} else if (item instanceof StoreLatestApps) {
			return StoreLatestAppsDisplayable.from((StoreLatestApps) item, dateCalculator);
		} else if (item instanceof App) {
			return AppUpdateDisplayable.fromApp((App) item, spannableFactory);
		}
		throw new IllegalArgumentException("Only articles, features, store latest apps and app updates supported.");
	}
}
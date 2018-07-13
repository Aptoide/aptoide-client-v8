package cm.aptoide.pt.viewRateAndCommentReviews;

import android.os.Bundle;
import android.support.annotation.NonNull;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListReviewsRequest;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.util.schedulers.SchedulerProvider;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.view.View;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

// FIXME: 17/11/2016 sithengineer missing the dependency injection of the data providers
public class RateAndReviewsPresenter implements Presenter {

    @NonNull
    private final RateAndReviewsView view;
    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final ListReviewsRequest request;
    @NonNull
    private final GetAppRequest ratingRequest;

    @NonNull
    private CompositeSubscription subscriptions;

    public RateAndReviewsPresenter(@NonNull long appId, @NonNull String storeName,
                                   @NonNull String packageName, @NonNull RateAndReviewsView view,
                                   @NonNull SchedulerProvider schedulerProvider) {
        this.view = view;
        this.schedulerProvider = schedulerProvider;

        String aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                DataProvider.getContext()).getUniqueIdentifier();

        request = ListReviewsRequest.of(storeName, packageName, AptoideAccountManager.getAccessToken(),
                aptoideClientUUID);


        ratingRequest =
                GetAppRequest.of(appId, AptoideAccountManager.getAccessToken(), aptoideClientUUID,
                        packageName);

        subscriptions = new CompositeSubscription();
    }

    @Override
    public void present() {
        view.getLifecycle()
                .filter(event -> event.equals(View.LifecycleEvent.RESUME))
                .flatMap(resume -> Observable.merge(showReviews(), showRating()))
                //.subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY_VIEW))
                .subscribe(aVoid -> {
                }, err -> {
                    view.showError(err);
                    CrashReport.getInstance().log(err);
                });

        view.nextReviews().flatMap(offset -> {
            ListReviewsRequest.Body body = request.getBody();
            body.setOffset(offset);
            return request.observe().observeOn(schedulerProvider.ui()).doOnNext(response -> {
                if (response.isOk()) {
                    view.showNextReviews(0, response.getDatalist().getList());
                } else {
                    // TODO: 17/11/2016 sithengineer improve this exception
                    IllegalStateException exception = new IllegalStateException("Unexpected response");
                    view.showError(exception);
                    CrashReport.getInstance().log(exception);
                }
            });
        }).compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY_VIEW)).subscribe(aVoid -> {
        }, err -> {
            view.showError(err);
            CrashReport.getInstance().log(err);
        });
    }

    @Override
    public void saveState(Bundle state) {

    }

    @Override
    public void restoreState(Bundle state) {

    }



    @NonNull
    private Observable<Object> showReviews() {
        return request.observe().observeOn(schedulerProvider.ui()).doOnNext(response -> {
            if (response.isOk()) {
                view.showNextReviews(0, response.getDatalist().getList());
            } else {
                // TODO: 17/11/2016 sithengineer improve this exception
                IllegalStateException exception = new IllegalStateException("Unexpected response");
                view.showError(exception);
                CrashReport.getInstance().log(exception);
            }
        }).map(response -> null);
    }

    @NonNull
    private Observable<Object> showRating() {
        return ratingRequest.observe().observeOn(schedulerProvider.ui()).doOnNext(response -> {
            if (response.isOk()) {
                view.showRating(response.getNodes().getMeta().getData().getStats().getRating());
            } else {
                // TODO: 17/11/2016 sithengineer improve this exception
                IllegalStateException exception = new IllegalStateException("Unexpected response");
                view.showError(exception);
                CrashReport.getInstance().log(exception);
            }
        }).map(response -> null);
    }
}

package cm.aptoide.pt.analytics.analytics;

import rx.Single;

public interface AnalyticsBodyInterceptor<T> {

  Single<T> intercept(T body);
}

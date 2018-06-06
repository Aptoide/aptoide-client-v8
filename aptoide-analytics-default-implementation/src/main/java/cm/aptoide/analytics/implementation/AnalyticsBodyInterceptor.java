package cm.aptoide.analytics.implementation;

import rx.Single;

public interface AnalyticsBodyInterceptor<T> {

  Single<T> intercept(T body);
}
